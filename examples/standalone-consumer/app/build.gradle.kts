plugins {
    id("com.android.application")
}

android {
    namespace = "com.goreecloud.android.toolkit.independent"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.goreecloud.android.toolkit.independent"
        minSdk = 26
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // The separate Gradle root resolves this through an explicit composite,
    // not an invented published Maven coordinate.
    implementation("com.goreecloud.android:android-kit:0.1.0-dev")
}
