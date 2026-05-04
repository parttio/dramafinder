---
name: DramaFinder Setup
description: How to add DramaFinder and Playwright to a Vaadin 25 Spring Boot project.
---

# Setting Up DramaFinder in Your Vaadin Project

## 1. Add dependencies to `pom.xml`

```xml
<!-- Playwright runtime -->
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <scope>test</scope>
</dependency>

<!-- DramaFinder element wrappers -->
<dependency>
    <groupId>org.vaadin.addons.dramafinder</groupId>
    <artifactId>dramafinder</artifactId>
    <version>LATEST</version>
    <scope>test</scope>
</dependency>
```

Check the latest version at [GitHub releases](https://github.com/vaadin/dramafinder/releases).

## 2. Base test class

All IT tests extend `SpringPlaywrightIT` (Spring Boot) or `AbstractBasePlaywrightIT` (plain):

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MyViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "/my-route";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Expected Title");
    }
}
```

## 3. Run tests

```bash
# Run all IT tests
mvn verify

# Run a specific IT test
mvn verify -Dit.test=MyViewIT
```

> **Note:** The first Vaadin frontend build takes 3–5 minutes. Subsequent runs take ~25 seconds.

## 4. Debugging with a visible browser

To disable headless mode and watch the browser during a test run, use the `headless` property or add a `debug-ui` profile:

```bash
# Via system property
mvn -Dit.test=MyViewIT -Dheadless=false verify

# Via profile (if added to pom.xml)
mvn -Pdebug-ui -Dit.test=MyViewIT verify
```

Add this profile to your `pom.xml` to enable the `-Pdebug-ui` shorthand:

```xml
<profile>
    <id>debug-ui</id>
    <properties>
        <headless>false</headless>
    </properties>
</profile>
```
