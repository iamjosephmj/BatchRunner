import com.android.build.gradle.internal.errors.DeprecationReporterImpl.Companion.clean
import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.type

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}