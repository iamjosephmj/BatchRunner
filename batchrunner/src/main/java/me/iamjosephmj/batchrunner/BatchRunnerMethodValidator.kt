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

import org.junit.*
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KClass

class BatchRunnerMethodValidator(private val runnerTestClass: BatchRunnerTest) {

    private val errors: MutableList<Throwable> = ArrayList()

    private fun validateInstanceMethods() {
        validateTestMethods(After::class, false)
        validateTestMethods(Before::class, false)
        validateTestMethods(Test::class, false)
        runnerTestClass.getAnnotatedMethods(
            Test::class
        ).let { methodCollection ->
            if (methodCollection.isEmpty()) {
                errors.add(Exception("No runnable methods"))
            }
        }

    }

    private fun validateStaticMethods() {
        validateTestMethods(BeforeClass::class, true)
        validateTestMethods(AfterClass::class, true)
    }

    fun validateMethodsForDefaultRunner(): List<Throwable> {
        validateNoArgConstructor()
        validateStaticMethods()
        validateInstanceMethods()
        return errors
    }

    fun assertValid() {
        if (errors.isNotEmpty()) {
            throw InitializationError(errors)
        }
    }

    private fun validateNoArgConstructor() {
        try {
            runnerTestClass.getConstructor()
        } catch (e: Exception) {
            errors.add(Exception("Test class should have public zero-argument constructor", e))
        }
    }

    private fun validateTestMethods(
        annotation: KClass<out Annotation>,
        isStatic: Boolean
    ) {
        runnerTestClass.getAnnotatedMethods(annotation).let {
            for (each in it) {
                each.let { method ->
                    if (Modifier.isStatic(method.modifiers) != isStatic) {
                        val state = if (isStatic) "should" else "should not"
                        errors.add(
                            Exception(
                                "Method " + method.name + "() "
                                        + state + " be static"
                            )
                        )
                    }
                    if (!Modifier.isPublic(method.declaringClass.modifiers)) {
                        errors.add(
                            Exception(
                                ("Class " + method.declaringClass.name
                                        + " should be public")
                            )
                        )
                    }
                    if (!Modifier.isPublic(method.modifiers)) {
                        errors.add(
                            Exception(
                                ("Method " + method.name
                                        + " should be public")
                            )
                        )
                    }
                    if (method.returnType != Void.TYPE) {
                        errors.add(
                            Exception(
                                ("Method " + method.name
                                        + "should have a return type of void")
                            )
                        )
                    }
                    if (method.parameterTypes.isNotEmpty()) {
                        errors.add(
                            Exception(
                                ("Method " + method.name
                                        + " should have no parameters")
                            )
                        )
                    }
                }

            }

        }
    }
}