package me.iamjosephmj.batchrunner.extensions

import kotlinx.coroutines.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.FixMethodOrder
import org.junit.internal.AssumptionViolatedException
import org.junit.internal.Checks
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
import org.junit.runners.model.*
import org.junit.validator.AnnotationsValidator
import org.junit.validator.TestClassValidator
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

abstract class BatchParentRunner<T> : Runner, Filterable, Orderable {

    private val childrenLock: Lock = ReentrantLock()

    /**
     * Returns a [TestClass] object wrapping the class to be executed.
     */
    val testClass: TestClass

    // Guarded by childrenLock
    @Volatile
    private var filteredChildren: MutableList<T> = mutableListOf()

    /**
     * Constructs a new `ParentRunner` that will run `@TestClass`
     */
    protected constructor(testClass: Class<*>?) {
        this.testClass = TestClass(testClass)
        validate()
    }

    /**
     * Constructs a new `ParentRunner` that will run the `TestClass`.
     *
     * @since 4.13
     */
    protected constructor(testClass: TestClass) {
        this.testClass = Checks.notNull(testClass)
        validate()
    }

    /**
     * @since 4.12
     */
    @Deprecated(
        "Please use {@link #BatchParentRunner(org.junit.runners.model.TestClass)}.",
        ReplaceWith("TestClass(testClass)", "org.junit.runners.model.TestClass")
    )
    fun createTestClass(testClass: Class<*>?): TestClass {
        return TestClass(testClass)
    }

    /**
     * Returns a list of objects that define the children of this Runner.
     */
    protected abstract fun getChildren(): List<T>

    /**
     * Returns a [Description] for `child`, which can be assumed to
     * be an element of the list returned by [BatchParentRunner.getChildren]
     */
    protected abstract fun describeChild(child: T): Description

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
    protected open fun collectInitializationErrors(errors: MutableList<Throwable?>) {
        validatePublicVoidNoArgMethods(BeforeClass::class.java, true, errors)
        validatePublicVoidNoArgMethods(AfterClass::class.java, true, errors)
        validateClassRules(errors)
        applyValidators(errors)
    }

