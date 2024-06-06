plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.kapt") version "2.0.0"
}

android {
    namespace = "com.example.pawsome"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pawsome"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

        implementation (libs.androidx.constraintlayout)
        implementation (libs.play.services.identity.v2000)
        implementation (libs.kotlinx.coroutines.play.services)
        implementation (libs.androidx.lifecycle.viewmodel.ktx)
        implementation (libs.androidx.lifecycle.livedata.ktx)
        implementation (libs.androidx.lifecycle.viewmodel.savedstate)
        implementation(libs.lifecycle.runtime.ktx)
        implementation(libs.play.services.auth)
        implementation(libs.identity.credential)
        implementation(libs.androidx.security.identity.credential)
        implementation(libs.androidx.security.crypto)
        implementation(libs.googleid)
        implementation(libs.play.services.auth)
        implementation(libs.credentials)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.play.services.base)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
