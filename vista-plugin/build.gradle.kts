import org.gradle.api.tasks.Copy
import org.gradle.api.file.DuplicatesStrategy
import com.gradle.publish.PluginBundleExtension

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm")
}

group = "io.github.theaniketraj"
version = "1.0.7"

gradlePlugin {
    website.set("https://github.com/theaniketraj/vista")
    vcsUrl.set("https://github.com/theaniketraj/vista.git")

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