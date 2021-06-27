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

import org.junit.internal.AssumptionViolatedException
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import java.lang.reflect.InvocationTargetException

open class BatchRunnerClassRoadie(
    private var notifier: RunNotifier,
    private var runnerTestClass: BatchRunnerTest,
    private var testClassObject: Any,
    private var description: Description,
    private val runnable: Runnable,
) {


    private fun runUnprotected() {
        runnable.run()
    }

    private fun addFailure(targetException: Throwable?) {
        notifier.fireTestFailure(Failure(description, targetException))
    }

    fun runProtected() {
        try {
            runBefores()
            runUnprotected()
        } catch (e: BatchRunnerFailedBefore) {
        } finally {
            runAfters()
        }
    }

    private fun runBefores() {
        try {
            try {
                val befores = runnerTestClass.getBefores()
                for (before in befores) {
                    before.invoke(testClassObject)
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
        val afters = runnerTestClass.getAfters()
        for (after in afters) {
            try {
                after.invoke(testClassObject)
            } catch (e: InvocationTargetException) {
                addFailure(e.targetException)
            } catch (e: Throwable) {
                addFailure(e) // Untested, but seems impossible
            }
        }
    }
}