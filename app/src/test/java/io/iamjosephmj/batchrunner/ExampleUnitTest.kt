package io.iamjosephmj.batchrunner

import me.iamjosephmj.batchrunner.test.BatchRunner
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(BatchRunner::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}