/*
 * MIT License
 *
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

package me.iamjosephmj.batchrunner

import kotlinx.coroutines.*
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.manipulation.*
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

open class BatchRunner(klass: Class<*>) :
    Runner(),
    Filterable,
    Sortable {

    private var testMethods: MutableList<Method>
    private var runnerTestClass: BatchRunnerTest = BatchRunnerTest(klass)
    private val testClassObject = createTest()

    init {
        testMethods = getTestMethods()
        validate()
    }

    private fun getTestMethods(): MutableList<Method> {
        return runnerTestClass.getTestMethods()
    }

    private fun validate() {
        val methodValidator = BatchRunnerMethodValidator(runnerTestClass)
        methodValidator.validateMethodsForDefaultRunner()
        methodValidator.assertValid()
    }

    override fun run(notifier: RunNotifier) {
        BatchRunnerClassRoadie(notifier, runnerTestClass, description) {
            runMethods(notifier)
        }.runProtected()

    }


    private fun runMethods(notifier: RunNotifier) {
        runBlocking {
            val jobs: List<Job> = testMethods.map { method ->
                // 64 threads will be forked
                launch(context = Dispatchers.IO) {
                    invokeTestMethod(method, notifier)
                }
            }

            jobs.joinAll()
        }

    }

    override fun getDescription(): Description {
        val spec = Description.createSuiteDescription(getName(), *classAnnotations())
        val testMethods: List<Method> = testMethods
        for (method in testMethods) {
            spec.addChild(methodDescription(method))
        }
        return spec
    }

    private fun classAnnotations(): Array<out Annotation> {
        return runnerTestClass.javaClass.annotations
    }

    private fun getName(): String? {
        return getTestClass().getName()
    }

    private fun createTest(): Any {
        return getTestClass().getConstructor().newInstance()
    }

    private fun invokeTestMethod(method: Method, notifier: RunNotifier) {
        val description = methodDescription(method)
        try {
            testClassObject
        } catch (e: InvocationTargetException) {
            testAborted(notifier, description, e.cause)
            return
        } catch (e: Exception) {
            testAborted(notifier, description, e)
            return
        }
        val testMethod = wrapMethod(method)
        BatchRunnerMethodRoadie(testClassObject, notifier, description, testMethod).run()
    }

    private fun testAborted(
        notifier: RunNotifier,
        description: Description,
        e: Throwable?
    ) {
        notifier.fireTestStarted(description)
        notifier.fireTestFailure(Failure(description, e))
        notifier.fireTestFinished(description)
    }

    private fun wrapMethod(method: Method): BatchRunnerTestMethod {
        return BatchRunnerTestMethod(method, runnerTestClass)
    }

    private fun testName(method: Method): String? {
        return method.name
    }

    private fun methodDescription(method: Method): Description {
        return Description.createTestDescription(
            getTestClass().javaClass,
            testName(method),
            *testAnnotations(method)
        )
    }

    private fun testAnnotations(method: Method): Array<Annotation?> {
        return method.annotations
    }

    override fun filter(filter: Filter) {
        val iter = testMethods.iterator()
        while (iter.hasNext()) {
            val method = iter.next()
            if (!filter.shouldRun(methodDescription(method))) {
                iter.remove()
            }
        }
        if (testMethods.isEmpty()) {
            throw NoTestsRemainException()
        }
    }

    override fun sort(sorter: Sorter) {
        testMethods.sortWith { o1, o2 ->
            sorter.compare(
                methodDescription(o1),
                methodDescription(o2)
            )
        }
    }

    private fun getTestClass(): BatchRunnerTest {
        return runnerTestClass
    }


}