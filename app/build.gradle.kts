plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.football"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.football"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["appName"] = "Football"

        buildConfigField("String", "APPMETRICA_API_KEY", "\"\"")
    }

    // BUILD TYPES
    buildTypes {
        debug {
            versionNameSuffix = "-DEBUG"
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("boolean", "USE_MOCK_DATA", "true")
            manifestPlaceholders["appName"] = "Football Dev"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "USE_MOCK_DATA", "false")
            manifestPlaceholders["appName"] = "Football"
        }
    }

    //  PRODUCT FLAVORS
    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "BASE_URL", "\"https://dev.api.sstats.net/\"")
            buildConfigField("boolean", "USE_MOCK_DATA", "true")
            manifestPlaceholders["appName"] = "Football Dev"
        }
        create("prod") {
            dimension = "environment"
            applicationIdSuffix = ""
            buildConfigField("String", "BASE_URL", "\"https://api.sstats.net/\"")
            buildConfigField("boolean", "USE_MOCK_DATA", "false")
            manifestPlaceholders["appName"] = "Football"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}



dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.work.runtime.ktx)

    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation(libs.androidx.compose.foundation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("io.appmetrica.analytics:analytics:7.7.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
}