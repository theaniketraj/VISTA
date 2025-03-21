# VISTA

**VISTA** (*Version Increment and Semantic Tracking Automation*) is a CLI Gradle plugin that automates project version management using a `version.properties` file.

## Features

- **Automated Version Management:** Increment build numbers automatically.
- **Semantic Versioning:** Uses `VERSION_MAJOR`, `VERSION_MINOR`, `VERSION_PATCH`, and `BUILD_NUMBER` from `version.properties`.
- **CLI Integration:** Run via Gradle tasks for easy version control.

## Getting Started

### Prerequisites

- JDK 17 or later
- Gradle 8.11.1 or later
- Kotlin (configured via Gradle)

### Usage

Apply the plugin in your Gradle build:
```kotlin
plugins {
    id("com.example.vista.versioning") version "1.0.0"
}
```