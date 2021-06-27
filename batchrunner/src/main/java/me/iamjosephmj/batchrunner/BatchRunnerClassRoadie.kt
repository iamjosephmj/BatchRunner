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