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

import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.internal.MethodSorter
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KClass

class BatchRunnerTest(private val klass: Class<*>) {

    fun getTestMethods(): MutableList<Method> {
        val methods = getAnnotatedMethods(Test::class)
        return if (methods.isNullOrEmpty()) {
            mutableListOf()
        } else {
            methods as MutableList<Method>
        }

    }

    fun getBefores(): List<Method> {
        return getAnnotatedMethods(BeforeClass::class)
    }

    fun getAfters(): List<Method> {
        return getAnnotatedMethods(AfterClass::class)
    }

    fun getAnnotatedMethods(annotationClass: KClass<out Annotation>): List<Method> {
        val results: MutableList<Method> = ArrayList()
        for (eachClass in getSuperClasses(klass)) {
            val methods = MethodSorter.getDeclaredMethods(eachClass)
            for (eachMethod in methods) {
                val annotation = eachMethod.getAnnotation(annotationClass.java)
                if (annotation != null && !isShadowed(eachMethod, results)) {
                    results.add(eachMethod)
                }
            }
        }
        if (runsTopToBottom(annotationClass.java)) {
            results.reverse()
        }
        return results
    }

    private fun runsTopToBottom(annotation: Class<out Annotation>): Boolean {
        return annotation == Before::class.java || annotation == BeforeClass::class.java
    }

    private fun isShadowed(method: Method, results: List<Method?>): Boolean {
        for (each in results) {
            if (isShadowed(method, each)) {
                return true
            }
        }
        return false
    }

    private fun isShadowed(current: Method, previous: Method?): Boolean {
        if (previous!!.name != current.name) {
            return false
        }
        if (previous.parameterTypes.size != current.parameterTypes.size) {
            return false
        }
        for (i in previous.parameterTypes.indices) {
            if (previous.parameterTypes[i] != current.parameterTypes[i]) {
                return false
            }
        }
        return true
    }

    private fun getSuperClasses(testClass: Class<*>?): List<Class<*>?> {
        val results: MutableList<Class<*>?> = ArrayList()
        var current = testClass
        while (current != null) {
            results.add(current)
            current = current.superclass
        }
        return results
    }

    fun getConstructor(): Constructor<*> {
        return klass.getConstructor()
    }

    fun getName(): String? {
        return klass.name
    }
}