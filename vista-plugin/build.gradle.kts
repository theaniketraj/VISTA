import org.gradle.api.tasks.Copy
import org.gradle.api.file.DuplicatesStrategy
import com.gradle.publish.PluginBundleExtension

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm")
}

group = "com.example.vista"
version = "1.0.0"

gradlePlugin {
    website.set("https://github.com/theaniketraj/vista")
    vcsUrl.set("https://github.com/theaniketraj/vista.git")

    plugins {
        create("versioningPlugin") {
            id = "com.example.vista.versioning.properties"
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