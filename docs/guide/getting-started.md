# Getting started

This page takes you from an existing Vaadin 25 application to a first green
DramaFinder test.

## 1. Add the dependency

```xml
<dependency>
    <groupId>org.vaadin.addons</groupId>
    <artifactId>dramafinder</artifactId>
    <version>2.0.2</version>
    <scope>test</scope>
</dependency>
```

DramaFinder brings Playwright for Java with it. Declare
`com.microsoft.playwright:playwright` yourself only if you want to pin another
Playwright version. Spring and Vaadin are **not** added to your classpath:
DramaFinder declares them `provided` and uses what your application already
has.

Playwright downloads its browser binaries the first time it starts. On a CI
machine, cache `~/.cache/ms-playwright` between builds, or install the
browsers ahead of time:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps chromium"
```

## 2. Run the tests as integration tests

Browser tests are integration tests: they need the application running. Name
them `*IT.java` and let the Failsafe plugin run them in the
`integration-test` phase:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

```bash
mvn verify                          # every IT
mvn verify -Dit.test=ContactFormIT  # a single test class
```

## 3. Write the first test

Given a view:

```java
@Route("contact")
public class ContactView extends VerticalLayout {
    public ContactView() {
        TextField name = new TextField("Name");
        Button send = new Button("Send", e -> Notification.show("Thanks " + name.getValue()));
        add(name, send);
    }
}
```

=== "Spring Boot"

    A Spring Boot application's test extends `SpringPlaywrightIT`. It starts
    the application on a random port (it carries `@SpringBootTest`), opens a
    fresh browser page before each test and navigates it to `getView()`.

    ```java
    import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;
    import org.vaadin.addons.dramafinder.element.*;

    public class ContactViewIT extends SpringPlaywrightIT {

        @Override
        public String getView() {
            return "contact";
        }

        @Test
        public void sendShowsAThankYouNotification() {
            TextFieldElement.getByLabel(page, "Name").setValue("Ada");
            ButtonElement.getByText(page, "Send").click();
            NotificationElement.getByText(page, "Thanks Ada").assertOpen();
        }
    }
    ```

    `SpringPlaywrightIT` needs `spring-boot-starter-test` in test scope, which
    every Spring Boot project already has.

=== "Any server"

    Without Spring Boot, extend `AbstractBasePlaywrightIT` and tell it where the
    application runs. Start the server however you like before the ITs run,
    e.g. with the `jetty:start` / `spring-boot:start` goals bound to
    `pre-integration-test`.

    ```java
    import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;

    public class ContactViewIT extends AbstractBasePlaywrightIT {

        @Override
        public String getUrl() {
            return "http://localhost:8080/";
        }

        @Override
        public String getView() {
            return "contact";
        }

        @Test
        public void sendShowsAThankYouNotification() {
            TextFieldElement.getByLabel(page, "Name").setValue("Ada");
            ButtonElement.getByText(page, "Send").click();
            NotificationElement.getByText(page, "Thanks Ada").assertOpen();
        }
    }
    ```

=== "Your own Playwright setup"

    The base classes are a convenience. Every element only needs a Playwright
    `Page` (or a `Locator`), so it works with whatever setup you already have:

    ```java
    @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
    public class ContactViewIT {

        @LocalServerPort
        int port;

        static Playwright playwright;
        static Browser browser;
        Page page;

        @BeforeAll
        static void startBrowser() {
            playwright = Playwright.create();
            browser = playwright.chromium().launch();
        }

        @AfterAll
        static void stopBrowser() {
            playwright.close();
        }

        @BeforeEach
        void openView() {
            page = browser.newPage();
            page.navigate("http://localhost:" + port + "/contact");
            AutoWait.waitForVaadinIdle(page);
        }

        @AfterEach
        void closePage() {
            page.close();
        }

        @Test
        void sendShowsAThankYouNotification() {
            TextFieldElement.getByLabel(page, "Name").setValue("Ada");
            ButtonElement.getByText(page, "Send").click();
            NotificationElement.getByText(page, "Thanks Ada").assertOpen();
        }
    }
    ```

    You lose the extras of the base classes: navigation by route class, the
    Spring Security sign-in and the failure report.

Run it:

```bash
mvn verify -Dit.test=ContactViewIT
```

## What the base class does for you

`AbstractBasePlaywrightIT` (and `SpringPlaywrightIT`, which extends it):

- starts one Playwright and one Chromium per test class (`@BeforeAll`) and
  closes them afterwards;
- before each test, opens a new `page` in a fresh browser context, calls
  `prepareBrowserContext(context)` (the place for cookies or a sign-in),
  navigates to `getUrl() + getView()` and waits until Vaadin is idle;
- sets Playwright's default timeout to 15 s (navigation: 4 s);
- closes the page after each test;
- on failure, writes a report with a screenshot, the components on the page and
  a lookup for each of them (see [Debugging failed tests](debugging.md)).

!!! warning "Overriding the lifecycle methods"

    `setup()` / `cleanup()` are `@BeforeAll` / `@AfterAll` and `setupTest()` /
    `cleanupTest()` are `@BeforeEach` / `@AfterEach`. If you override one, call
    `super`, otherwise `page` stays `null`. To do something before each test,
    prefer your own `@BeforeEach` method: JUnit runs it after the base class's.

```java
@BeforeEach
void openTheEditor() {
    ButtonElement.getByText(page, "New contact").click();
}
```

## Watching the browser

Tests run headless. To watch them, set the `headless` system property (or the
`HEADLESS` environment variable) to `false`:

```bash
mvn verify -Dit.test=ContactViewIT -Dheadless=false
```

A Maven profile makes it shorter:

```xml
<profile>
    <id>debug-ui</id>
    <properties>
        <headless>false</headless>
    </properties>
</profile>
```

```bash
mvn verify -Pdebug-ui -Dit.test=ContactViewIT
```

To step through a test, put `page.pause()` where you want to stop: Playwright
opens its inspector, from which you can resume or record locators.

## Next steps

- [Core concepts](core-concepts.md) — elements, factories, locators and mixins.
- [Finding components](finding-components.md) — labels, accessible names, ids
  and the typed query builder.
- [Assertions](assertions.md) — why every check should be an `assertX` call.
- [Recipes](recipes.md) — forms, grids, dialogs and notifications end to end.
- [Components](../specifications/index.md) — one page per Vaadin component.
