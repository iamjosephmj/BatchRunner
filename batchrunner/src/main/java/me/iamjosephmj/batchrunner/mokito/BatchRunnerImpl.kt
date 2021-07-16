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

package me.iamjosephmj.batchrunner.mokito

import me.iamjosephmj.batchrunner.test.BatchRunner
import org.junit.runner.Description
import org.junit.runner.manipulation.Filter
import org.junit.runner.notification.RunNotifier
import org.mockito.MockitoAnnotations
import org.mockito.internal.runners.RunnerImpl
import org.mockito.internal.runners.util.FrameworkUsageValidator

/**
 * Runner Impl is the coordinator class for mock tests.
 *
 * @author Joseph James.
 */
class BatchRunnerImpl(klass: Class<*>) : RunnerImpl {

    var runner: BatchRunner = object : BatchRunner(klass) {
        override fun createTest(): Any {
            val test = super.createTest()
            MockitoAnnotations.initMocks(test)
            return test
        }
    }

    override fun run(notifier: RunNotifier) {
        // add listener that validates framework usage at the end of each test
        notifier.addListener(FrameworkUsageValidator(notifier))
        runner.run(notifier)
    }

    override fun getDescription(): Description {
        return runner.description
    }

    override fun filter(filter: Filter) {
        runner.filter(filter)
    }

}