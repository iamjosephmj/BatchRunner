plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "io.iamjosephmj.batchrunner"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {

    implementation("androidx.core:core-ktx:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

}