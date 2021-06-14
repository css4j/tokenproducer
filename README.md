# TokenProducer-uParser

A small event parser.

It is at the core of the css4j NSAC parser.

<br/>

## Building from source

### Requirements

To build this project, you need the following software installed:

- The [Git version control system](https://git-scm.com/downloads) is required to
obtain the sources. Any recent version should suffice.
- A recent modular Java JDK (version 16 is being used to build). You can install it
from your favourite package manager (for example [`scoop`](https://scoop.sh/) in
Windows or [SDKMAN!](https://sdkman.io/) on Linux) or by downloading from
[AdoptOpenJDK](https://adoptopenjdk.net/).


<br/>

### Building with Gradle

At the tokenproducer sources directory, run `gradlew build` to build. For example:

```shell
git clone https://github.com/css4j/tokenproducer.git
cd tokenproducer
gradlew build
```
or
```shell
git clone https://github.com/css4j/tokenproducer.git
cd tokenproducer
./gradlew build
```
on Unix-like shells (where the current directory is generally not in the `PATH`).

<br/>

### Deploying to a Maven repository

Use:
- `gradlew build publishToMavenLocal` to install in your local Maven repository.
- `gradlew publish` to deploy to a (generally remote) Maven repository.

Before deploying to a remote Maven repository, please read the
`publishing.repositories.maven` block of
[carte.java-conventions.gradle](https://github.com/css4j/tokenproducer/blob/master/buildSrc/src/main/groovy/carte.java-conventions.gradle)
to learn which properties you need to set (like `mavenReleaseRepoUrl`or
`mavenRepoUsername`), either at the [command line](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties)
(`-P` option) or your `GRADLE_USER_HOME/gradle.properties` file.
