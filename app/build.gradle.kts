plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.faceimage"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.faceimage"
        minSdk = 24
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
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
//        jvmTarget = "1.8"
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    // Use this dependency to bundle the model with your app
    implementation("com.google.mlkit:face-detection:16.1.5")
    // Use this dependency to use the dynamically downloaded model in Google Play Services
//    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    var arch_version = "2.7.0-alpha01"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$arch_version")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$arch_version")

    implementation ("androidx.exifinterface:exifinterface:1.3.3")

    // Room
    val roomVersion = "2.5.2"
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
//    kapt ("androidx.room:room-compiler:$roomVersion")

}