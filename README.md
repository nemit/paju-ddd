# pajulahti-domain-demo
Demo io.paju.templateservice.repository for proposed architecture and domain io.paju.templateservice.domain prototyping


# Build

Project build is done with Gradle.

## How to build
```bash
$ ./gradlew clean compile
```

## About Gradle configuration 

- Uses Gradle wrapper
- Project dependencies are defined in the `gradle/dependencies.gradle` file

## Kotlin linter

Kotlin linter is <https://ktlint.github.io>

### Check format

Check format with task `ktlintCheck`

Checkstyle reports are generated to file `build\reports\ktlint\ktlint.xml` per 
project. 

```bash
$ ./gradlew ktlintCheck
```

### Format source

Format source set with task `ktlintFormat`. 

```bash
$ ./gradlew ktlintFormat
```

### Apply formatting rules to IDEA

Make Intellij IDEA's built-in formatter produce 100% ktlint-compatible code with task `ktlintToIdea`

```bash
$ ./gradlew ktlintToIdea
```
