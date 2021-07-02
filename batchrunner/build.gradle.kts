plugins {

    id("com.android.library")

    id("kotlin-android")

    id("maven-publish")
}



android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    subprojects {
        tasks.withType<Test> {
            maxParallelForks = Runtime.getRuntime().availableProcessors()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "me.iamjosephmj"
                artifactId = "batchrunner"
                version = "1.0.0"
                artifact("$buildDir/outputs/aar/${artifactId}-release.aar")
            }
            create<MavenPublication>("debug") {
                groupId = "me.iamjosephmj"
                artifactId = "batchrunner"
                version = "1.0.0"
                artifact("$buildDir/outputs/aar/${artifactId}-debug.aar")
            }
        }
    }
}


dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect:${Dependencies.reflect}")

    implementation("androidx.core:core-ktx:1.6.0")

    implementation("junit:junit:4.13.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

    testImplementation("junit:junit:4.13.2")


}