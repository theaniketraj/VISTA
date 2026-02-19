package com.example.vista

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import java.util.Properties

// Extension for configuring the plugin
interface VistaExtension {
    val versionFileName: Property<String>
}

class VersioningPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Create the extension
        val extension = target.extensions.create("vista", VistaExtension::class.java)
        extension.versionFileName.convention("version.properties")

        // Helper to load properties
        fun loadProperties(file: File): Properties {
            return Properties().apply {
                if (file.exists()) {
                    file.inputStream().use { load(it) }
                }
            }
        }

        // Helper to save properties
        fun saveProperties(
            file: File,
            properties: Properties,
        ) {
            file.bufferedWriter().use { writer ->
                properties.store(writer, null)
            }
        }

        // Helper to get property as Int
        fun Properties.getInt(
            key: String,
            default: Int = 0,
        ): Int = getProperty(key, default.toString()).toIntOrNull() ?: default

        // Helper to get value from Env or Properties
        fun getValue(
            properties: Properties,
            propKey: String,
            envKey: String,
            default: Int = 0,
        ): Int {
            val envVal = System.getenv(envKey)
            if (envVal != null) {
                return envVal.toIntOrNull() ?: default
            }
            return properties.getInt(propKey, default)
        }

        // Configure project version in afterEvaluate to allow extension configuration
        target.afterEvaluate {
            val versionFile = File(target.rootDir, extension.versionFileName.get())
            val props = loadProperties(versionFile)

            val major = getValue(props, "VERSION_MAJOR", "VISTA_VERSION_MAJOR")
            val minor = getValue(props, "VERSION_MINOR", "VISTA_VERSION_MINOR")
            val patch = getValue(props, "VERSION_PATCH", "VISTA_VERSION_PATCH")
            val build = getValue(props, "BUILD_NUMBER", "VISTA_BUILD_NUMBER")

            val versionString = "$major.$minor.$patch.$build"
            target.version = versionString
            println("VISTA: Project version set to $versionString")
        }

        // Register tasks
        target.tasks.register("incrementMajor", DefaultTask::class.java) { task ->
            task.group = "versioning"
            task.description = "Increments the MAJOR version and resets MINOR, PATCH, and BUILD."
            task.doLast {
                val versionFile = File(target.rootDir, extension.versionFileName.get())
                val props = loadProperties(versionFile)

                val major = props.getInt("VERSION_MAJOR") + 1
                props.setProperty("VERSION_MAJOR", major.toString())
                props.setProperty("VERSION_MINOR", "0")
                props.setProperty("VERSION_PATCH", "0")
                props.setProperty("BUILD_NUMBER", "0")

                saveProperties(versionFile, props)
                println("Incremented MAJOR version to: $major.0.0.0")
            }
        }

        target.tasks.register("incrementMinor", DefaultTask::class.java) { task ->
            task.group = "versioning"
            task.description = "Increments the MINOR version and resets PATCH and BUILD."
            task.doLast {
                val versionFile = File(target.rootDir, extension.versionFileName.get())
                val props = loadProperties(versionFile)

                val major = props.getInt("VERSION_MAJOR")
                val minor = props.getInt("VERSION_MINOR") + 1
                props.setProperty("VERSION_MINOR", minor.toString())
                props.setProperty("VERSION_PATCH", "0")
                props.setProperty("BUILD_NUMBER", "0")

                saveProperties(versionFile, props)
                println("Incremented MINOR version to: $major.$minor.0.0")
            }
        }

        target.tasks.register("incrementPatch", DefaultTask::class.java) { task ->
            task.group = "versioning"
            task.description = "Increments the PATCH version and resets BUILD."
            task.doLast {
                val versionFile = File(target.rootDir, extension.versionFileName.get())
                val props = loadProperties(versionFile)

                val major = props.getInt("VERSION_MAJOR")
                val minor = props.getInt("VERSION_MINOR")
                val patch = props.getInt("VERSION_PATCH") + 1
                props.setProperty("VERSION_PATCH", patch.toString())
                props.setProperty("BUILD_NUMBER", "0")

                saveProperties(versionFile, props)
                println("Incremented PATCH version to: $major.$minor.$patch.0")
            }
        }

        target.tasks.register("incrementBuild", DefaultTask::class.java) { task ->
            task.group = "versioning"
            task.description = "Increments the BUILD_NUMBER."
            task.doLast {
                val versionFile = File(target.rootDir, extension.versionFileName.get())
                val props = loadProperties(versionFile)

                val build = props.getInt("BUILD_NUMBER") + 1
                props.setProperty("BUILD_NUMBER", build.toString())

                saveProperties(versionFile, props)
                println("Updated build number to: $build")
            }
        }

        // Maintain backward compatibility or alias
        target.tasks.register("incrementVersion", DefaultTask::class.java) { task ->
            task.group = "versioning"
            task.description = "Alias for incrementBuild."
            task.dependsOn("incrementBuild")
        }

        target.tasks.register("printVersion", DefaultTask::class.java) { task ->
            task.group = "versioning"
            task.description = "Prints the current version string."
            task.doLast {
                println("Project Version: ${target.version}")

                val versionFile = File(target.rootDir, extension.versionFileName.get())
                if (versionFile.exists()) {
                    val props = loadProperties(versionFile)
                    val major = props.getInt("VERSION_MAJOR")
                    val minor = props.getInt("VERSION_MINOR")
                    val patch = props.getInt("VERSION_PATCH")
                    val build = props.getInt("BUILD_NUMBER")
                    println("File Version:    $major.$minor.$patch.$build")
                } else {
                    println("Version file not found: ${versionFile.absolutePath}")
                }
            }
        }
    }
}
