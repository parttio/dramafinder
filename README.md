# Drama Finder

Drama Finder is a set of helper classes to test a Vaadin application using Playwright

## Usage

## Development instructions

Starting the test/demo server:

```
mvn spring-boot:run
```

This deploys demo at http://localhost:8080
The demo is only here to run the test

## Running tests

To run the unit tests, execute the following command:

```
mvn test
```

## Integration tests

The integration tests are built using Spring Boot, Playwright, and Axe-core.
The tests are located in files ending with `IT.java` in the `sortable-layout-demo` module.
The tests are run with the `maven-failsafe-plugin` when the `it` profile is activated.

To run the integration tests, execute the following command:

```
mvn -Pit verify
```