plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.luckycharmfairy.luckycharmfairy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.luckycharmfairy.luckycharmfairy"
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
    implementation(libs.androidx.material3.v110)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.play.services.ads.lite)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.appcompat)
    implementation (libs.androidx.cardview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp)

    // Firebase BOM을 통해 버전 관리
    implementation(platform(libs.google.firebase.bom)) // BOM 버전 추가
    implementation (libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.google.firebase.firestore.ktx)
    implementation(libs.google.firebase.storage.ktx)

    implementation(libs.material.calendarview)
    implementation(libs.coil.kt.coil)
    implementation(libs.retrofit2.retrofit)

    // Firebase UI 관련 의존성
    implementation(libs.firebaseui.firebase.ui.auth) // 버전 확인 필요
    // implementation(libs.firebaseui.firebase.ui.database) // 필요시 추가
    implementation (libs.firebase.bom.v3210)
    implementation (libs.com.google.firebase.firebase.auth.ktx)
    implementation (libs.play.services.auth.v2050)

    // 그래프
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
}

