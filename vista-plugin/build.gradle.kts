import org.gradle.api.tasks.Copy
import org.gradle.api.file.DuplicatesStrategy

plugins {
    // Core plugins for a Gradle plugin project
    `java-gradle-plugin`
    `maven-publish`
    // Apply the plugin-publish plugin with the latest version
    id("com.gradle.plugin-publish") version "1.2.1"
    // Kotlin support (if your plugin uses Kotlin)
    kotlin("jvm")
}

group = "com.example.vista"
version = "1.0.0"

// Define the Gradle plugin
gradlePlugin {
    plugins {
        create("versioningPlugin") {
            id = "com.example.vista.versioning.properties"
            implementationClass = "com.example.vista.VersioningPlugin"
            displayName = "VISTA Versioning Plugin"
            description = "A CLI Gradle plugin that automates version management using a version.properties file."
        }
    }
    website = "https://github.com/theaniketraj/vista"
    vcsUrl = "https://github.com/theaniketraj/vista.git"
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}