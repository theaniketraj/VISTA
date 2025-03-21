import com.gradle.plugin.publish.PluginBundleExtension

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.0"
    kotlin("jvm")
}

group = "com.example.vista"
version = "1.0.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("versioningPlugin") {
            id = "com.example.vista.versioning"
            implementationClass = "com.example.vista.VersioningPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/theaniketraj/VISTA"
    vcsUrl = "https://github.com/theaniketraj/VISTA.git"
    tags = listOf("versioning", "gradle", "cli", "automation")
    plugins {
        named("versioningPlugin") {
            displayName = "VISTA Versioning Plugin"
            description = "A CLI Gradle plugin that automates version management using a version.properties file."
        }
    }
}

val pluginBundleExtension = extensions.findByName("pluginBundle") as? PluginBundleExtension
if (pluginBundleExtension != null) {
    pluginBundleExtension.apply {
        website = "https://github.com/theaniketraj/VISTA"
        vcsUrl = "https://github.com/theaniketraj/VISTA.git"
        tags = listOf("versioning", "gradle", "automation")
        plugins.named("versioningPlugin") {
            displayName = "VISTA Versioning Plugin"
            description = "Automates version management using a version.properties file."
        }
    }
} else {
    println("Warning: PluginBundleExtension not found. Plugin publishing metadata is not configured.")
}