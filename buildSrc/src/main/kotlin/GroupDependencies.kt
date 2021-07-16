object GroupDependencies {


    internal val core = listOf(
        "androidx.core:core-ktx:${Dependencies.ktx}"
    )

    internal val app = listOf(
        "com.google.android.material:material:${Dependencies.material}"
    )

    internal val test = listOf(
        "junit:junit:${Dependencies.junit}"
    )

    private val appTest = listOf(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.coroutines}",
        test
    )

    internal val appUiTest = listOf(
        "androidx.test.ext:junit:${Dependencies.androidJunit}"
    )

    internal val batch = listOf(
        core,
        appTest,
        test,
        "org.jetbrains.kotlin:kotlin-reflect:${Dependencies.reflect}",
        "org.mockito:mockito-core:${Dependencies.mockito}"
    )


}