    private fun applyValidators(errors: MutableList<Throwable?>) {
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
        annotation: Class<out Annotation?>?,
        isStatic: Boolean, errors: List<Throwable?>?
    ) {
        val methods = testClass.getAnnotatedMethods(annotation)
        for (eachTestMethod in methods) {
            eachTestMethod.validatePublicVoidNoArg(isStatic, errors)
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
    protected fun classBlock(notifier: RunNotifier): Statement {
        var statement = childrenInvoker(notifier)
        if (!areAllChildrenIgnored()) {
            statement = withBeforeClasses(statement)
            statement = withAfterClasses(statement)
            statement = withClassRules(statement)
            statement = withInterruptIsolation(statement)
        }
        return statement
    }

    private fun areAllChildrenIgnored(): Boolean {
        for (child in getFilteredChildren()) {
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
     * and superclasses after executing `statement`; all AfterClass methods are
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
     * @return a RunRules statement if any class-level [Rule]s are
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
        val collector = ClassRuleCollector()
        testClass.collectAnnotatedMethodValues(
            null,
            ClassRule::class.java,
            TestRule::class.java,
            collector
        )
        testClass.collectAnnotatedFieldValues(
            null,
            ClassRule::class.java,
            TestRule::class.java,
            collector
        )
        return collector.orderedRules
    }

    /**
     * Returns a [Statement]: Call [.runChild]
     * on each object returned by [.getChildren] (subject to any imposed
     * filter and sort)
     */
    protected fun childrenInvoker(notifier: RunNotifier): Statement {
        return object : Statement() {
            override fun evaluate() {
                runChildren(notifier)
            }
        }
    }

    /**
     * @return a [Statement]: clears interrupt status of current thread after execution of statement
     */
    protected fun withInterruptIsolation(statement: Statement): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    statement.evaluate()
                } finally {
                    Thread.interrupted() // clearing thread interrupted status for isolation
                }
            }
        }
    }

    protected open fun isIgnored(child: T): Boolean {
        return false
    }

    /**
     * Evaluates whether a child is ignored. The default implementation always
     * returns `false`.
     *
     *
     * [BatchJunit4ClassRunner], for example, overrides this method to
     * filter tests based on the [Ignore] annotation.
     */
    private fun runChildren(notifier: RunNotifier) {
        runBlocking {
            val jobs: List<Job> = getFilteredChildren().map { method ->
                // 64 threads will be forked
                launch(context = Dispatchers.IO) {
                    runChild(method, notifier)
                }
            }

            jobs.joinAll()
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

    //
    // Implementation of Runner
    //
    override fun getDescription(): Description {
        val clazz = testClass.javaClass
        val description: Description
        // if subclass overrides `getName()` then we should use it
        // to maintain backwards compatibility with JUnit 4.12
        description = if (clazz == null || clazz.name != getName()) {
            Description.createSuiteDescription(getName(), *getRunnerAnnotations())
        } else {
            Description.createSuiteDescription(clazz, *getRunnerAnnotations())
        }
        for (child in getFilteredChildren()) {
            description.addChild(describeChild(child))
        }
        return description
    }

    override fun run(notifier: RunNotifier) {
        val testNotifier = EachTestNotifier(
            notifier,
            description
        )
        testNotifier.fireTestSuiteStarted()
        try {
            val statement = classBlock(notifier)
            statement.evaluate()
        } catch (e: AssumptionViolatedException) {
            testNotifier.addFailedAssumption(e)
        } catch (e: StoppedByUserException) {
            throw e
        } catch (e: Throwable) {
            testNotifier.addFailure(e)
        } finally {
            testNotifier.fireTestSuiteFinished()
        }
    }

    //
    // Implementation of Filterable and Sortable
    //
    @Throws(NoTestsRemainException::class)
    override fun filter(filter: Filter) {
        childrenLock.lock()
        try {
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
            filteredChildren = Collections.unmodifiableList(children)
            if (filteredChildren.isEmpty()) {
                throw NoTestsRemainException()
            }
        } finally {
            childrenLock.unlock()
        }
    }

    override fun sort(sorter: Sorter) {
        if (shouldNotReorder()) {
            return
        }
        childrenLock.lock()
        filteredChildren = try {
            for (each in getFilteredChildren()) {
                sorter.apply(each)
            }
            val sortedChildren: List<T> = ArrayList(getFilteredChildren())
            Collections.sort(sortedChildren, comparator(sorter))
            Collections.unmodifiableList(sortedChildren)
        } finally {
            childrenLock.unlock()
        }
    }

    /**
     * Implementation of [Orderable.order].
     *
     * @since 4.13
     */
    @Throws(InvalidOrderingException::class)
    override fun order(orderer: Orderer) {
        if (shouldNotReorder()) {
            return
        }
        childrenLock.lock()
        try {
            var children = getFilteredChildren()
            // In theory, we could have duplicate Descriptions. De-dup them before ordering,
            // and add them back at the end.
            val childMap: MutableMap<Description, MutableList<T>> = LinkedHashMap(
                children.size
            )
            for (child in children) {
                val description = describeChild(child)
                var childrenWithDescription = childMap[description]
                if (childrenWithDescription == null) {
                    childrenWithDescription = ArrayList(1)
                    childMap[description] = childrenWithDescription
                }
                childrenWithDescription.add(child)
                orderer.apply(child)
            }
            val inOrder = orderer.order(childMap.keys)
            children = ArrayList(children.size)
            for (description in inOrder) {
                children.addAll(childMap[description]!!)
            }
            filteredChildren = Collections.unmodifiableList(children)
        } finally {
            childrenLock.unlock()
        }
    }

    //
    // Private implementation
    //
    private fun shouldNotReorder(): Boolean {
        // If the test specifies a specific order, do not reorder.
        return description.getAnnotation(FixMethodOrder::class.java) != null
    }

    @Throws(InitializationError::class)
    private fun validate() {
        val errors: MutableList<Throwable?> = ArrayList()
        collectInitializationErrors(errors)
        if (!errors.isEmpty()) {
            throw InvalidTestClassError(testClass.javaClass, errors)
        }
    }

    private fun getFilteredChildren(): MutableList<T> {
        if (filteredChildren.isEmpty()) {
            childrenLock.lock()
            try {
                if (filteredChildren.isEmpty()) {
                    filteredChildren = Collections.unmodifiableList(
                        ArrayList(getChildren())
                    )
                }
            } finally {
                childrenLock.unlock()
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

    private class ClassRuleCollector : MemberValueConsumer<TestRule?> {
        val entries: MutableList<RuleContainer.RuleEntry> = ArrayList()
        override fun accept(member: FrameworkMember<*>, value: TestRule?) {
            val rule = member.getAnnotation(ClassRule::class.java)
            entries.add(
                RuleContainer.RuleEntry(
                    value, RuleContainer.RuleEntry.TYPE_TEST_RULE,
                    rule?.order
                )
            )
        }

        val orderedRules: List<TestRule>
            get() {
                Collections.sort(entries, RuleContainer.ENTRY_COMPARATOR)
                val result: MutableList<TestRule> = ArrayList(entries.size)
                for (entry in entries) {
                    result.add(entry.rule as TestRule)
                }
                return result
            }
    }

    companion object {
        private val VALIDATORS: List<TestClassValidator> = listOf(
            AnnotationsValidator()
        )
    }
}