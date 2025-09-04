# User Guide

This comprehensive guide covers all aspects of using VISTA for automated version management in your projects. Whether you're a beginner or an advanced user, you'll find detailed information about VISTA's capabilities and best practices.

## Table of Contents

- [Core Concepts](#core-concepts)
- [Available Tasks](#available-tasks)
- [Version Properties](#version-properties)
- [Advanced Usage](#advanced-usage)
- [Best Practices](#best-practices)
- [Integration Patterns](#integration-patterns)
- [Troubleshooting](#troubleshooting)

## Core Concepts

### Semantic Versioning with VISTA

VISTA implements a four-component versioning system:

```version
VERSION_MAJOR.VERSION_MINOR.VERSION_PATCH.BUILD_NUMBER
```

- **VERSION_MAJOR**: Incremented for breaking changes
- **VERSION_MINOR**: Incremented for new features (backward compatible)
- **VERSION_PATCH**: Incremented for bug fixes
- **BUILD_NUMBER**: Automatically incremented by VISTA

### Version Properties File

The `version.properties` file is the heart of VISTA's version management:

```properties
# version.properties
VERSION_MAJOR=1
VERSION_MINOR=2
VERSION_PATCH=3
BUILD_NUMBER=45
```

This creates version: `1.2.3.45`

## Available Tasks

### incrementVersion

The primary task provided by VISTA.

**Usage:**

```bash
./gradlew incrementVersion
```

**What it does:**

1. Reads the current `version.properties` file
2. Increments the `BUILD_NUMBER` by 1
3. Saves the updated properties back to the file
4. Displays the new build number

**Output Example:**

```kotlin
> Task :incrementVersion
✅ Updated build number to: 46

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
```

### Task Configuration

You can configure the task behavior in your `build.gradle.kts`:

```kotlin
tasks.named("incrementVersion") {
    group = "versioning"
    description = "Increments the build number in version.properties"
}
```

## Version Properties

### Property Descriptions

| Property | Purpose | When to Update | Example |
|----------|---------|----------------|---------|
| `VERSION_MAJOR` | Breaking changes | API changes, major refactoring | `1` → `2` |
| `VERSION_MINOR` | New features | New functionality, enhancements | `1` → `2` |
| `VERSION_PATCH` | Bug fixes | Patches, minor fixes | `0` → `1` |
| `BUILD_NUMBER` | Build tracking | Automatically by VISTA | `1` → `2` |

### Manual Version Updates

While VISTA automatically manages `BUILD_NUMBER`, you'll manually update other components:

```bash
# For a new feature release
# Edit version.properties:
VERSION_MAJOR=1
VERSION_MINOR=3  # Incremented from 2
VERSION_PATCH=0  # Reset to 0
BUILD_NUMBER=1   # Will be auto-incremented

# Then run
./gradlew incrementVersion
```

## Advanced Usage

### Custom Version Format

Create a custom version string in your build script:

```kotlin
val versionProps = Properties().apply {
    file("version.properties").inputStream().use { load(it) }
}

val customVersion = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}-build.${versionProps["BUILD_NUMBER"]}"

version = customVersion
// Results in: "1.2.3-build.45"
```

### Conditional Version Increment

Increment version only under certain conditions:

```kotlin
tasks.register("conditionalIncrement") {
    doLast {
        val isReleaseBuild = project.hasProperty("release")
        if (isReleaseBuild) {
            tasks.named("incrementVersion").get().actions.forEach { it.execute(this) }
        }
    }
}
```

Usage:

```bash
./gradlew conditionalIncrement -Prelease
```

### Version Information in Code

#### Kotlin/Java Applications

```kotlin
class VersionInfo {
    companion object {
        fun getVersion(): String {
            val properties = Properties()
            properties.load(
                VersionInfo::class.java.classLoader
                    .getResourceAsStream("version.properties")
            )
            
            return "${properties["VERSION_MAJOR"]}.${properties["VERSION_MINOR"]}.${properties["VERSION_PATCH"]}.${properties["BUILD_NUMBER"]}"
        }
    }
}

// Usage
println("Application Version: ${VersionInfo.getVersion()}")
```

#### Android Applications

Add to your `app/build.gradle.kts`:

```kotlin
val versionProps = Properties().apply {
    file("../version.properties").inputStream().use { load(it) }
}

android {
    defaultConfig {
        versionCode = versionProps["BUILD_NUMBER"].toString().toInt()
        versionName = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}"
    }
}
```

## Best Practices

### 1. Version Control Integration

Always commit `version.properties` changes:

```bash
# After incrementing version
./gradlew incrementVersion
git add version.properties
git commit -m "Increment build number to $(grep BUILD_NUMBER version.properties | cut -d'=' -f2)"
```

### 2. CI/CD Pipeline Integration

#### GitHub Actions Example

```yaml
name: Build and Deploy

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Increment Version
      run: ./gradlew incrementVersion
    
    - name: Build Project
      run: ./gradlew build
    
    - name: Commit Version Update
      run: |
        git config --local user.email "action@github.com"
        git config --local user.name "GitHub Action"
        git add version.properties
        git commit -m "Auto-increment build number [skip ci]" || exit 0
        git push
```

### 3. Release Workflow

```bash
# 1. Prepare for release
# Manually update VERSION_MAJOR, VERSION_MINOR, or VERSION_PATCH
# Reset BUILD_NUMBER to 1

# 2. Create release build
./gradlew incrementVersion build

# 3. Tag the release
git tag -a "v$(grep VERSION_MAJOR version.properties | cut -d'=' -f2).$(grep VERSION_MINOR version.properties | cut -d'=' -f2).$(grep VERSION_PATCH version.properties | cut -d'=' -f2)" -m "Release version"

# 4. Push tag
git push origin --tags
```

### 4. Multi-Module Projects

For multi-module Gradle projects, apply VISTA to the root project:

```kotlin
// Root build.gradle.kts
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}

// Share version across all modules
subprojects {
    val versionProps = Properties().apply {
        rootProject.file("version.properties").inputStream().use { load(it) }
    }
    
    version = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}.${versionProps["BUILD_NUMBER"]}"
}
```

## Integration Patterns

### 1. Automatic Build Integration

```kotlin
tasks.named("build") {
    dependsOn("incrementVersion")
}

tasks.named("test") {
    mustRunAfter("incrementVersion")
}
```

### 2. Publishing Integration

```kotlin
tasks.named("publish") {
    dependsOn("incrementVersion")
}

tasks.named("publishToMavenLocal") {
    dependsOn("incrementVersion")
}
```

### 3. Docker Integration

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

# Copy version properties
COPY version.properties /app/

# Copy application
COPY build/libs/*.jar /app/app.jar

# Set version as environment variable
RUN echo "export APP_VERSION=$(grep VERSION_MAJOR /app/version.properties | cut -d'=' -f2).$(grep VERSION_MINOR /app/version.properties | cut -d'=' -f2).$(grep VERSION_PATCH /app/version.properties | cut -d'=' -f2).$(grep BUILD_NUMBER /app/version.properties | cut -d'=' -f2)" >> /etc/environment

WORKDIR /app
CMD ["java", "-jar", "app.jar"]
```

## Troubleshooting

### Common Issues and Solutions

#### Issue: Version Properties Not Found

**Error:**

```version
⚠️ version.properties not found in /path/to/project
```

**Solutions:**

1. Create the file in your project root:

   ```bash
   echo -e "VERSION_MAJOR=1\nVERSION_MINOR=0\nVERSION_PATCH=0\nBUILD_NUMBER=1" > version.properties
   ```

2. Verify file location:

   ```bash
   ls -la version.properties
   ```

#### Issue: Permission Denied

**Error:**

```gradle
java.io.IOException: Permission denied: version.properties
```

**Solutions:**

1. Check file permissions:

   ```bash
   chmod 644 version.properties
   ```

2. Verify directory permissions:

   ```bash
   chmod 755 .
   ```

#### Issue: Invalid Number Format

**Error:**

```java
java.lang.NumberFormatException: For input string: "abc"
```

**Solution:**
Ensure `BUILD_NUMBER` contains only numeric values:

```properties
BUILD_NUMBER=123  # ✅ Correct
BUILD_NUMBER=abc  # ❌ Incorrect
```

#### Issue: Task Not Found

**Error:**

```gradle
Task 'incrementVersion' not found in root project
```

**Solutions:**

1. Verify plugin application:

   ```kotlin
   plugins {
       id("io.github.theaniketraj.vista") version "1.0.7"
   }
   ```

2. Check available tasks:

   ```bash
   ./gradlew tasks --all | grep increment
   ```

### Debug Mode

Enable Gradle debug output for troubleshooting:

```bash
./gradlew incrementVersion --debug
```

### Getting Help

If you encounter issues not covered here:

1. **Check the logs**: Enable debug mode and review the output
2. **Verify setup**: Ensure all prerequisites are met
3. **GitHub Issues**: [Report bugs or ask questions](https://github.com/theaniketraj/VISTA/issues)
4. **Community**: Join discussions in the GitHub repository

## Performance Considerations

### Large Projects

For projects with many modules or complex build scripts:

1. **Run incrementVersion early**: Place it before resource-intensive tasks
2. **Use build cache**: Enable Gradle build cache for faster builds
3. **Parallel execution**: Use `--parallel` flag when appropriate

### CI/CD Optimization

```yaml
# Optimize CI builds
- name: Cache Gradle packages
  uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

- name: Increment Version (Fast)
  run: ./gradlew incrementVersion --no-daemon --parallel
```

---

*This user guide covers the essential aspects of using VISTA. For additional features and advanced configurations, check out our [Configuration Guide](./configuration.md).*
