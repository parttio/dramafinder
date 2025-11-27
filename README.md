# Drama Finder

## Usage

Drama Finder is a set of helper classes to test a Vaadin application using
Playwright.
It gives you access to a list of assertions and actions you can do for our
components.

For example:

```java

@Test
public void testTooltip() {
    // get a text that has an accessible name (label or aria label,...) equals to text field
    TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
    // assert that is visible
    textfield.assertVisible();
    // assert that the textfield has a tooltip
    textfield.assertTooltipHasText("Tooltip for textfield");
}
```

It also gives you some inner locators.

```java

@Test
public void testHelper() {
    // get a text that has an accessible name (label or aria label,...) equals to text field
    TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with helper component");
    // Use the helperLocator to get the TextField added as a helper component
    TextFieldElement helperComponent = new TextFieldElement(textfield.getHelperLocator());
    // You can use Playwright assertions
    assertThat(helperComponent.getHelperLocator()).hasText("Internal helper");
    // or reuse the locator with the API

}

```

## Note

The API is in early stage of development and a lot of components are not yet
covered.

If you notice something missing please create a ticket or a Pull Request.

The tests in the demo application is not meant to be a best practice since it's
primarly here to test the API.
For example it will test the getter like `getMinLength` without waiting which is
a bad practice.

```java

@Test
public void testPattern() {
    // use Playwright assertion to wait
    textfield.assertMinLength(6);
    // doesn't wait but will test `getMinLength`, use the previous in your code
    assertEquals(6, textfield.getMinLength());
}
```

## Development instructions

Starting the test/demo server:

```bash
mvn spring-boot:run
```

This deploys demo at http://localhost:8080
The demo is only here to run the test

### Javadoc

Public APIs in the `dramafinder` module are documented with concise Javadoc:
- Element classes include a short summary referencing the underlying Vaadin
  tag (e.g., `vaadin-text-field`) and any noteworthy behaviors.
- Public methods document parameters, return values, and null semantics.
- Factory helpers (e.g., `getByLabel`) note the ARIA role used to locate the
  element.

## Running tests

To run the unit tests, execute the following command:

```bash
mvn test
```

## Integration tests

The integration tests are built using Spring Boot, Playwright, and Axe-core.
The tests are located in files ending with `IT.java` in the
`sortable-layout-demo` module.
The tests are run with the `maven-failsafe-plugin` when the `it` profile is
activated.

To run the integration tests, execute the following command:

```bash
mvn -Pit verify
```

## How to use it

Add the addon as a test dependency.

```xml

<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

With Spring Boot you can copy the `SpringPlaywrightIT` then create a simple
test:

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

## Cutting a release

Before cutting a release, make sure the build passes properly locally and in
GitHub Actions based verification build.

To tag a release and increment versions, issue:

```bash
mvn release:prepare release:clean
```

Answer questions, defaults most often fine. Note that release:perform is not
needed as there is a GitHub Action is set up build and to push release to Maven
Central automatically.

Directory will automatically pick up new releases within about half an hour, but
if browser or Vaadin version support change, be sure to adjust the metadata in
Vaadin Directory UI.
