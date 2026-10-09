plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "io.github.mdraihan27.routinemanager"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.github.mdraihan27.routinemanager"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.rxjava3)
    annotationProcessor(libs.room.compiler)

    implementation(libs.rxjava3)
    implementation(libs.rxandroid)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.rxjava3)

    implementation(libs.dynamicanimation)

    implementation(libs.work.runtime)
    implementation(libs.work.rxjava3)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.common.java8)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.core.testing)

    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}