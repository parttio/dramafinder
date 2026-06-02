---
name: vaadin-playwright-test
description: Generate Playwright integration tests for Vaadin 25 views using the DramaFinder library, including element interaction, form validation, grid assertions, and navigation checks. Use when the user wants to write IT tests for a Vaadin view, mentions DramaFinder, or asks about Playwright testing in a Vaadin project.
---

# Vaadin Playwright Test Generator (DramaFinder)

## Best practices

Always follow [@TESTING.md](TESTING.md) when generating tests. Key rules:

- **One test, one assert** — each test method covers a single piece of
  functionality
- **User-facing locators** — prefer label, `aria-label`, `aria-role`, or
  `data-testid` over CSS classes or generated IDs
- **DramaFinder elements for all interactions** — never interact with raw
  locators when a wrapper exists
- **No `Thread.sleep()`** — use Playwright auto-waiting or `waitFor` methods
  instead
- **Assert on user-visible state** — check visibility, text, or
  enabled/disabled, not internal CSS or component state

## Step 1 — Assess project state

Run these checks in parallel before doing anything else:

1. **DramaFinder on classpath?** — grep `pom.xml` for
   `<artifactId>dramafinder</artifactId>`.
2. **Spring Boot app?** — grep `pom.xml` for `spring-boot-starter`.
3. **Existing IT tests?** — look for `*IT.java` files under `src/test/java`.
4. **`SpringPlaywrightIT` already in project?** —
   `find src/test/java -name SpringPlaywrightIT.java`.

### DramaFinder not found — propose setup, then run it

Resolve the latest version (Step 1 of [setup.md](setup.md)) and propose the
following in a single confirmation:

- Add `org.vaadin.addons:dramafinder:<VERSION>` and
  `com.microsoft.playwright:playwright` (test scope) to `pom.xml` with
  `<dramafinder.version>` in `<properties>`.
- **Spring Boot only:** also create
  `src/test/java/<basePackage>/it/support/SpringPlaywrightIT.java`.

On confirmation, execute [setup.md](setup.md) end-to-end, then continue with
Step 2.

### DramaFinder found — follow existing patterns

If existing `*IT.java` files are found, read one or two to understand the
project's conventions (base class, package structure, assertion style, helper
methods) and use them as the template.

If no existing IT tests exist, use the default structure in Step 3.

### `SpringPlaywrightIT` location

- 1 hit → use that fully-qualified class name.
- 0 hits + Spring Boot detected → run setup (it will create the file).
- More than 1 hit → ask the user which one to use.

## Step 2 — Map view components to DramaFinder elements

Read the target view source provided by the user. Extract:

- `@Route("value")` → URL path (default: class name lowercased, stripped of
  `View` suffix, e.g. `PersonView` → `/person`).
- `@PageTitle("...")` → expected page title
- Every interactive component → its DramaFinder wrapper (see table below).
- Form fields → label text used as locator.
- Grids → column headers and row content to assert against.
- Navigation triggers → button labels or menu items that cause route changes.

See [element-mapping.md](element-mapping.md) for the full component → element
class table. Each element also has detailed documentation with examples in
the [specifications folder](https://github.com/parttio/dramafinder/tree/master/docs/specifications).

For components with **no DramaFinder wrapper**, use a plain Playwright locator.
For more complex needs, you can create your own element class extending
`VaadinElement`,
or [open an issue](https://github.com/vaadin/dramafinder/issues) in the
DramaFinder repository to request one.

## Step 3 — Generate the test class

### Default structure (no existing tests to mirror)

```java
package

<same.package.as.view>; // mirror src/test/java structure

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TextFieldElement; // import only used elements

import <basePackage>.it.support.SpringPlaywrightIT; // Spring projects: actual location from Step 1
// import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT; // non-Spring projects

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// omit if not Spring Boot
public class <ViewName>IT extends

SpringPlaywrightIT { // or AbstractBasePlaywrightIT

    @Override
    public String getView () {
        return "/<route-path>";
    }

    @Test
    public void testTitle () {
        assertThat(page).hasTitle("<PageTitle value>");
    }

    // ... component tests below
}
```

Use `SpringPlaywrightIT` if Spring Boot is detected, `AbstractBasePlaywrightIT`
otherwise.

### Component test patterns

**Smoke test (one per component):**

```java
    @Test
public void test<ComponentLabel>(){
TextFieldElement field = TextFieldElement.getByLabel(page, "My Label");
    field.

assertVisible();
    field.

assertLabel("My Label");
    field.

assertValue("");
    field.

setValue("test value");
    field.

assertValue("test value");
}
```

**Form with validation:**

```java

@Test
public void testFormSubmitWithInvalidInput() {
    TextFieldElement nameField = TextFieldElement.getByLabel(page, "Name");
    ButtonElement submitBtn = ButtonElement.getByText(page, "Save");
    nameField.setValue("");
    submitBtn.click();
    nameField.assertInvalid();
    nameField.assertErrorMessage("Field is required");
}

@Test
public void testFormSubmitWithValidInput() {
    TextFieldElement nameField = TextFieldElement.getByLabel(page, "Name");
    ButtonElement submitBtn = ButtonElement.getByText(page, "Save");
    nameField.setValue("Jane Doe");
    submitBtn.click();
    nameField.assertValid();
}
```

**Grid data loading:**

```java

@Test
public void testGridLoadsData() {
    GridElement grid = GridElement.get(page);
    grid.assertRowCount(10); // adjust to expected count
    grid.assertCellContent(0, 0, "Expected cell value");
}
```

## Step 4 — Show generated test, then confirm before writing

Display the full generated test class in a code block. Then ask:

> Shall I write this to `src/test/java/<package>/<ViewName>IT.java`?

Only write the file after explicit confirmation. Place it in `src/test/java`
mirroring the view's package under `src/main/java`.

## Step 5 — Offer to run the test

After writing, ask:

> Do you want me to run this test now with `mvn verify -Dit.test=<ViewName>IT`?

**Warn the user**: the first Vaadin frontend build takes 3–5 minutes. Subsequent
runs are ~25 seconds.