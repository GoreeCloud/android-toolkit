plugins {
    `java-library`
}

group = "com.goreecloud.android"
version = "0.1.0-dev"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}

// Dependency-free deterministic regression suite. Not a substitute for device tests.
tasks.register<JavaExec>("verifyCore") {
    group = "verification"
    description = "Run the SDK core regression suite"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("com.goreecloud.android.core.CoreSelfTest")
}
