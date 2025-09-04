# Configuration

This guide covers advanced configuration options for VISTA, allowing you to customize the plugin behavior to meet your specific project requirements.

## Table of Contents

- [Basic Configuration](#basic-configuration)
- [Version Properties Customization](#version-properties-customization)
- [Task Configuration](#task-configuration)
- [Build Integration](#build-integration)
- [Multi-Project Setup](#multi-project-setup)
- [Environment-Specific Configuration](#environment-specific-configuration)
- [Advanced Scenarios](#advanced-scenarios)

## Basic Configuration

### Default Configuration

VISTA works out of the box with minimal configuration. The plugin automatically:

- Looks for `version.properties` in the project root
- Provides the `incrementVersion` task
- Uses standard property names

### Plugin Application

Apply VISTA to your project using the plugins DSL:

```kotlin
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}
```

## Version Properties Customization

### Standard Properties File

The default `version.properties` file structure:

```properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0
BUILD_NUMBER=1
```

### Custom Property Names

While VISTA uses standard property names, you can work with custom formats by preprocessing:

```kotlin
tasks.register("customVersionIncrement") {
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        // Custom property mapping
        val customBuildNumber = properties.getProperty("CUSTOM_BUILD", "0").toInt() + 1
        properties.setProperty("CUSTOM_BUILD", customBuildNumber.toString())
        
        versionFile.outputStream().use { properties.store(it, null) }
        println("Updated custom build number to: $customBuildNumber")
    }
}
```

### Alternative File Locations

Configure VISTA to use a different properties file location:

```kotlin
tasks.named("incrementVersion") {
    doFirst {
        // Ensure the custom location exists
        val customVersionFile = file("config/version.properties")
        if (!customVersionFile.exists()) {
            customVersionFile.parentFile.mkdirs()
            customVersionFile.writeText("""
                VERSION_MAJOR=1
                VERSION_MINOR=0
                VERSION_PATCH=0
                BUILD_NUMBER=1
            """.trimIndent())
        }
    }
}
```

## Task Configuration

### Task Customization

Customize the `incrementVersion` task behavior:

```kotlin
tasks.named("incrementVersion") {
    group = "versioning"
    description = "Increments the build number in version.properties"
    
    // Add custom actions
    doFirst {
        println("Starting version increment...")
    }
    
    doLast {
        println("Version increment completed!")
        
        // Custom post-increment actions
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val fullVersion = "${properties["VERSION_MAJOR"]}.${properties["VERSION_MINOR"]}.${properties["VERSION_PATCH"]}.${properties["BUILD_NUMBER"]}"
        println("New full version: $fullVersion")
    }
}
```

### Conditional Execution

Execute version increment only under specific conditions:

```kotlin
tasks.named("incrementVersion") {
    onlyIf {
        // Only increment on CI builds
        System.getenv("CI") == "true"
    }
}

// Or create a conditional wrapper task
tasks.register("incrementVersionIfNeeded") {
    dependsOn("incrementVersion")
    
    onlyIf {
        val shouldIncrement = project.hasProperty("increment") || 
                            System.getenv("AUTO_INCREMENT") == "true"
        shouldIncrement
    }
}
```

### Custom Increment Logic

Create tasks with custom increment logic:

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

tasks.register("incrementMinor") {
    group = "versioning"
    description = "Increments the minor version"
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val currentMinor = properties.getProperty("VERSION_MINOR", "0").toInt()
        properties.setProperty("VERSION_MINOR", (currentMinor + 1).toString())
        properties.setProperty("VERSION_PATCH", "0") // Reset patch
        properties.setProperty("BUILD_NUMBER", "1") // Reset build number
        
        versionFile.outputStream().use { properties.store(it, null) }
        println("Incremented minor version to: ${currentMinor + 1}")
    }
}
```

## Build Integration

### Automatic Version Increment

Integrate version increment with your build process:

```kotlin
// Increment version before building
tasks.named("build") {
    dependsOn("incrementVersion")
}

// Ensure tests run after version increment
tasks.named("test") {
    mustRunAfter("incrementVersion")
}

// For publishing workflows
tasks.named("publish") {
    dependsOn("incrementVersion")
}
```

### Selective Integration

Apply version increment only to specific build types:

```kotlin
tasks.register("releaseBuild") {
    group = "build"
    description = "Builds a release version with incremented version"
    
    dependsOn("incrementVersion", "build")
    
    doLast {
        println("Release build completed with new version")
    }
}

tasks.register("developmentBuild") {
    group = "build"
    description = "Builds without incrementing version"
    
    dependsOn("build")
    
    doLast {
        println("Development build completed")
    }
}
```

### Version in Build Output

Include version information in build artifacts:

```kotlin
val versionProps = Properties().apply {
    file("version.properties").inputStream().use { load(it) }
}

val projectVersion = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}.${versionProps["BUILD_NUMBER"]}"

// Set project version
version = projectVersion

// Include in JAR manifest
tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to projectVersion,
            "Build-Number" to versionProps["BUILD_NUMBER"]
        )
    }
}
```

## Multi-Project Setup

### Root Project Configuration

For multi-module projects, configure VISTA at the root level:

```kotlin
// Root build.gradle.kts
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}

// Share version across all subprojects
subprojects {
    val versionProps = Properties().apply {
        rootProject.file("version.properties").inputStream().use { load(it) }
    }
    
    version = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}.${versionProps["BUILD_NUMBER"]}"
    
    // Apply version to all JAR tasks
    tasks.withType<Jar> {
        archiveVersion.set(version.toString())
    }
}
```

### Module-Specific Versioning

Configure different versioning strategies for different modules:

```kotlin
// In a specific module's build.gradle.kts
val versionProps = Properties().apply {
    rootProject.file("version.properties").inputStream().use { load(it) }
}

