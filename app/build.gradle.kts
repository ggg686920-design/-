plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "app.nasma.keyboard"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "app.nasma.keyboard"
        // Preserve the original app's Android 6 support during recovery.
        minSdk = 23
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1-recovered"
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            // Deliberately unsigned. The original owner's private key is unavailable.
        }
    }
    lint {
        abortOnError = true
        warningsAsErrors = true
        // Keep the owner's API 35 baseline until a separately tested SDK migration.
        informational += "OldTargetApi"
    }
}

kotlin { jvmToolchain(17) }

dependencies {
    testImplementation("junit:junit:4.13.2")
}
