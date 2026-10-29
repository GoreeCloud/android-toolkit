plugins {
    id("com.android.library")
}

group = "com.goreecloud.android"
version = "0.1.0-dev"

android {
    namespace = "com.goreecloud.android.kit"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":sdk-core"))
}
