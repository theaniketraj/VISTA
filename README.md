# VISTA

**VISTA** *(Version Increment and Semantic Tracking Automation)* is a CLI Gradle plugin designed to automate project version management using a `version.properties` file. Originally developed as part of the Controlled Environment Integration Engine (CEIE) ecosystem, VISTA represents the evolution of our versioning strategy—from initial automation (CEIE 1.0) through enhanced CLI capabilities (CEIE 2.0), (CEIE 3.0), and finally, a focused CLI solution (CEIE 4.0: VISTA).

---

## Table of Contents

- [VISTA](#vista)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Architecture \& Internals](#architecture--internals)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Environment Setup](#environment-setup)
  - [Usage](#usage)
    - [Applying the Plugin in Your Project](#applying-the-plugin-in-your-project)
    - [Running the Version Increment Task](#running-the-version-increment-task)
  - [Project Structure](#project-structure)
    - [Key Points in the Structure](#key-points-in-the-structure)
  - [Versioning Philosophy](#versioning-philosophy)
  - [Contributing](#contributing)
  - [License](#license)

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
   git clone https://github.com/theaniketraj/VISTA.git
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

---

## Project Structure

```pgsql
VISTA/
├── vista-plugin/                // VISTA Gradle Plugin module
│   ├── build.gradle.kts         // Plugin module build script (plugin & publishing configuration)
│   ├── src/
│   │   └── main/
│   │       └── kotlin/
│   │           └── com/example/vista/
│   │                   └── VersioningPlugin.kt  // Plugin implementation
│   └── src/main/resources/
│       └── META-INF/
│           └── gradle-plugins/
│               └── com.example.vista.versioning.properties  // Plugin descriptor
├── gradle/                      // Gradle wrapper files
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── settings.gradle.kts          // Project settings & module inclusion
├── gradle.properties            // Global Gradle configuration (JVM args, code style, etc.)
├── version.properties           // Version configuration file (used by the plugin)
├── README.md                    // This documentation file
└── .gitignore                   // Git ignore settings
```

### Key Points in the Structure

- vista-plugin/: Contains the source code and publishing configuration for the VISTA Gradle plugin.
- version.properties: Maintains the current version details (used by VISTA to increment version numbers).
- Gradle Wrapper & Settings: Ensure a consistent build environment and repository configuration

---

## Versioning Philosophy

VISTA uses a simple yet robust versioning strategy:

- Semantic Versioning:
```VERSION_MAJOR```,```VERSION_MINOR```,```VERSION_PATCH``` combined with an incrementing ```BUILD_NUMBER``` to create a complete version string.
- Autumation:
The ```incrementVersion``` task updates the build number automatically - minimizing human error.
- Integrability:
Designed to work seamlessly with CI/CD pipelines for consistent version updates in each build.

---

## Contributing

Contributions are welcome! Please follow these guidelines:

- Fork the Repository and create a new branch for your changes.
- Ensure Code Quality:
Adhere to the Kotlin code style (official) and ensure all tests pass.
- Submit a Pull Request:
Include a detailed description of your changes and follow the project's commit conventions.

---

## License

This project is licensed under the [MIT License](https://github.com/theaniketraj/VISTA/blob/main/LICENSE)
