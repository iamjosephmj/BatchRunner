plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = App.compileSDK
    buildToolsVersion = App.buildVersion

    defaultConfig {
        applicationId = App.appId
        minSdk = App.minSDK
        targetSdk = App.targetSDK
        versionCode = Jitpack.versionCode
        versionName = Jitpack.versionName

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

dependencies {

    getAppDependencies()
    testImplementation("com.github.iamjosephmj:BatchRunner:1.0.1")
}