# VISTA

**VISTA** *(Version Increment and Semantic Tracking Automation)* is a CLI Gradle plugin designed to automate semantic versioning for your projects. It seamlessly manages your project's versioning via a property file, supports environment variable overrides for CI/CD, and integrates directly with the Gradle build lifecycle.

---

## Features

- **Automated SemVer Management**:
  - `incrementMajor`: 1.0.0 -> 2.0.0
  - `incrementMinor`: 1.2.0 -> 1.3.0
  - `incrementPatch`: 1.2.3 -> 1.2.4
  - `incrementBuild`: 1.2.3.4 -> 1.2.3.5

- **Project Integration**:
  - Automatically sets `project.version` during configuration.
  - Compatible with other plugins that rely on standard Gradle version properties.

- **Flexible Configuration**:
  - Customize the version file name (default: `version.properties`).
  - Override any version component via environment variables (e.g., `VISTA_BUILD_NUMBER`).

- **CI/CD Ready**:
  - Ideal for GitHub Actions, GitLab CI, and other pipelines.
  - Inject build numbers or version overrides dynamically.

---

## Installation

Add the plugin to your `plugins` block in `build.gradle.kts`:

```kotlin
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}
```

Ensure you have a `version.properties` file in your root project directory:

```properties
VERSION_MAJOR=1
VERSION_MINOR=0
VERSION_PATCH=0
BUILD_NUMBER=0
```

---

## Usage

### Tasks

Run VISTA tasks directly from the command line:

```bash
./gradlew incrementMajor   # Bump Major (1.0.0 -> 2.0.0)
./gradlew incrementMinor   # Bump Minor (1.2.0 -> 1.3.0)
./gradlew incrementPatch   # Bump Patch (1.2.3 -> 1.2.4)
./gradlew incrementBuild   # Bump Build Number (1.2.3.4 -> 1.2.3.5)
./gradlew printVersion     # Print current version (1.2.3.4)
```

### Configuration

Customize VISTA in your `build.gradle.kts`:

```kotlin
vista {
    // Optional: Change the properties file name
    versionFileName.set("my-project-version.properties")
}
```

### CI/CD Integration

VISTA supports environment variable overrides, perfect for CI/CD pipelines.

**Environment Variables:**

- `VISTA_VERSION_MAJOR`
- `VISTA_VERSION_MINOR`
- `VISTA_VERSION_PATCH`
- `VISTA_BUILD_NUMBER`

**Example (GitHub Actions):**

```yaml
- name: Build with Version
  run: ./gradlew build
  env:
    VISTA_BUILD_NUMBER: ${{ github.run_number }}
```

This will automatically set your Gradle project version to align with your CI build number.

---

## Documentation

For more detailed information, check out the [docs/](docs/) folder:

- [Usage Guide](docs/usage.md)
- [Configuration](docs/configuration.md)
- [Contributing](docs/contributing.md)

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
