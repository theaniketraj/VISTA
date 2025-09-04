# Getting Started

This guide will help you get up and running with VISTA in just a few minutes. Follow these steps to integrate automated version management into your Gradle project.

## Prerequisites

Before you begin, ensure you have the following installed:

- **JDK**: Version 17 or later
- **Gradle**: Version 8.11.1 or later
- **Kotlin**: Configured via Gradle (preferably version 1.9.x)

### Environment Setup

1. **Verify Java Installation**

   ```bash
   java -version
   ```

   Ensure you have JDK 17+ installed and `JAVA_HOME` points to a valid JDK (not JRE).

2. **Check Gradle Version**

   ```bash
   gradle --version
   ```

   Or use the Gradle wrapper:

   ```bash
   ./gradlew --version
   ```

## Installation

### Method 1: Using the Plugins DSL (Recommended)

Add VISTA to your project's `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}
```

Or for Groovy DSL (`build.gradle`):

```groovy
plugins {
    id 'io.github.theaniketraj.vista' version '1.0.7'
}
```

### Method 2: Using Legacy Plugin Application

For older Gradle versions, use the legacy plugin application method:

```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.theaniketraj:vista-plugin:1.0.7")
    }
}

apply(plugin = "io.github.theaniketraj.vista")
```

## Initial Setup

### 1. Create Version Properties File

Create a `version.properties` file in your project root directory:

```properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0
BUILD_NUMBER=1
```

### 2. Verify Installation

Run the following command to verify VISTA is properly installed:

```bash
./gradlew tasks --group="versioning"
```

You should see the `incrementVersion` task listed.

## First Run

### Increment Your Build Number

Execute the version increment task:

```bash
./gradlew incrementVersion
```

**Expected Output:**

```kotlin
> Task :incrementVersion
✅ Updated build number to: 2

BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
```

### Verify the Update

Check your `version.properties` file - the `BUILD_NUMBER` should now be incremented:

```properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0
BUILD_NUMBER=2
```

## Integration with Your Build Process

### Automatic Version Increment on Build

To automatically increment the version on every build, add the following to your `build.gradle.kts`:

```kotlin
tasks.named("build") {
    dependsOn("incrementVersion")
}
```

### CI/CD Integration

For continuous integration, add the version increment to your CI pipeline:

```yaml
# GitHub Actions example
- name: Increment Version
  run: ./gradlew incrementVersion

- name: Build Project
  run: ./gradlew build
```

## Reading Version Information

### In Gradle Build Scripts

Access version information in your build scripts:

```kotlin
val versionProps = Properties().apply {
    file("version.properties").inputStream().use { load(it) }
}

val projectVersion = "${versionProps["VERSION_MAJOR"]}.${versionProps["VERSION_MINOR"]}.${versionProps["VERSION_PATCH"]}.${versionProps["BUILD_NUMBER"]}"

version = projectVersion
```

### In Application Code

Read version information in your application:

```kotlin
val properties = Properties()
properties.load(this::class.java.classLoader.getResourceAsStream("version.properties"))

val version = "${properties["VERSION_MAJOR"]}.${properties["VERSION_MINOR"]}.${properties["VERSION_PATCH"]}.${properties["BUILD_NUMBER"]}"
println("Application Version: $version")
```

## Common Use Cases

### 1. Development Workflow

```bash
# Make changes to your code
git add .
git commit -m "Add new feature"

# Increment version and build
./gradlew incrementVersion build

# Push changes
git push
```

### 2. Release Preparation

```bash
# Update major/minor/patch versions manually in version.properties
# Then increment build number
./gradlew incrementVersion

# Create release build
./gradlew build
```

### 3. Automated CI/CD

```bash
# In your CI pipeline
./gradlew incrementVersion test build publish
```

## Troubleshooting

### Common Issues

**Issue**: `version.properties not found`

```gradle
⚠️ version.properties not found in /path/to/project
```

**Solution**: Create the `version.properties` file in your project root directory.

**Issue**: Plugin not found

```gradle
Plugin [id: 'io.github.theaniketraj.vista'] was not found
```

**Solution**: Ensure you're using the correct plugin ID and version. Check the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.github.theaniketraj.vista) for the latest version.

**Issue**: Permission denied

```version
Permission denied: version.properties
```

**Solution**: Ensure the `version.properties` file has write permissions.

### Getting Help

If you encounter issues not covered here:

1. Check the [User Guide](./user-guide.md) for detailed usage information
2. Visit our [GitHub Issues](https://github.com/theaniketraj/VISTA/issues) page
3. Review the [Configuration](./configuration.md) guide for advanced setup options

## Next Steps

Now that you have VISTA up and running:

- Explore the [User Guide](./user-guide.md) for advanced usage patterns
- Learn about [Configuration](./configuration.md) options
- Check out [Contributing](./contributing.md) if you'd like to contribute to the project

---

*Congratulations! You've successfully set up VISTA for automated version management.*
