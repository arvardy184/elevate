plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // alias(libs.plugins.relay) // Temporarily disabled untuk presentasi
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

hilt {
    enableAggregatingTask = false
}

android {
    namespace = "com.application.elevate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.application.elevate"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
            excludes += "**/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "**/META-INF/MANIFEST.MF"
            excludes += "**/META-INF/DEPENDENCIES"
            excludes += "**/META-INF/LICENSE"
            excludes += "**/META-INF/LICENSE.txt"
            excludes += "**/META-INF/NOTICE"
            excludes += "**/META-INF/NOTICE.txt"
        }
    }
}

// KAPT configuration
kapt {
    correctErrorTypes = true
    useBuildCache = true
}

// Fix JavaPoet version conflicts
configurations.all {
    resolutionStrategy {
        force("com.squareup:javapoet:1.13.0")
        force("com.google.dagger:hilt-android:2.47")
        force("com.google.dagger:hilt-compiler:2.47")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.7.8")
    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("androidx.compose.animation:animation:1.6.1")
    implementation ("androidx.navigation:navigation-compose:2.7.6")
    
    // Hilt Dependencies - Fixed versions
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-compiler:2.47")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // JavaPoet untuk fix compatibility
    kapt("com.squareup:javapoet:1.13.0")
    
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.32.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    
    // Room Database dependencies
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // PDF Generation dependencies
    implementation("com.tom-roush:pdfbox-android:2.0.27.0") {
        exclude(group = "org.bouncycastle")
    }
    implementation("androidx.activity:activity-ktx:1.8.2")

    implementation(libs.material.icons.extended)
    implementation(libs.identity.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

