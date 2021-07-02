/*
 * MIT License
 * Copyright (c) 2021 Joseph James
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.iamjosephmj.batchrunner.suite

import kotlinx.coroutines.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.internal.AssumptionViolatedException
import org.junit.internal.runners.model.EachTestNotifier
import org.junit.internal.runners.rules.RuleMemberValidator
import org.junit.internal.runners.statements.RunAfters
import org.junit.internal.runners.statements.RunBefores
import org.junit.rules.RunRules
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.manipulation.*
import org.junit.runner.notification.RunNotifier
import org.junit.runner.notification.StoppedByUserException
import org.junit.runners.model.InitializationError
import org.junit.runners.model.Statement
import org.junit.runners.model.TestClass
import org.junit.validator.AnnotationsValidator
import org.junit.validator.PublicClassValidator
import java.util.*

/**
 * Provides most of the functionality specific to a Runner that implements a
 * "parent node" in the test tree, with children defined by objects of some data
 * type `T`. (For [org.junit.runners.BlockJUnit4ClassRunner], `T` is
 * [Method] . For [BatchSuite], `T` is [Class].) Subclasses
 * must implement finding the children of the node, describing each child, and
 * running each child. ParentRunner will filter and sort children, handle
 * `@BeforeClass` and `@AfterClass` methods,
 * handle annotated [ClassRule]s, create a composite
 * [Description], and run children sequentially.
 *
 * @author Joseph James.
 */
