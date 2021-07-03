import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.getAppDependencies() {

    listOf(
        GroupDependencies.core,
        GroupDependencies.app,
        GroupDependencies.batchLib,
    ).forEach { item ->
        item.forEach { dependency ->
            addApi(dependency)
        }
    }

    GroupDependencies.test.forEach {
        addTest(it)
    }

    GroupDependencies.appUiTest.forEach {
        addAndroidTest(it)
    }
}

fun DependencyHandler.getBatchDependencies() {
    GroupDependencies.batch.forEach {
        if (it is String)
            addApi(it)
        else if (it is List<*>) {
            it.forEach { subDependency ->
                if (subDependency is String)
                    addApi(subDependency)
            }
        }
    }

    GroupDependencies.test.forEach {
        addTest(it)
    }
}


fun DependencyHandler.addApi(dependency: String): Dependency? {
    return add("api", dependency)
}

fun DependencyHandler.addTest(dependency: String): Dependency? {
    return add("testApi", dependency)
}

fun DependencyHandler.addAndroidTest(dependency: String): Dependency? {
    return add("androidTestApi", dependency)
}
