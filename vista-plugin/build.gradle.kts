import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm")
}

group = "io.github.theaniketraj"
version = "1.0.7"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.named<Test>("test") {
    enabled = false
}

gradlePlugin {
    website.set("https://github.com/theaniketraj/VISTA")
    vcsUrl.set("https://github.com/theaniketraj/VISTA.git")

    plugins {
        create("versioningPlugin") {
            id = "io.github.theaniketraj.vista"
            version = "1.0.7"
            displayName = "VISTA Versioning Plugin"
            implementationClass = "com.example.vista.VersioningPlugin"
            displayName = "VISTA Versioning Plugin"
            website = "https://github.com/theaniketraj/VISTA/blob/main/README.md"
            vcsUrl = "https://github.com/theaniketraj/VISTA.git"
            description = "A CLI Gradle plugin that automates version management using a version.properties file."
            tags.set(listOf("versioning", "automation", "ceie", "cvm", "cli"))
        }
    }
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
