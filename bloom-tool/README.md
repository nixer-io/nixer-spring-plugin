# bloom-tool

A simple command line utility to manipulate file-based bloom filters. 
It can efficiently handle bloom filters that require multi-GB data storage (e.g. with billions of insertions and low false positive rates).

## Building

The tool is build using Gradle [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html).
```
./gradlew
```
builds whole project and places the tool package under `build/distributions` directory.

## Using

Unzip the application package and invoke:

```
bin/bloom-tool --help
```
