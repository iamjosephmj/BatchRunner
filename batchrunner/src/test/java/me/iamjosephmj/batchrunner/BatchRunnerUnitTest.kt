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
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(BatchRunner::class)
class BatchRunnerUnitTest {

    var delay by Delegates.notNull<Long>()
    private val assertionExpression = true


    @Before
    fun setup() {
        delay = 1000
    }

    @Test
    fun test_1() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_2() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_3() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_4() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_5() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_6() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_7() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_8() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_9() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_10() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_11() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_12() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_13() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_14() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_15() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_16() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_17() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_18() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_19() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_20() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_21() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_22() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_23() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_24() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_25() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_26() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_27() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_28() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_29() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_30() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_31() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_32() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_33() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_34() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_35() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_36() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_37() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_38() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_39() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_40() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }


    @Test
    fun test_41() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_42() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_43() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_44() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_45() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_46() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_47() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_48() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_49() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @Test
    fun test_50() {
        Thread.sleep(delay)
        assert(assertionExpression)
    }

    @After
    fun tearDown() {

    }

    companion object {
        @AfterClass
        @JvmStatic
        fun afterClass() {
            println("end = ${System.currentTimeMillis()}")
        }

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            System.setOut(System.out)
            println("start = ${System.currentTimeMillis()}")
        }
    }
}