abstract class BatchParentRunner<T> protected constructor(testClass: Class<*>?) : Runner(),
    Filterable,
    Sortable {
    private val childrenLock = Any()

    /**
     * Returns a [TestClass] object wrapping the class to be executed.
     */
    private val testClass: TestClass

    // Guarded by childrenLock
    @Volatile
    private var filteredChildren: Collection<T>? = null


    protected fun createTestClass(testClass: Class<*>?): TestClass {
        return TestClass(testClass)
    }

    //
    // Must be overridden
    //

    /**
     * Returns a list of objects that define the children of this Runner.
     */
    protected abstract fun getChildren(): List<T>

    /**
     * Returns a [Description] for `child`, which can be assumed to
     * be an element of the list returned by [BatchParentRunner.getChildren]
     */
    protected abstract fun describeChild(child: T): Description?

    /**
     * Runs the test corresponding to `child`, which can be assumed to be
     * an element of the list returned by [BatchParentRunner.getChildren].
     * Subclasses are responsible for making sure that relevant test events are
     * reported through `notifier`
     */
    protected abstract fun runChild(child: T, notifier: RunNotifier?)
    //
    // May be overridden
    //
    /**
     * Adds to `errors` a throwable for each problem noted with the test class (available from [.getTestClass]).
     * Default implementation adds an error for each method annotated with
     * `@BeforeClass` or `@AfterClass` that is not
     * `public static void` with no arguments.
     */
    protected fun collectInitializationErrors(errors: MutableList<Throwable>) {
        validatePublicVoidNoArgMethods(BeforeClass::class.java, true, errors)
        validatePublicVoidNoArgMethods(AfterClass::class.java, true, errors)
        validateClassRules(errors)
        applyValidators(errors)
    }

    private fun applyValidators(errors: MutableList<Throwable>) {
        if (testClass.javaClass != null) {
            for (each in VALIDATORS) {
                errors.addAll(each.validateTestClass(testClass))
            }
        }
    }

    /**
     * Adds to `errors` if any method in this class is annotated with
     * `annotation`, but:
     *
     *  * is not public, or
     *  * takes parameters, or
     *  * returns something other than void, or
     *  * is static (given `isStatic is false`), or
     *  * is not static (given `isStatic is true`).
     *
     */
    protected fun validatePublicVoidNoArgMethods(
        annotation: Class<out Annotation>,
        isMethodStatic: Boolean, errors: List<Throwable>
    ) {
        val methods = testClass.getAnnotatedMethods(annotation)
        for (eachTestMethod in methods) {
            eachTestMethod.validatePublicVoidNoArg(isMethodStatic, errors)
        }
    }

    private fun validateClassRules(errors: List<Throwable?>) {
        RuleMemberValidator.CLASS_RULE_VALIDATOR.validate(testClass, errors)
        RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR.validate(
            testClass, errors
        )
    }

    /**
     * Constructs a `Statement` to run all of the tests in the test class.
     * Override to add pre-/post-processing. Here is an outline of the
     * implementation:
     *
     *  1. Determine the children to be run using [.getChildren]
     * (subject to any imposed filter and sort).
     *  1. If there are any children remaining after filtering and ignoring,
     * construct a statement that will:
     *
     *  1. Apply all `ClassRule`s on the test-class and superclasses.
     *  1. Run all non-overridden `@BeforeClass` methods on the test-class
     * and superclasses; if any throws an Exception, stop execution and pass the
     * exception on.
     *  1. Run all remaining tests on the test-class.
     *  1. Run all non-overridden `@AfterClass` methods on the test-class
     * and superclasses: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * [org.junit.runners.model.MultipleFailureException].
     *
     *
     *
     *
     * @return `Statement`
     */
    protected fun classBlock(notifier: RunNotifier?): Statement {
        var statement = childrenInvoker(notifier)
        if (!areAllChildrenIgnored()) {
            statement = withBeforeClasses(statement)
            statement = withAfterClasses(statement)
            statement = withClassRules(statement)
        }
        return statement
    }

    private fun areAllChildrenIgnored(): Boolean {
        for (child in getFilteredChildren()!!) {
            if (!isIgnored(child)) {
                return false
            }
        }
        return true
    }

    /**
     * Returns a [Statement]: run all non-overridden `@BeforeClass` methods on this class
     * and superclasses before executing `statement`; if any throws an
     * Exception, stop execution and pass the exception on.
     */
    protected fun withBeforeClasses(statement: Statement): Statement {
        val befores = testClass
            .getAnnotatedMethods(BeforeClass::class.java)
        return if (befores.isEmpty()) statement else RunBefores(statement, befores, null)
    }

    /**
     * Returns a [Statement]: run all non-overridden `@AfterClass` methods on this class
     * and superclasses before executing `statement`; all AfterClass methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * [org.junit.runners.model.MultipleFailureException].
     */
    protected fun withAfterClasses(statement: Statement): Statement {
        val afters = testClass
            .getAnnotatedMethods(AfterClass::class.java)
        return if (afters.isEmpty()) statement else RunAfters(statement, afters, null)
    }

    /**
     * Returns a [Statement]: apply all
     * static fields assignable to [TestRule]
     * annotated with [ClassRule].
     *
     * @param statement the base statement
     * @return a RunRules statement if any class-level [TestRule]s are
     * found, or the base statement
     */
    private fun withClassRules(statement: Statement): Statement {
        val classRules = classRules()
        return if (classRules.isEmpty()) statement else RunRules(statement, classRules, description)
    }

    /**
     * @return the `ClassRule`s that can transform the block that runs
     * each method in the tested class.
     */
    protected fun classRules(): List<TestRule> {
        val result =
            testClass.getAnnotatedMethodValues(null, ClassRule::class.java, TestRule::class.java)
        result.addAll(
            testClass.getAnnotatedFieldValues(
                null,
                ClassRule::class.java,
                TestRule::class.java
            )
        )
        return result
    }

    /**
     * Returns a [Statement]: Call [.runChild]
     * on each object returned by [.getChildren] (subject to any imposed
     * filter and sort)
     */
    protected fun childrenInvoker(notifier: RunNotifier?): Statement {
        return object : Statement() {
            override fun evaluate() {
                runChildren(notifier)
            }
        }
    }

    /**
     * Evaluates whether a child is ignored. The default implementation always
     * returns `false`.
     *
     *
     * [org.junit.runners.BlockJUnit4ClassRunner], for example, overrides this method to
     * filter tests based on the [org.junit.Ignore] annotation.
     */
    protected fun isIgnored(child: T): Boolean {
        return false
    }

    private fun runChildren(notifier: RunNotifier?) {
        runBlocking {
            getFilteredChildren()?.let { children ->
                val jobs: List<Job> = children.map { each ->
                    launch(context = Dispatchers.Default) {
                        runChild(each, notifier)
                    }
                }
                jobs.joinAll()
            }

        }
    }

    /**
     * Returns a name used to describe this Runner
     */
    protected fun getName(): String {
        return testClass.name
    }
    //
    // Available for subclasses
    //
    /**
     * Runs a [Statement] that represents a leaf (aka atomic) test.
     */
    protected fun runLeaf(
        statement: Statement, description: Description?,
        notifier: RunNotifier?
    ) {
        val eachNotifier = EachTestNotifier(notifier, description)
        eachNotifier.fireTestStarted()
        try {
            statement.evaluate()
        } catch (e: AssumptionViolatedException) {
            eachNotifier.addFailedAssumption(e)
        } catch (e: Throwable) {
            eachNotifier.addFailure(e)
        } finally {
            eachNotifier.fireTestFinished()
        }
    }

    /**
     * @return the annotations that should be attached to this runner's
     * description.
     */
    protected fun getRunnerAnnotations(): Array<Annotation> {
        return testClass.annotations
    }

    /**
     * Implementation of Runner
     */
    override fun getDescription(): Description {
        val description = Description.createSuiteDescription(
            getName(),
            *getRunnerAnnotations()
        )
        getFilteredChildren()?.forEach { child ->
            description.addChild(describeChild(child))

        }

        return description
    }

    override fun run(notifier: RunNotifier) {
        val testNotifier = EachTestNotifier(
            notifier,
            description
        )
        try {
            val statement = classBlock(notifier)
            statement.evaluate()
        } catch (e: AssumptionViolatedException) {
            testNotifier.addFailedAssumption(e)
        } catch (e: StoppedByUserException) {
            throw e
        } catch (e: Throwable) {
            testNotifier.addFailure(e)
        }
    }

    //
    // Implementation of Filterable and Sortable
    //
    @Throws(NoTestsRemainException::class)
    override fun filter(filter: Filter) {
        synchronized(childrenLock) {
            val children: MutableList<T> = ArrayList(getFilteredChildren())
            val iter = children.iterator()
            while (iter.hasNext()) {
                val each = iter.next()
                if (shouldRun(filter, each)) {
                    try {
                        filter.apply(each)
                    } catch (e: NoTestsRemainException) {
                        iter.remove()
                    }
                } else {
                    iter.remove()
                }
            }
            filteredChildren = Collections.unmodifiableCollection(children)
            if ((filteredChildren as MutableCollection<T>?)?.isEmpty() == true) {
                throw NoTestsRemainException()
            }
        }
    }

    override fun sort(sorter: Sorter) {
        synchronized(childrenLock) {
            for (each in getFilteredChildren()!!) {
                sorter.apply(each)
            }
            val sortedChildren: List<T> = ArrayList(getFilteredChildren())
            Collections.sort(sortedChildren, comparator(sorter))
            filteredChildren = Collections.unmodifiableCollection(sortedChildren)
        }
    }

    //
    // Private implementation
    //
    private fun validate() {
        val errors: MutableList<Throwable> = ArrayList()
        collectInitializationErrors(errors)
        if (errors.isNotEmpty()) {
            throw InitializationError(errors)
        }
    }

    private fun getFilteredChildren(): Collection<T>? {
        if (filteredChildren == null) {
            synchronized(childrenLock) {
                if (filteredChildren == null) {
                    filteredChildren = Collections.unmodifiableCollection(getChildren())
                }
            }
        }
        return filteredChildren
    }

    private fun shouldRun(filter: Filter, each: T): Boolean {
        return filter.shouldRun(describeChild(each))
    }

    private fun comparator(sorter: Sorter): Comparator<in T> {
        return Comparator { o1, o2 -> sorter.compare(describeChild(o1), describeChild(o2)) }
    }

    companion object {
        private val VALIDATORS = listOf(
            AnnotationsValidator(), PublicClassValidator()
        )
    }

    /**
     * Constructs a new `ParentRunner` that will run `@TestClass`
     */
    init {
        this.testClass = createTestClass(testClass)
        validate()
    }
}