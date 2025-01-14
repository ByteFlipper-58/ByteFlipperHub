plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.byteflipper.hub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.byteflipper.hub"
        minSdk = 28
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    val ackpineVersion = "0.8.3"
    implementation("ru.solrudev.ackpine:ackpine-core:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-ktx:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-splits:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-assets:$ackpineVersion")

    implementation("androidx.work:work-runtime:2.7.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}