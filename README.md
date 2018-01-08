# Domain-Driven Design API for Kotlin [![CircleCI](https://circleci.com/gh/nemit/paju-ddd.svg?style=shield)](https://circleci.com/gh/nemit/paju-ddd)

This project explores Domain-Driven Design pattern with Kotlin. 

API features

- Domain-Driven Design base classes
- CQRS
- Event-driven state changes

## Project Structure

Module | Description
------ | -----------
`ddd-core` | Core API for Domain-Driven Design
`examples/sales-order` | Sales order example with Spark and Spring MVC ports 


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

# Publish project artifact

Project artifacts is build with Jitpack automatically: 

- Create a GitHub Release
- That's it!

Snapshots are supported from commit hash and branch https://jitpack.io/docs/#building-with-jitpack

More information: https://jitpack.io/

### Add `ddd-core` dependency to project   
 

**Step 1.** Add the JitPack repository to your build file

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}  
```

**Step 2.** Add the dependency

```
dependencies {
    compile 'com.github.nemit:paju-ddd:ddd-core:[GitHub Release]'
}
```

More information: https://jitpack.io/#nemit/paju-ddd