# Drama Finder

## Documentation

The documentation can be found in this repository and also deployed on https://parttio-dramafinder.mintlify.app/ if you find some errors please file an issue.

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

## Getting started

Add the addon as a test dependency.

```xml

<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

The Drama Finder element classes are built around a plain Playwright `Page`
(or `Locator`). They have no dependency on any particular test base class, so
you can use them with whatever Playwright setup you already have. Just hand a
`Page` to the factory helpers (e.g. `getByLabel`) or to an element constructor.

The simplest way to get started is to let Spring Boot start the server for you
with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and manage the Playwright
`Page` yourself:

```java

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimpleExampleViewIT {

    @LocalServerPort
    private int port;

    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        page = browser.newPage();
        page.navigate(String.format("http://localhost:%d/", port));
    }

    @AfterEach
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    public void testTooltip() {
        // The only thing the element API needs is a Playwright Page
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertVisible();
        textfield.assertTooltipHasText("Tooltip for textfield");
    }
}
```

Letting `@SpringBootTest` start the server is not the only option. You can also
write the tests as plain integration tests (e.g. classes ending in `IT.java`
run by the `maven-failsafe-plugin`) and start/stop the server separately with
Maven — for example using the `spring-boot:start` and `spring-boot:stop` goals
bound to the `pre-integration-test` / `post-integration-test` phases — before
the ITs execute. In that case the test simply navigates to the externally
started server's URL. Either way, the element API only ever needs a Playwright
`Page`, so you are free to obtain it however suits your project.

### Optional: `AbstractBasePlaywrightIT` convenience base class

If you don't already have a Playwright setup, Drama Finder ships an optional
`AbstractBasePlaywrightIT` base class that handles the boilerplate for you:
creating and closing the `Playwright`/`Browser`, opening a fresh `page` and
navigating to your view before each test, waiting for Vaadin to finish loading,
sensible default timeouts, and headless toggling (via the `headless` system
property or `HEADLESS` environment variable). Extending it is purely a
convenience — it is never required to use the element API.

```java

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SimpleExampleViewIT extends AbstractBasePlaywrightIT {

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return String.format("http://localhost:%d/", port);
    }

    @Test
    public void testTooltip() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertVisible();
        textfield.assertTooltipHasText("Tooltip for textfield");
    }
}
```

## Note

The API is in early stage of development.

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

To debug the UI with a visible browser, disable headless mode using either the
`headless` property or the `debug-ui` profile:

```bash
# system property
mvn -Dit.test=ContextMenuViewIT -Dheadless=false verify

# convenient profile
mvn -Pdebug-ui -Dit.test=ContextMenuViewIT verify
```

You will need to add the profile in your pom.xml:

```xml

<profile>
    <id>debug-ui</id>
    <properties>
        <headless>false</headless>
    </properties>
</profile>

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
