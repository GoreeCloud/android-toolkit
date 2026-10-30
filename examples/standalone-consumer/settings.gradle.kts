pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// This is a separate Gradle build, not a module of the toolkit project.
rootProject.name = "goreecloud-toolkit-independent-consumer"
include(":app")

// Resolve a declared module coordinate from the checked-out source build,
// without pretending the library has been published to a remote Maven repo.
includeBuild("../..") {
    dependencySubstitution {
        substitute(module("com.goreecloud.android:android-kit")).using(project(":android-kit"))
    }
}
