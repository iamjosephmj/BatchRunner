package io.iamjosephmj.batchrunner

import android.content.Context
import me.iamjosephmj.batchrunner.mokito.MockitoJUnitBatchRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitBatchRunner::class)
class MockitoBatchTest {

    @Mock
    private lateinit var context: Context

    @Before
    fun before() {
        `when`(context.isRestricted).thenReturn(true)
    }

    @Test
    fun mockTest1() {
        assert(context.isRestricted)
    }


}