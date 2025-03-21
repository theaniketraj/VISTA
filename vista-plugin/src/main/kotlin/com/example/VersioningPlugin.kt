package com.example.vista

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.util.Properties

class VersioningPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Register a task to increment the version based on version.properties.
        target.tasks.register("incrementVersion") {
            doLast {
                val versionFile = File(target.rootDir, "version.properties")
                if (!versionFile.exists()) {
                    println("⚠️ version.properties not found in ${target.rootDir.absolutePath}")
                    return@doLast
                }
                val properties = Properties().apply { 
                    versionFile.inputStream().use { load(it) }
                }
                val buildNumber = properties.getProperty("BUILD_NUMBER", "0").toInt() + 1
                properties.setProperty("BUILD_NUMBER", buildNumber.toString())
                versionFile.bufferedWriter().use { writer ->
                    properties.store(writer, null)
                }
                println("✅ Updated build number to: $buildNumber")
            }
        }
    }
}