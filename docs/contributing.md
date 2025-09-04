# Contributing to VISTA

We welcome contributions to VISTA! Whether you're fixing bugs, adding features, improving documentation, or helping with testing, your contributions make VISTA better for everyone.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Guidelines](#contributing-guidelines)
- [Code Standards](#code-standards)
- [Testing](#testing)
- [Documentation](#documentation)
- [Pull Request Process](#pull-request-process)
- [Community Guidelines](#community-guidelines)

## Getting Started

### Ways to Contribute

- **Bug Reports**: Found a bug? Let us know!
- **Feature Requests**: Have an idea for improvement? Share it!
- **Code Contributions**: Fix bugs or implement new features
- **Documentation**: Improve or expand our documentation
- **Testing**: Help test new features and report issues
- **Community Support**: Help other users in discussions

### Before You Start

1. **Check existing issues**: Look through [GitHub Issues](https://github.com/theaniketraj/VISTA/issues) to see if your bug or feature request already exists
2. **Read the documentation**: Familiarize yourself with VISTA's functionality
3. **Join the discussion**: Participate in existing discussions or start new ones

## Development Setup

### Prerequisites

- **JDK**: Version 17 or later
- **Gradle**: Version 8.11.1 or later
- **Git**: For version control
- **IDE**: IntelliJ IDEA (recommended) or any Kotlin-compatible IDE

### Fork and Clone

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:

   ```bash
   git clone https://github.com/YOUR_USERNAME/VISTA.git
   cd VISTA
   ```

3. **Add upstream remote**:

   ```bash
   git remote add upstream https://github.com/theaniketraj/VISTA.git
   ```

### Build the Project

1. **Build VISTA**:

   ```bash
   ./gradlew build
   ```

2. **Run tests**:

   ```bash
   ./gradlew test
   ```

3. **Publish to local repository** (for testing):

   ```bash
   ./gradlew publishToMavenLocal
   ```

### Project Structure

```pgsql
VISTA/
├── vista-plugin/                    # Main plugin module
│   ├── src/main/kotlin/            # Plugin source code
│   │   └── com/example/vista/
│   │       └── VersioningPlugin.kt # Main plugin class
│   ├── src/test/kotlin/            # Test source code
│   └── build.gradle.kts            # Plugin build configuration
├── docs/                           # Documentation
├── gradle/                         # Gradle wrapper
├── settings.gradle.kts             # Project settings
├── version.properties              # Version configuration
└── README.md                       # Project README
```

## Contributing Guidelines

### Issue Reporting

When reporting bugs or requesting features:

#### Bug Reports

Use the bug report template and include:

- **VISTA version**: Which version are you using?
- **Gradle version**: What Gradle version?
- **JDK version**: Which JDK?
- **Operating System**: Windows, macOS, Linux?
- **Steps to reproduce**: Clear, step-by-step instructions
- **Expected behavior**: What should happen?
- **Actual behavior**: What actually happens?
- **Error messages**: Include full stack traces
- **Sample project**: If possible, provide a minimal reproduction case

**Example Bug Report:**

```markdown
**VISTA Version:** 1.0.7
**Gradle Version:** 8.11.1
**JDK Version:** OpenJDK 17
**OS:** Ubuntu 22.04

**Description:**
The incrementVersion task fails when version.properties contains non-ASCII characters.

**Steps to Reproduce:**
1. Create version.properties with content: `BUILD_NUMBER=1ñ`
2. Run `./gradlew incrementVersion`

**Expected:** Task should handle or report invalid characters gracefully
**Actual:** Task fails with NumberFormatException

**Error Message:**

```java
java.lang.NumberFormatException: For input string: "1ñ"

```

#### Feature Requests

Include:

- **Use case**: Why do you need this feature?
- **Proposed solution**: How should it work?
- **Alternatives considered**: What other approaches did you consider?
- **Additional context**: Any other relevant information

### Code Contributions

#### Branching Strategy

1. **Create a feature branch** from `main`:

   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Use descriptive branch names**:
   - `feature/add-version-validation`
   - `bugfix/fix-properties-parsing`
   - `docs/update-getting-started`

#### Commit Messages

Follow conventional commit format:

```gradle
type(scope): description

[optional body]

[optional footer]
```

**Types:**

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**

```bash
feat(plugin): add version validation task
fix(increment): handle missing properties file gracefully
docs(readme): update installation instructions
test(plugin): add tests for custom property names
```

## Code Standards

### Kotlin Style Guide

Follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html):

#### Naming Conventions

```kotlin
// Classes: PascalCase
class VersioningPlugin

// Functions and variables: camelCase
fun incrementVersion()
val buildNumber = 1

// Constants: SCREAMING_SNAKE_CASE
const val DEFAULT_BUILD_NUMBER = 1

// Files: PascalCase matching the main class
// VersioningPlugin.kt
```

#### Code Formatting

```kotlin
// Good: Clear and readable
class VersioningPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("incrementVersion", DefaultTask::class.java) { task ->
            task.doLast {
                incrementBuildNumber(target)
            }
        }
    }
    
    private fun incrementBuildNumber(project: Project) {
        val versionFile = File(project.rootDir, "version.properties")
        // Implementation...
    }
}
```

#### Documentation

Document public APIs:

```kotlin
/**
 * VISTA Gradle plugin for automated version management.
 * 
 * This plugin provides tasks for incrementing version numbers
 * stored in a version.properties file.
 */
class VersioningPlugin : Plugin<Project> {
    
    /**
     * Applies the VISTA plugin to the given project.
     * 
     * @param target The project to apply the plugin to
     */
    override fun apply(target: Project) {
        // Implementation...
    }
}
```

### Error Handling

Handle errors gracefully:

```kotlin
// Good: Proper error handling
fun incrementVersion(project: Project) {
    val versionFile = File(project.rootDir, "version.properties")
    
    if (!versionFile.exists()) {
        project.logger.warn("⚠️ version.properties not found in ${project.rootDir.absolutePath}")
        return
    }
    
    try {
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        
        val currentBuild = properties.getProperty("BUILD_NUMBER", "0").toIntOrNull() ?: 0
        // Continue with increment...
        
    } catch (e: IOException) {
        project.logger.error("Failed to read version.properties: ${e.message}")
        throw GradleException("Version increment failed", e)
    }
}
```

## Testing

### Writing Tests

Create comprehensive tests for new features:

```kotlin
class VersioningPluginTest {
    
    @Test
    fun `incrementVersion should increment build number`() {
        // Given
        val project = ProjectBuilder.builder().build()
        val versionFile = File(project.rootDir, "version.properties")
        versionFile.writeText("BUILD_NUMBER=1")
        
        project.plugins.apply(VersioningPlugin::class.java)
        
        // When
        val task = project.tasks.getByName("incrementVersion")
        task.actions.forEach { it.execute(task) }
        
        // Then
        val properties = Properties().apply {
            versionFile.inputStream().use { load(it) }
        }
        assertEquals("2", properties.getProperty("BUILD_NUMBER"))
    }
    
    @Test
    fun `incrementVersion should handle missing file gracefully`() {
        // Given
        val project = ProjectBuilder.builder().build()
        project.plugins.apply(VersioningPlugin::class.java)
        
        // When/Then - should not throw exception
        val task = project.tasks.getByName("incrementVersion")
        assertDoesNotThrow {
            task.actions.forEach { it.execute(task) }
        }
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests VersioningPluginTest

# Run with verbose output
./gradlew test --info
```

### Test Coverage

Maintain good test coverage:

```bash
# Generate coverage report
./gradlew jacocoTestReport

# View coverage report
open vista-plugin/build/reports/jacoco/test/html/index.html
```

## Documentation Guidelines

### Types of Documentation

1. **Code Documentation**: Inline comments and KDoc
2. **User Documentation**: Guides and tutorials
3. **API Documentation**: Generated from code
4. **Examples**: Sample projects and use cases

### Writing Documentation

#### User Guides

- Use clear, concise language
- Include practical examples
- Provide step-by-step instructions
- Test all examples

#### Code Examples

```kotlin
// Good: Complete, runnable example
plugins {
    id("io.github.theaniketraj.vista") version "1.0.7"
}

tasks.named("build") {
    dependsOn("incrementVersion")
}
```

#### Markdown Guidelines

- Use proper heading hierarchy
- Include table of contents for long documents
- Use code blocks with language specification
- Add links to related sections

## Pull Request Process

### Before Submitting

1. **Update your branch**:

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run tests**:

   ```bash
   ./gradlew test
   ```

3. **Check code style**:

   ```bash
   ./gradlew ktlintCheck
   ```

4. **Update documentation** if needed

### Pull Request Template

Use this template for your PR description:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Refactoring
- [ ] Other (please describe)

## Testing
- [ ] Tests pass locally
- [ ] Added new tests for changes
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes (or clearly documented)

## Related Issues
Fixes #123
```

### Review Process

1. **Automated checks** must pass
2. **Code review** by maintainers
3. **Testing** in different environments
4. **Documentation review** if applicable
5. **Final approval** and merge

### After Merge

1. **Delete your feature branch**:

   ```bash
   git branch -d feature/your-feature-name
   git push origin --delete feature/your-feature-name
   ```

2. **Update your local main**:

   ```bash
   git checkout main
   git pull upstream main
   ```

## Community Guidelines

### Code of Conduct

We follow the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/). Please read and follow these guidelines:

- **Be respectful** and inclusive
- **Be collaborative** and constructive
- **Be patient** with newcomers
- **Focus on the issue**, not the person

### Communication

- **GitHub Issues**: For bug reports and feature requests
- **GitHub Discussions**: For questions and general discussion
- **Pull Request Comments**: For code-specific discussions

### Recognition

Contributors are recognized in:

- **CHANGELOG.md**: For significant contributions
- **README.md**: For major contributors
- **Release notes**: For feature contributions

## Getting Help

### Resources

- **Documentation**: Read the full documentation
- **Examples**: Check the examples directory
- **Issues**: Search existing issues
- **Discussions**: Join community discussions

### Contact

- **GitHub Issues**: For bugs and features
- **GitHub Discussions**: For questions
- **Email**: For security issues or private matters

## Development Workflow

### Typical Contribution Flow

1. **Find or create an issue**
2. **Fork and clone** the repository
3. **Create a feature branch**
4. **Make your changes**
5. **Write tests**
6. **Update documentation**
7. **Submit a pull request**
8. **Address review feedback**
9. **Celebrate your contribution!** 🎉

### Release Process

For maintainers:

1. **Update version** in `version.properties`
2. **Update CHANGELOG.md**
3. **Create release tag**
4. **Publish to Gradle Plugin Portal**
5. **Update documentation**

---

Thank you for contributing to VISTA! Your efforts help make version management easier for developers everywhere. 🚀

*For questions about contributing, please open a discussion or contact the maintainers.*
