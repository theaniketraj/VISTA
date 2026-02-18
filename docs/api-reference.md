# API Reference

This document provides detailed API reference for VISTA's classes, methods, and configuration options.

## Table of Contents

- [Plugin API](#plugin-api)
- [Tasks](#tasks)
- [Configuration](#configuration)
- [Properties](#properties)
- [Gradle Integration](#gradle-integration)
- [Extension Points](#extension-points)

## Plugin API

### VersioningPlugin

The main plugin class that provides version management functionality.

```kotlin
class VersioningPlugin : Plugin<Project>
```

#### Methods

##### apply(target: Project)

Applies the VISTA plugin to the specified Gradle project.

**Parameters:**

- `target: Project` - The Gradle project to apply the plugin to

**Behavior:**

- Registers the `incrementVersion` task
- Configures task dependencies and properties
- Sets up version management infrastructure

**Example:**

```kotlin
// Applied automatically when using plugins DSL
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}
```

## Tasks

### incrementVersion

The primary task for incrementing build numbers in the version properties file.

**Task Type:** `DefaultTask`  
**Group:** `versioning` (default)  
**Description:** Increments the build number in version.properties

#### Configuration

```kotlin
tasks.named("incrementVersion") {
    group = "versioning"
    description = "Increments the build number in version.properties"
}
```

#### Behavior

1. **File Location**: Looks for `version.properties` in the project root directory
2. **Property Reading**: Loads existing properties using Java Properties format
3. **Increment Logic**: Increments the `BUILD_NUMBER` property by 1
4. **File Writing**: Saves updated properties back to the file
5. **Logging**: Outputs success message with new build number

#### Error Handling

| Condition                 | Behavior                                          |
| ------------------------- | ------------------------------------------------- |
| File not found            | Logs warning message, task completes successfully |
| Invalid number format     | Uses default value of 0, then increments to 1     |
| File permission error     | Task fails with IOException                       |
| Malformed properties file | Task fails with parsing exception                 |

#### Output Examples

**Success:**

```kotlin
> Task :incrementVersion
✅ Updated build number to: 42

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
```

**File not found:**

```kotlin
> Task :incrementVersion
⚠️ version.properties not found in /path/to/project

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
```

### Plugin Configuration

VISTA uses convention-over-configuration and works with minimal setup.

#### Default Behavior

```kotlin
// Default configuration (no explicit configuration needed)
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}

// The plugin automatically:
// - Looks for version.properties in project root
// - Provides incrementVersion task
// - Uses standard property names
```

#### Custom Task Configuration

```kotlin
tasks.named("incrementVersion") {
    // Task group (optional)
    group = "versioning"
    
    // Task description (optional)
    description = "Increments the build number in version.properties"
    
    // Custom actions (optional)
    doFirst {
        println("Starting version increment...")
    }
    
    doLast {
        println("Version increment completed!")
    }
}
```

#### Conditional Execution

```kotlin
tasks.named("incrementVersion") {
    // Only run on CI
    onlyIf {
        System.getenv("CI") == "true"
    }
}
```

## Properties

### Version Properties File Format

VISTA expects a standard Java properties file format:

```properties
# version.properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0
BUILD_NUMBER=1
```

#### Property Specifications

| Property        | Type    | Required | Default | Description                     |
| --------------- | ------- | -------- | ------- | ------------------------------- |
| `VERSION_MAJOR` | Integer | No       | N/A     | Major version number            |
| `VERSION_MINOR` | Integer | No       | N/A     | Minor version number            |
| `VERSION_PATCH` | Integer | No       | N/A     | Patch version number            |
| `BUILD_NUMBER`  | Integer | Yes      | 0       | Build number (auto-incremented) |

#### Property Validation

- **BUILD_NUMBER**: Must be a valid integer or empty (defaults to 0)
- **Other properties**: Not validated by VISTA but recommended to be integers
- **File format**: Must be valid Java properties format

#### Example Configurations

**Minimal configuration:**

```properties
BUILD_NUMBER=1
```

**Full semantic versioning:**

```properties
VERSION_MAJOR=2
VERSION_MINOR=1
VERSION_PATCH=3
BUILD_NUMBER=45
```

**With comments:**

```properties
# Project version configuration
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0

# Auto-incremented by VISTA
BUILD_NUMBER=1
```

## Gradle Integration

### Task Dependencies

#### Automatic Dependencies

VISTA tasks can be integrated into your build lifecycle:

```kotlin
// Run incrementVersion before build
tasks.named("build") {
    dependsOn("incrementVersion")
}

// Ensure tests run after version increment
tasks.named("test") {
    mustRunAfter("incrementVersion")
}
```

#### Publishing Integration

```kotlin
// Increment version before publishing
tasks.named("publish") {
    dependsOn("incrementVersion")
}

tasks.named("publishToMavenLocal") {
    dependsOn("incrementVersion")
}
```

### Multi-Project Setup

#### Root Project Configuration

```kotlin
// Root build.gradle.kts
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}

// Apply to all subprojects
subprojects {
    apply(plugin = "io.github.theaniketraj.vista")
}
```

#### Shared Version Configuration

```kotlin
// Share version across modules
subprojects {
    val versionProps = Properties().apply {
        rootProject.file("version.properties").inputStream().use { load(it) }
    }
    
    version = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}.${versionProps["BUILD_NUMBER"]}"
}
```

### Version Access in Build Scripts

#### Reading Version Properties

```kotlin
// Load version properties
val versionProps = Properties().apply {
    file("version.properties").inputStream().use { load(it) }
}

// Access individual components
val major = versionProps["VERSION_MAJOR"]
val minor = versionProps["VERSION_MINOR"]
val patch = versionProps["VERSION_PATCH"]
val build = versionProps["BUILD_NUMBER"]

// Create version string
val fullVersion = "$major.$minor.$patch.$build"
version = fullVersion
```

#### Using in JAR Manifest

```kotlin
tasks.named<Jar>("jar") {
    val versionProps = Properties().apply {
        file("version.properties").inputStream().use { load(it) }
    }
    
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to version,
            "Build-Number" to versionProps["BUILD_NUMBER"],
            "Build-Date" to java.time.LocalDateTime.now().toString()
        )
    }
}
```

## Extension Points

### Custom Tasks

Create custom tasks that work with VISTA:

#### Custom Increment Tasks

```kotlin
tasks.register("incrementPatch") {
    group = "versioning"
    description = "Increments the patch version"
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val currentPatch = properties.getProperty("VERSION_PATCH", "0").toInt()
        properties.setProperty("VERSION_PATCH", (currentPatch + 1).toString())
        properties.setProperty("BUILD_NUMBER", "1") // Reset build number
        
        versionFile.outputStream().use { properties.store(it, null) }
        println("Incremented patch version to: ${currentPatch + 1}")
    }
}
```

#### Version Validation Tasks

```kotlin
tasks.register("validateVersion") {
    group = "versioning"
    description = "Validates version properties"
    
    doLast {
        val versionFile = file("version.properties")
        require(versionFile.exists()) { "version.properties not found" }
        
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        // Validate required properties
        val buildNumber = properties.getProperty("BUILD_NUMBER")
        requireNotNull(buildNumber) { "BUILD_NUMBER property is required" }
        
        // Validate format
        buildNumber.toIntOrNull() ?: throw IllegalArgumentException("BUILD_NUMBER must be an integer")
        
        println("Version validation passed")
    }
}
```

### Custom Version Formats

#### Semantic Version Generation

```kotlin
tasks.register("generateSemanticVersion") {
    group = "versioning"
    description = "Generates semantic version string"
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val semanticVersion = "${properties["VERSION_MAJOR"]}.${properties["VERSION_MINOR"]}.${properties["VERSION_PATCH"]}"
        val fullVersion = "$semanticVersion+${properties["BUILD_NUMBER"]}"
        
        // Write to files
        file("version.txt").writeText(fullVersion)
        file("semantic-version.txt").writeText(semanticVersion)
        
        println("Generated versions:")
        println("  Semantic: $semanticVersion")
        println("  Full: $fullVersion")
    }
}
```

### Integration with Other Plugins

#### Git Integration

```kotlin
tasks.register("tagVersion") {
    group = "versioning"
    description = "Tags current commit with version"
    
    dependsOn("incrementVersion")
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val version = "${properties["VERSION_MAJOR"]}.${properties["VERSION_MINOR"]}.${properties["VERSION_PATCH"]}"
        val tagName = "v$version"
        
        exec {
            commandLine("git", "tag", "-a", tagName, "-m", "Release version $version")
        }
        
        println("Created tag: $tagName")
    }
}
```

#### Docker Integration

```kotlin
tasks.register("buildDockerImage") {
    group = "docker"
    description = "Builds Docker image with version tag"
    
    dependsOn("incrementVersion")
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val version = "${properties["VERSION_MAJOR"]}.${properties["VERSION_MINOR"]}.${properties["VERSION_PATCH"]}.${properties["BUILD_NUMBER"]}"
        val imageName = "${project.name}:$version"
        
        exec {
            commandLine("docker", "build", "-t", imageName, ".")
        }
        
        println("Built Docker image: $imageName")
    }
}
```

## Error Handling and Recovery

### Exception Types

| Exception               | Cause                        | Handling                         |
| ----------------------- | ---------------------------- | -------------------------------- |
| `FileNotFoundException` | version.properties not found | Warning logged, task continues   |
| `IOException`           | File read/write error        | Task fails with error message    |
| `NumberFormatException` | Invalid BUILD_NUMBER format  | Defaults to 0, continues         |
| `SecurityException`     | File permission denied       | Task fails with permission error |

### Error Recovery

```kotlin
// Custom error handling
tasks.named("incrementVersion") {
    doLast {
        try {
            // Normal increment logic
            val versionFile = file("version.properties")
            // ... increment logic
        } catch (e: IOException) {
            logger.error("Failed to update version: ${e.message}")
            // Custom recovery logic
            createDefaultVersionFile()
        }
    }
}

fun createDefaultVersionFile() {
    file("version.properties").writeText("""
        VERSION_MAJOR=1
        VERSION_MINOR=0
        VERSION_PATCH=0
        BUILD_NUMBER=1
    """.trimIndent())
}
```

## Performance Considerations

### File I/O Optimization

- **Single read/write**: VISTA reads and writes the properties file only once per task execution
- **Memory usage**: Properties are loaded into memory temporarily
- **File locking**: No explicit file locking is used

### Build Performance

- **Task execution time**: Typically < 100ms for standard properties files
- **Incremental builds**: Task is always executed (not cacheable by design)
- **Parallel execution**: Safe to run in parallel builds

### Large Projects

For projects with many modules:

```kotlin
// Optimize for large projects
tasks.named("incrementVersion") {
    // Run early in build lifecycle
    mustRunAfter("clean")
    
    // Avoid unnecessary work
    onlyIf {
        !project.gradle.startParameter.isOffline
    }
}
```

---

*This API reference covers all public APIs and extension points provided by VISTA. For usage examples, see the [User Guide](./user-guide.md).*
