package me.iamjosephmj.batchrunner

import me.iamjosephmj.batchrunner.suite.BatchSuite
import org.junit.runner.RunWith


@RunWith(BatchSuite::class)
@BatchSuite.SuiteClasses(
    BatchRunnerUnitTest1::class,
    BatchRunnerUnitTest2::class
)
class BatchSuiteRunner