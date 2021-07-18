plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}



android {
    compileSdk = App.compileSDK
    buildToolsVersion = App.buildVersion

    defaultConfig {
        minSdk = App.minSDK
        targetSdk = App.targetSDK
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
            create<MavenPublication>(Jitpack.release) {
                from(components.getByName(Jitpack.release))
                groupId = Jitpack.groupId
                artifactId = Jitpack.artifactId
                version = Jitpack.version

            }
            create<MavenPublication>(Jitpack.debug) {
                from(components.getByName(Jitpack.debug))
                groupId = Jitpack.groupId
                artifactId = Jitpack.artifactId
                version = Jitpack.version
            }
        }
    }
}


dependencies {
    getBatchDependencies()
}