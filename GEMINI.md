# Drama Finder Project Overview

This document provides an overview of the Drama Finder project, its purpose, and key development aspects relevant for automated agents.

## Project Purpose

Drama Finder is a set of helper classes designed to facilitate testing of Vaadin applications using Playwright. It offers a collection of assertions and actions for Vaadin components, simplifying the creation of robust integration tests.

## Key Technologies

*   **Vaadin:** Frontend framework for web applications.
*   **Playwright:** A Node.js library to automate Chromium, Firefox and WebKit with a single API. Used here for browser automation and testing.
*   **Spring Boot:** Used for the test/demo server and integration tests.
*   **Maven:** Project build automation tool.
*   **JUnit:** Testing framework.
*   **Axe-core:** Used for accessibility testing within integration tests.

## Project Structure Highlights

*   `src/main/java/`: Contains the core Java helper classes for Vaadin and Playwright integration.
*   `src/test/java/`: Contains unit and integration tests for the Drama Finder library itself, as well as demo application tests.
*   `pom.xml`: Maven project configuration, including dependencies and build profiles.

## Development & Testing

### Starting the Demo Server

To run the demo application (used for testing the Drama Finder library):

```bash
mvn spring-boot:run
```

The demo will be available at `http://localhost:8080`.

### Running Unit Tests

```bash
mvn test
```

### Running Integration Tests

Integration tests are located in `src/test/java/.../it/` and are run with the `it` Maven profile.

```bash
mvn -Pit verify
```

## How to Use Drama Finder

To incorporate Drama Finder into a Vaadin project, add it as a test dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

Example of an integration test:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimpleExampleViewIT extends SpringPlaywrightIT {
    @Test
    public void testTooltip() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertVisible();
        textfield.assertTooltipHasText("Tooltip for textfield");
    }
}
```
