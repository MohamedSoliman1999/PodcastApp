plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}

android {
    namespace = "com.podcast.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.podcast.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.podcast.app.HiltTestRunner"
        vectorDrawables.useSupportLibrary = true

        // API base URLs
        buildConfigField("String", "HOME_API_URL", "\"https://api-v2-b2sit6oh3a-uc.a.run.app/\"")
        buildConfigField("String", "SEARCH_API_URL", "\"https://mock.apidog.com/m1/735111-711675-default/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // JUnit 5 support
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()
            }
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    // ─── Compose BOM ─────────────────────────────────────────────────────────
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // ─── Compose UI ──────────────────────────────────────────────────────────
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // ─── AndroidX ────────────────────────────────────────────────────────────
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.appcompat)
    implementation(libs.core.splashscreen)
    // ─── Dependency Injection (Hilt) ─────────────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ─── Networking ──────────────────────────────────────────────────────────
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // ─── Image Loading ───────────────────────────────────────────────────────
    implementation(libs.coil.compose)

    // ─── Coroutines ──────────────────────────────────────────────────────────
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // ─── Unit Testing ────────────────────────────────────────────────────────
    testImplementation(libs.junit5.api)
    testImplementation(libs.junit5.params)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)

    // ─── Instrumented Testing ────────────────────────────────────────────────
    androidTestImplementation(libs.junit.android)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android)
    kspAndroidTest(libs.hilt.compiler)
//     Hilt Instrumentation Testing (UI Tests)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.testing.compiler) // or kspAndroidTest(...)
    // ─── Media3 (ExoPlayer + MediaSession notification) ──────────────────────
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)
    // ─── DataStore (persist settings)
    implementation(libs.datastore.preferences)
}
