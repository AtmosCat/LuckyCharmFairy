plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-android")
    id("kotlin-parcelize")
//    id("com.google.gms.google-services") version "4.3.10"
}

android {
    namespace = "com.luckycharmfairy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.luckycharmfairy"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.benchmark.common)
    implementation(libs.play.services.ads.lite)
    implementation(libs.volley)
    testImplementation(libs.junit)
    implementation(libs.coil)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.lottie)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation (libs.material.calendarview)
    implementation (libs.coil.kt.coil)
    implementation (platform(libs.google.firebase.bom))
}