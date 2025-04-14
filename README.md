# VISTA

**VISTA** *(Version Increment and Semantic Tracking Automation)* is a CLI Gradle plugin designed to automate project version management using a `version.properties` file. Originally developed as part of the Controlled Environment Integration Engine (CEIE) ecosystem, VISTA represents the evolution of our versioning strategy—from initial automation (CEIE 1.0) through enhanced CLI capabilities (CEIE 2.0), GUI integration (CEIE 3.0), and finally, a focused CLI solution (CEIE 4.0: VISTA).

---

## Table of Contents

1. [Features](#features)
2. [Architecture & Internals](#architecture--internals)
3. [Getting Started](#getting-started)
4. [Usage](#usage)
5. [Project Structure](#project-structure)
6. [Plugin Publishing](#plugin-publishing)
7. [Versioning Philosophy](#versioning-philosophy)
8. [Contributing](#contributing)
9. [License](#license)

---

## Features

- **Automated Version Management:**  
  Increment build numbers automatically by updating a `version.properties` file on every build/CI run.
  
- **Semantic Versioning:**  
  Uses four properties—`VERSION_MAJOR`, `VERSION_MINOR`, `VERSION_PATCH`, and `BUILD_NUMBER`—to generate a meaningful version string.
  
- **CLI Integration:**  
  A pure CLI Gradle plugin that lets you run tasks (e.g., `incrementVersion`) directly from the command line.
  
- **CI/CD and Plugin Portal Ready:**  
  Seamlessly integrates with GitHub Actions to build, test, and publish your plugin to the Gradle Plugin Portal.
  
- **Evolution from CEIE:**  
  VISTA is the culmination of enhancements made across the CEIE series:
  - **CEIE 1.0:** Initial automation and Git-integrated version tracking.
  - **CEIE 2.0:** Introduction of CLI tools and incremental version scaffolding.
  - **CEIE 3.0:** GUI (ReFlow) integration for enhanced project visualization.
  - **CEIE 4.0:** Dedicated, streamlined CLI plugin—VISTA—for robust version management.

---

## Architecture & Internals

VISTA is built as a Gradle plugin with the following key components:

- **Plugin ID:**  
  `com.example.vista.versioning`
  
- **Implementation Class:**  
  `com.example.vista.VersioningPlugin`
  
- **Core Tasks:**  
  - `incrementVersion`: Increments the `BUILD_NUMBER` in `version.properties`.
  - (Optional) Additional tasks may be added later (e.g., printing or resetting version information).

- **Publishing Metadata:**  
  Configured via the Gradle Plugin Publish DSL (or via a descriptor file in `META-INF/gradle-plugins/`), including details like website, VCS URL, and tags.

---

## Getting Started

### Prerequisites

- **JDK:** 17 or later
- **Gradle:** 8.11.1 or later
- **Kotlin:** Configured via Gradle (preferably version 1.9.x)

### Environment Setup

1. **Ensure a clean JDK installation:**  
   Avoid duplicate JDK folders and ensure `JAVA_HOME` points to a valid JDK, not a JRE.

2. **Clone the Repository:**  
   ```bash
   git clone https://github.com/yourusername/VISTA.git
   cd VISTA
   ```

3. **Set Up Git and Dependencies:**
    Gradle wrapper and ```gradle.properties``` are already configured for you.

---

## Usage

### Applying the Plugin in Your Project
To use VISTA in a Gradle project, add the following to your project's ```build.gradle.kts``` file:
```kotlin
plugins {
    id("com.example.vista.versioning") version "1.0.0"
}
```

### Running the Version Increment Task
Simply run:
```bash
./gradlew incrementVersion
```
This task will:
- Look for a ```version.properties``` file in the root of your project.
- Increment the ```BUILD_NUMBER``` property.
- Save the updated version information.