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
from your favourite package manager or by downloading from
[AdoptOpenJDK](https://adoptopenjdk.net/).
- Version 7 of [Gradle](https://gradle.org/). If you do not have Gradle
installed, it is easy to do so using a package manager (for example
[`scoop`](https://scoop.sh/) in Windows or [SDKMAN!](https://sdkman.io/) on Linux).

It is a good idea to create a Gradle wrapper, especially if you want to use a
[IDE](https://en.wikipedia.org/wiki/Integrated_development_environment)
(otherwise it is not really necessary). To create it, run
`gradle wrapper --gradle-version 7.0.2` (or any Gradle version later than 7.0)
at the tokenproducer sources directory.

<br/>

### Building with Gradle

Run `gradle build` to build. For example:

```shell
git clone https://github.com/css4j/tokenproducer.git
cd tokenproducer
gradle build
```
or
```shell
git clone https://github.com/css4j/tokenproducer.git
cd tokenproducer
gradle wrapper --gradle-version 7.0.2
gradle build
```
if you want to create a wrapper (only need to do that once!).

<br/>

### Deploying to a Maven repository

Use:
- `gradle build publishToMavenLocal` to install in your local Maven repository.
- `gradle publish` to deploy to a (generally remote) Maven repository.

Before deploying to a remote Maven repository, please read the
`publishing.repositories.maven` block of
[carte.java-conventions.gradle](https://github.com/css4j/tokenproducer/blob/master/buildSrc/src/main/groovy/carte.java-conventions.gradle)
to learn which properties you need to set (like `mavenReleaseRepoUrl`or
`mavenRepoUsername`), either at the [command line](https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties)
(`-P` option) or your `GRADLE_USER_HOME/gradle.properties` file.
