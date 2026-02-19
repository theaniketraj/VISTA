# Usage Guide

## Tasks

VISTA provides a set of Gradle tasks to manage your project's version. You can run these tasks from the command line using `./gradlew` (or `gradlew.bat` on Windows).

### `incrementMajor`

Increments the `VERSION_MAJOR` property by 1.

- Resets `VERSION_MINOR` to 0.
- Resets `VERSION_PATCH` to 0.
- Resets `BUILD_NUMBER` to 0.

**Example:**
`1.2.3.4` -> `2.0.0.0`

```bash
./gradlew incrementMajor
```

### `incrementMinor`

Increments the `VERSION_MINOR` property by 1.

- Resets `VERSION_PATCH` to 0.
- Resets `BUILD_NUMBER` to 0.
- Keeps `VERSION_MAJOR` unchanged.

**Example:**
`1.2.3.4` -> `1.3.0.0`

```bash
./gradlew incrementMinor
```

### `incrementPatch`

Increments the `VERSION_PATCH` property by 1.

- Resets `BUILD_NUMBER` to 0.
- Keeps `VERSION_MAJOR` and `VERSION_MINOR` unchanged.

**Example:**
`1.2.3.4` -> `1.2.4.0`

```bash
./gradlew incrementPatch
```

### `incrementBuild`

Increments the `BUILD_NUMBER` property by 1.

- Keeps `VERSION_MAJOR`, `VERSION_MINOR`, and `VERSION_PATCH` unchanged.

**Example:**
`1.2.3.4` -> `1.2.3.5`

```bash
./gradlew incrementBuild
```

*(Also available via alias `incrementVersion`)*

### `printVersion`

Prints the current project version to the console. This is useful for checking the active version, including any environment variable overrides.

**Example:**

```bash
./gradlew printVersion
> Task :printVersion
Project Version: 1.2.3.4
File Version:    1.2.3.4
```

## Project Version Integration

When you apply the VISTA plugin, it automatically sets the standard Gradle `project.version` property during the configuration phase.

This means you can use `project.version` anywhere in your build scripts without manual parsing:

```kotlin
// build.gradle.kts
version = project.version // Set by VISTA

println("Configuring project version: ${project.version}")

tasks.register("myTask") {
    doLast {
        println("Running task for version: ${project.version}")
    }
}
```

This integration allows VISTA to work seamlessly with other plugins (like `maven-publish` or `com.android.application`) that rely on `project.version` being set correctly.

## CI/CD Workflow Example

A typical workflow in a CI pipeline (e.g., GitHub Actions):

1. **Checkout Code:** The CI runner checks out your repository.
2. **Configuration:** Environment variables are set (e.g., build number from the CI trigger).
3. **Build:** Run `./gradlew build`. VISTA detects the environment variables and sets `project.version` accordingly (e.g., `1.0.0.123`).
4. **Publish:** When publishing artifacts, they will be versioned using the CI build number automatically.
5. **Tagging/Release (Optional):** If a release is triggered, you might run `./gradlew incrementMinor` and push the updated `version.properties` back to the repository to prepare for the next development cycle.
