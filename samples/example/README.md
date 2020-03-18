# Nixer Plugin Example Application

Example Spring Boot web application demonstrating how to use Nixer Spring Plugin.

The application also serves as a reference implementation used in our integration tests,
which guarantees it is up to date with the latest version of the plugin.

This is a classic _Todo app_ so it consists of only authentication logic and a basic fronted.

## Running

The application can be started as a regular Spring Boot application, that is either by executing from the project root:

```
./gradlew bootRun
```

or executing the main application class, `io.nixer.example.NixerPluginApplication`, from your IDE.

Default credentials are configured in `io.nixer.example.WebSecurityConfig`.

## Integration Tests

The integration tests are implemented with Junit and can be executed as a regular tests from your IDE.

They can be also executed using Gradle:

```
./gradlew integrationTest
``` 
