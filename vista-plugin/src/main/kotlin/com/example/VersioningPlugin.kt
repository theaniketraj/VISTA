package com.example.vista

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.util.Properties


class VersioningPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Register a task to increment the version using the version.properties file
        target.tasks.register("incrementVersion", DefaultTask::class.java) { task ->
            task.doLast {
                val versionFile = File(target.rootDir, "version.properties")
                if (!versionFile.exists()) {
                    println("⚠️ version.properties not found in ${target.rootDir.absolutePath}")
                    return@doLast
                }
                
                // Load properties from the version file
                val properties = Properties().apply { 
                    versionFile.inputStream().use { load(it) }
                }
                
                // Safely retrieve the current build number (defaulting to 0)
                val currentBuild = properties.getProperty("BUILD_NUMBER", "0").toIntOrNull() ?: 0
                val newBuild = currentBuild + 1
                
                // Update the property and persist changes back to the version file
                properties.setProperty("BUILD_NUMBER", newBuild.toString())
                versionFile.bufferedWriter().use { writer ->
                    properties.store(writer, null)
                }
                
                println("✅ Updated build number to: $newBuild")
            }
        }
    }
}
