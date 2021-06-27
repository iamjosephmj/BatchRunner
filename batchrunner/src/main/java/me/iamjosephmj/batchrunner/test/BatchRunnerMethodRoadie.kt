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

package me.iamjosephmj.batchrunner.test

import org.junit.internal.AssumptionViolatedException
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.TestTimedOutException
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

open class BatchRunnerMethodRoadie(
    private val test: Any,
    private val notifier: RunNotifier,
    private val description: Description,
    var runnerTestMethod: BatchRunnerTestMethod
) {

    fun run() {
        if (runnerTestMethod.isIgnored()) {
            notifier.fireTestIgnored(description)
            return
        }
        notifier.fireTestStarted(description)
        try {
            val timeout = runnerTestMethod.getTimeout()
            if (timeout > 0) {
                runWithTimeout(timeout)
            } else {
                runTest()
            }
        } finally {
            notifier.fireTestFinished(description)
        }
    }

    private fun runWithTimeout(timeout: Long) {
        runBeforesThenTestThenAfters {
            val service = Executors.newSingleThreadExecutor()
            val callable: Callable<Any> = Callable {
                runTestMethod()
                null
            }
            val result = service.submit(callable)
            service.shutdown()
            try {
                val terminated = service.awaitTermination(
                    timeout,
                    TimeUnit.MILLISECONDS
                )
                if (!terminated) {
                    service.shutdownNow()
                }
                result[0, TimeUnit.MILLISECONDS] // throws the exception if one occurred during the invocation
            } catch (e: TimeoutException) {
                addFailure(
                    TestTimedOutException(
                        timeout,
                        TimeUnit.MILLISECONDS
                    )
                )
            } catch (e: Exception) {
                addFailure(e)
            }
        }
    }

    private fun runTest() {
        runBeforesThenTestThenAfters { runTestMethod() }
    }

    private fun runBeforesThenTestThenAfters(test: Runnable) {
        try {
            runBefores()
            test.run()
        } catch (e: BatchRunnerFailedBefore) {
        } catch (e: Exception) {
            throw RuntimeException("test should never throw an exception to this level")
        } finally {
            runAfters()
        }
    }

    private fun runTestMethod() {
        try {
            runnerTestMethod.invoke(test)
            if (runnerTestMethod.expectsException()) {
                addFailure(AssertionError("Expected exception: " + runnerTestMethod.getExpectedException()?.simpleName))
            }
        } catch (e: InvocationTargetException) {
            val actual = e.targetException
            if (actual is AssumptionViolatedException) {
                return
            } else if (!runnerTestMethod.expectsException()) {
                addFailure(actual)
            } else if (runnerTestMethod.isUnexpected(actual)) {
                val message =
                    ("Unexpected exception, expected<" + runnerTestMethod.getExpectedException()?.simpleName + "> but was<"
                            + actual.javaClass.name + ">")
                addFailure(Exception(message, actual))
            }
        } catch (e: Throwable) {
            addFailure(e)
        }
    }

    private fun runBefores() {
        try {
            try {
                runnerTestMethod.getBefores().let {
                    for (before in it) {
                        before?.invoke(test)
                    }
                }

            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        } catch (e: AssumptionViolatedException) {
            throw BatchRunnerFailedBefore()
        } catch (e: Throwable) {
            addFailure(e)
            throw BatchRunnerFailedBefore()
        }
    }

    private fun runAfters() {
        runnerTestMethod.getAfters().let {
            for (after in it) {
                try {
                    after?.invoke(test)
                } catch (e: InvocationTargetException) {
                    addFailure(e.targetException)
                } catch (e: Throwable) {
                    addFailure(e) // Untested, but seems impossible
                }
            }
        }

    }

    private fun addFailure(e: Throwable?) {
        notifier.fireTestFailure(Failure(description, e))
    }
}