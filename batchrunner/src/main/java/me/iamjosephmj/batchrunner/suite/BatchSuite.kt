package me.iamjosephmj.batchrunner.suite

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.InitializationError
import org.junit.runners.model.RunnerBuilder
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*
import kotlin.reflect.KClass

/**
 * Using `Suite` as a runner allows you to manually
 * build a suite containing tests from many classes. It is the JUnit 4 equivalent of the JUnit 3.8.x
 * static [junit.framework.Test] `suite()` method. To use it, annotate a class
 * with `@RunWith(Suite.class)` and `@SuiteClasses({TestClass1.class, ...})`.
 * When you run this class, it will run all the tests in all the suite classes.
 *
 * @since 4.0
 */
open class BatchSuite protected constructor(klass: Class<*>?, runners: List<Runner>) :
    BatchParentRunner<Runner>(klass) {
    /**
     * The `SuiteClasses` annotation specifies the classes to be run when a class
     * annotated with `@RunWith(Suite.class)` is run.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
    @Inherited
    annotation class SuiteClasses(
        /**
         * @return the classes to be run
         */
        vararg val value: KClass<*>
    )

    private val runners: List<Runner> = Collections.unmodifiableList(runners) as List<Runner>

    /**
     * Called reflectively on classes annotated with `@RunWith(Suite.class)`
     *
     * @param klass   the root class
     * @param builder builds runners for classes in the suite
     */
    constructor(klass: Class<*>, builder: RunnerBuilder) : this(
        builder,
        klass,
        getAnnotatedClasses(klass)
    )

    /**
     * Call this when there is no single root class (for example, multiple class names
     * passed on the command line to [org.junit.runner.JUnitCore]
     *
     * @param builder builds runners for classes in the suite
     * @param classes the classes in the suite
     */
    constructor(builder: RunnerBuilder, classes: Array<KClass<*>>) : this(
        null,
        builder.runners(null, classes.map {
            it.java
        })
    )

    /**
     * Call this when the default builder is good enough. Left in for compatibility with JUnit 4.4.
     *
     * @param klass        the root of the suite
     * @param suiteClasses the classes in the suite
     */
    protected constructor(klass: Class<*>, suiteClasses: Array<out KClass<*>>) : this(
        AllDefaultPossibilitiesBuilder(true), klass, suiteClasses
    )


    /**
     * Call this when the default builder is good enough. Left in for compatibility with JUnit 4.4.
     *
     * @param klass        the root of the suite
     * @param suiteClasses the classes in the suite
     */
    protected constructor(klass: Class<*>?) : this(
        AllDefaultPossibilitiesBuilder(true), klass, arrayOf()
    )


    /**
     * Called by this class and subclasses once the classes making up the suite have been determined
     *
     * @param builder      builds runners for classes in the suite
     * @param klass        the root of the suite
     * @param suiteClasses the classes in the suite
     */
    protected constructor(
        builder: RunnerBuilder,
        klass: Class<*>?,
        suiteClasses: Array<out KClass<*>>
    ) : this(klass, builder.runners(klass, suiteClasses.map {
        it.java
    }))

    override fun getChildren(): List<Runner> {
        return runners
    }

    override fun describeChild(child: Runner): Description {
        return child.description
    }


    override fun runChild(runner: Runner, notifier: RunNotifier?) {
        runner.run(notifier)
    }

    companion object {
        /**
         * Returns an empty suite.
         */
        fun emptySuite(): Runner {
            return try {
                BatchSuite(null)
            } catch (e: InitializationError) {
                throw RuntimeException("This shouldn't be possible")
            }
        }

        private fun getAnnotatedClasses(klass: Class<*>): Array<out KClass<*>> {
            val annotation = klass.getAnnotation(SuiteClasses::class.java)
                ?: throw InitializationError(
                    String.format(
                        "class '%s' must have a SuiteClasses annotation",
                        klass.name
                    )
                )
            return annotation.value
        }
    }

}