---
name: vaadin-playwright-test
description: Generate Playwright integration tests for Vaadin 25 views using the DramaFinder library, including element interaction, form validation, grid assertions, and navigation checks. Use whenever you are about to write, edit, or run an integration/IT test for a Vaadin view — including when the requirement comes from a GitHub issue, PR, spec, or ticket rather than the user's direct words (e.g. "implement #85", "fix the failing test", "add coverage for this view"). Also use when the user mentions DramaFinder, Playwright, `SpringPlaywrightIT`, `@PlaywrightElement`, files under `src/test/**/tests/it/`, or asks about Playwright testing in a Vaadin project. If a task in a Vaadin project involves any Playwright/IT test, load this skill before writing the test.
---

# Vaadin Playwright Test Generator (DramaFinder)

## Best practices

Always follow [@TESTING.md](TESTING.md) when generating tests. Key rules:

- **One test, one assert** — each test method covers a single piece of
  functionality
- **User-facing locators** — prefer label, `aria-label`, `aria-role`, or
  `data-testid` over CSS classes or generated IDs
- **DramaFinder elements for all interactions AND assertions** — if a wrapper
  exists for the component (see [element-mapping.md](element-mapping.md)), you
  MUST use it. Never reach into the component's internal DOM with a raw
  Playwright locator (e.g. `grid.locator("vaadin-grid-cell-content")`,
  `combo.locator("vaadin-combo-box-item")`). These shadow/light-DOM tags are
  implementation details — the wrapper already exposes the count, content, and
  state you need. Raw locators are a **last resort**, allowed only for a
  component that has no wrapper at all (see Step 2).
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
class table, and [api-reference.md](api-reference.md) for the **complete public
API** (every element, its methods, signatures and one-line descriptions) of the
version you have installed.

> **Never download or unzip the DramaFinder jar/sources to discover its API.**
> The complete, always-current signature reference is bundled beside this skill
> in [api-reference.md](api-reference.md) (auto-generated from source). If a
> method isn't there, it doesn't exist in this version — do not guess or dig
> into the jar. The few components with non-obvious behaviour also have prose
> docs in the [specifications folder](https://github.com/parttio/dramafinder/tree/master/docs/specifications).
>
> To look up an element, **grep `api-reference.md` for the element name and read
> only that section** (each is a `### <Name>Element` heading) — don't read the
> whole file. Shared mixin methods are documented once under "Shared mixins".

Before writing any raw locator, confirm there is genuinely no wrapper: check
[element-mapping.md](element-mapping.md) **and** scan `src/main/java` for
`*Element.java` files (custom extensions not in the table). Only if neither
covers the component may you use a plain Playwright locator. For recurring
needs, create your own element class extending `VaadinElement`,
or [open an issue](https://github.com/parttio/dramafinder/issues) in the
DramaFinder repository to request one.

### Do NOT drop to raw locators for wrapped components

A wrapper exposes the count/content/state you need — use it instead of digging
into the component's internal tags.

```java
// ❌ WRONG — reaching into Grid internals with a raw locator
GridElement leaderboardGrid = GridElement.get(page);
// Vaadin Grid renders row cells as vaadin-grid-cell-content inside the grid element
int cellCount = leaderboardGrid.getLocator().locator("vaadin-grid-cell-content").count();

// ✅ RIGHT — use the GridElement API
GridElement leaderboardGrid = GridElement.get(page);
int rows = leaderboardGrid.getRenderedRowCount();   // or getTotalRowCount()
int cols = leaderboardGrid.getColumnCount();
leaderboardGrid.assertCellContent(0, "Score", "100");
leaderboardGrid.assertRowCount(10);
```

The same rule applies to every wrapped component: `ComboBoxElement.selectByText()`
not `combo.locator("vaadin-combo-box-item")`; `MenuBarElement.clickItem()` not
a raw `vaadin-menu-bar-button` locator; and so on.

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