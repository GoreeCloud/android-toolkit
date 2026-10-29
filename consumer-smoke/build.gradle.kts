plugins {
    id("com.android.application")
}

// Internal compile-only fixture; not a distributed application.
android {
    namespace = "com.goreecloud.android.toolkit.smoke"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.goreecloud.android.toolkit.smoke"
        minSdk = 26
        targetSdk = 35
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation(project(":android-kit"))
}
