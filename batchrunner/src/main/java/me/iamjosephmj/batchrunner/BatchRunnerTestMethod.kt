package me.iamjosephmj.batchrunner

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class BatchRunnerTestMethod(
    private val method: Method,
    private var runnerTestClass: BatchRunnerTest
) {

    fun isIgnored(): Boolean {
        return method.getAnnotation(Ignore::class.java) != null
    }

    fun getTimeout(): Long {
        val annotation = method.getAnnotation(Test::class.java) ?: return 0
        return annotation.timeout
    }

    fun getExpectedException(): KClass<out Throwable>? {
        val annotation = method.getAnnotation(Test::class.java)
        return if (annotation == null || annotation.expected == Test.None::class) {
            null
        } else {
            annotation.expected
        }
    }

    fun isUnexpected(exception: Throwable): Boolean {
        return getExpectedException()?.isSuperclassOf(exception::class) == false
    }

    fun expectsException(): Boolean {
        return getExpectedException() != null
    }

    fun getBefores(): List<Method?> {
        return runnerTestClass.getAnnotatedMethods(Before::class)
    }

    fun getAfters(): List<Method?> {
        return runnerTestClass.getAnnotatedMethods(After::class)
    }

    operator fun invoke(test: Any) {
        method.invoke(test)
    }
}