pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")  // Not strictly needed for a CLI plugin
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        plugins {
            id("com.gradle.plugin-publish") version "1.2.1"
        }
    }
}

rootProject.name = "VISTA"
include(":vista-plugin")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}