// Custom version format for this module
version = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}-${project.name}.${versionProps["BUILD_NUMBER"]}"
```

### Synchronized Version Updates

Ensure all modules use the same version:

```kotlin
// Root build.gradle.kts
tasks.register("updateAllVersions") {
    group = "versioning"
    description = "Updates version in all modules"
    
    dependsOn("incrementVersion")
    
    doLast {
        val versionProps = Properties().apply {
            file("version.properties").inputStream().use { load(it) }
        }
        
        val newVersion = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}.${versionProps["BUILD_NUMBER"]}"
        
        subprojects.forEach { subproject ->
            subproject.version = newVersion
            println("Updated ${subproject.name} to version: $newVersion")
        }
    }
}
```

## Environment-Specific Configuration

### Development vs Production

Configure different behaviors for different environments:

```kotlin
val isProduction = System.getenv("ENVIRONMENT") == "production"
val isDevelopment = System.getenv("ENVIRONMENT") == "development"

tasks.named("incrementVersion") {
    onlyIf { isProduction }
}

tasks.register("devBuild") {
    group = "build"
    dependsOn(if (isDevelopment) "build" else listOf("incrementVersion", "build"))
}
```

### CI/CD Configuration

Special configuration for continuous integration:

```kotlin
val isCi = System.getenv("CI") == "true"
val isGitHubActions = System.getenv("GITHUB_ACTIONS") == "true"

if (isCi) {
    tasks.named("incrementVersion") {
        doLast {
            // Additional CI-specific actions
            if (isGitHubActions) {
                val versionFile = file("version.properties")
                val properties = Properties().apply {
                    versionFile.inputStream().use { load(it) }
                }
                
                // Set GitHub Actions output
                println("::set-output name=version::${properties["BUILD_NUMBER"]}")
            }
        }
    }
}
```

### Branch-Specific Versioning

Different versioning strategies for different branches:

```kotlin
val currentBranch = System.getenv("GITHUB_REF_NAME") ?: "main"

