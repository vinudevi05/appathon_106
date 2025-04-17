plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.appathon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appathon"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Appcompat library for backward compatibility
    implementation(libs.appcompat)

    // Material Design components
    implementation(libs.material)

    // Android activity components
    implementation(libs.activity)

    // Networking library (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // AndroidX Libraries
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Credentials and Google Identity for sign-in
    // Replace with latest version
    implementation ("com.google.android.gms:play-services-auth:21.3.0")// Update to latest version if needed

    // Layout and UI components
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Unit tests
    testImplementation(libs.junit)

    // Android tests
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
