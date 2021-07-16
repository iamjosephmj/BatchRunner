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

import me.iamjosephmj.batchrunner.mokito.MockitoJUnitBatchRunner
import me.iamjosephmj.batchrunner.util.Util
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import kotlin.properties.Delegates

@RunWith(MockitoJUnitBatchRunner::class)
class MockitoJUnitBatchRunnerUnitTest {

    @Mock
    private lateinit var mockUtil: Util

    private val assertionExpression = true


    @Before
    fun setup() {
        `when`(mockUtil.getTime(1))
            .thenReturn(1000)
    }

    @Test
    fun test_C_1() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_2() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_3() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_4() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_5() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_6() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_7() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_8() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_9() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_10() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_11() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_12() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_13() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_14() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_15() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_16() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_17() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_18() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_19() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_20() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_21() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_22() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_23() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_24() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_25() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_26() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_27() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_28() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_29() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_30() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_31() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_32() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_33() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_34() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_35() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_36() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_37() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_38() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_39() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_40() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }


    @Test
    fun test_C_41() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_42() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_43() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_44() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_45() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_46() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_47() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_48() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_49() {
        Thread.sleep(mockUtil.getTime(1))
        assert(assertionExpression)
    }

    @Test
    fun test_C_50() {
        Thread.sleep(mockUtil.getTime(1))
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