tasks.register("branchSpecificIncrement") {
    group = "versioning"
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        when (currentBranch) {
            "main", "master" -> {
                // Standard increment for main branch
                tasks.named("incrementVersion").get().actions.forEach { it.execute(this) }
            }
            "develop" -> {
                // Different increment strategy for develop
                val currentBuild = properties.getProperty("BUILD_NUMBER", "0").toInt()
                properties.setProperty("BUILD_NUMBER", (currentBuild + 10).toString())
                versionFile.outputStream().use { properties.store(it, null) }
            }
            else -> {
                // Feature branches - no increment
                println("Skipping version increment for feature branch: $currentBranch")
            }
        }
    }
}
```

## Advanced Scenarios

### Version Rollback

Create a task to rollback version changes:

```kotlin
tasks.register("rollbackVersion") {
    group = "versioning"
    description = "Rolls back the last version increment"
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val currentBuild = properties.getProperty("BUILD_NUMBER", "1").toInt()
        if (currentBuild > 1) {
            properties.setProperty("BUILD_NUMBER", (currentBuild - 1).toString())
            versionFile.outputStream().use { properties.store(it, null) }
            println("Rolled back build number to: ${currentBuild - 1}")
        } else {
            println("Cannot rollback - already at minimum build number")
        }
    }
}
```

### Version Validation

Add validation to ensure version consistency:

```kotlin
tasks.register("validateVersion") {
    group = "versioning"
    description = "Validates version properties"
    
    doLast {
        val versionFile = file("version.properties")
        if (!versionFile.exists()) {
            throw GradleException("version.properties file not found")
        }
        
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val requiredProperties = listOf("VERSION_MAJOR", "VERSION_MINOR", "VERSION_PATCH", "BUILD_NUMBER")
        val missingProperties = requiredProperties.filter { !properties.containsKey(it) }
        
        if (missingProperties.isNotEmpty()) {
            throw GradleException("Missing required properties: ${missingProperties.joinToString(", ")}")
        }
        
        // Validate numeric values
        requiredProperties.forEach { prop ->
            val value = properties.getProperty(prop)
            try {
                value.toInt()
            } catch (e: NumberFormatException) {
                throw GradleException("Property $prop must be a valid integer, got: $value")
            }
        }
        
        println("Version properties validation passed")
    }
}

// Run validation before increment
tasks.named("incrementVersion") {
    dependsOn("validateVersion")
}
```

### Custom Version Formats

Support for different version formats:

```kotlin
tasks.register("generateSemanticVersion") {
    group = "versioning"
    description = "Generates semantic version string"
    
    doLast {
        val versionFile = file("version.properties")
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val major = properties["VERSION_MAJOR"]
        val minor = properties["VERSION_MINOR"]
        val patch = properties["VERSION_PATCH"]
        val build = properties["BUILD_NUMBER"]
        
        val semanticVersion = "$major.$minor.$patch"
        val fullVersion = "$major.$minor.$patch+$build"
        val shortVersion = "$major.$minor"
        
        println("Semantic Version: $semanticVersion")
        println("Full Version: $fullVersion")
        println("Short Version: $shortVersion")
        
        // Write to additional files
        file("version.txt").writeText(fullVersion)
        file("semantic-version.txt").writeText(semanticVersion)
    }
}
```

### Integration with Version Control

Automatically tag releases with version information:

```kotlin
tasks.register("tagRelease") {
    group = "versioning"
    description = "Tags the current commit with version"
    
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

## Configuration Best Practices

### 1. Keep It Simple

Start with basic configuration and add complexity only when needed:

```kotlin
// Good: Simple and clear
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}

tasks.named("build") {
    dependsOn("incrementVersion")
}
```

### 2. Environment Separation

Use different configurations for different environments:

```kotlin
val config = when (System.getenv("ENVIRONMENT")) {
    "production" -> ProductionConfig()
    "staging" -> StagingConfig()
    else -> DevelopmentConfig()
}

tasks.named("incrementVersion") {
    onlyIf { config.shouldIncrementVersion }
}
```

### 3. Documentation

Document your custom configurations:

```kotlin
/**
 * Custom version increment task for feature branches.
 * Increments build number by 100 to distinguish from main branch builds.
 */
tasks.register("featureBranchIncrement") {
    // Implementation
}
```

### 4. Testing

Test your configuration changes:

```kotlin
tasks.register("testVersionConfig") {
    group = "verification"
    
    doLast {
        // Test version increment without actually changing files
        val testProps = Properties().apply {
            setProperty("VERSION_MAJOR", "1")
            setProperty("VERSION_MINOR", "0")
            setProperty("VERSION_PATCH", "0")
            setProperty("BUILD_NUMBER", "1")
        }
        
        val newBuild = testProps.getProperty("BUILD_NUMBER").toInt() + 1
        println("Test increment would result in build number: $newBuild")
    }
}
```

---

*This configuration guide covers advanced VISTA usage scenarios. For basic usage, refer to the [Getting Started](./getting-started.md) guide.*
