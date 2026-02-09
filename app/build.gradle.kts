plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.me.gcu.dramwise"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.me.gcu.dramwise"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Your existing catalog deps
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --- Added: Navigation (BottomNav + NavHost) ---
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // --- Added: Room (Database) ---
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // --- Added: Lifecycle (often used with Fragments/ViewModel) ---
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}