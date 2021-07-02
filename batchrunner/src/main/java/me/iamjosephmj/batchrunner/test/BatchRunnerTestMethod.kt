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