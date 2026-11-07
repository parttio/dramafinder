---
name: vaadin-playwright-test
description: Generate Playwright integration tests for Vaadin 25 views using the DramaFinder library, including element interaction, form validation, grid assertions, and navigation checks. Use whenever you are about to write, edit, or run an integration/IT test for a Vaadin view — including when the requirement comes from a GitHub issue, PR, spec, or ticket rather than the user's direct words (e.g. "implement #85", "fix the failing test", "add coverage for this view"). Also use when the user mentions DramaFinder, Playwright, `SpringPlaywrightIT`, `@PlaywrightElement`, files under `src/test/**/tests/it/`, or asks about Playwright testing in a Vaadin project. If a task in a Vaadin project involves any Playwright/IT test, invoke this skill (via the Skill tool) before writing the test.
---

# Vaadin Playwright Test Generator (DramaFinder)

> **Invoke this skill via the Skill tool — do not just read this `SKILL.md`.**
> The files this skill depends on — [api-reference.md](api-reference.md) (the
> complete DramaFinder API), [element-mapping.md](element-mapping.md), and
> [TESTING.md](TESTING.md) — are bundled beside this file and only load into
> context when the skill is *invoked*, not when you open `SKILL.md` by hand. If
> you are reading this file directly, stop and invoke the skill instead. This is
> also *why* the rule below ("never decompile the jar to discover the API")
> holds: when invoked, the API reference is already in front of you.

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
- **Navigate by route class** — inside a test that extends
  `AbstractBasePlaywrightIT`, go to another view with `navigate(ProfileView.class)`
  (or `navigate(View.class, parameter)`), not `page.navigate(...)` with a built URL.
  Check where the browser is with `assertAt(View.class)`.
- **Pass the whole label** — `getByLabel`, `getByText`, `getByHeaderText` and
  the other label lookups match the full name, case-sensitively, and fail on
  duplicates. Copy the label exactly from the view; use `getByLabelContaining`
  only when the label is long or generated
- **A label is the visible label** — `getByLabel` matches the text a component
  shows as its label, never an `aria-label` or a placeholder. A field named only
  by `setAriaLabel` is found with
  `find(page, TextFieldElement.class).withAccessibleName("Search").single()`, one
  known only by its placeholder with `.withPlaceholder(..)` (`find` is
  `ElementQueries.find`)
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
4. **Own `SpringPlaywrightIT` copy in project?** —
   `find src/test/java -name SpringPlaywrightIT.java`. DramaFinder 2.0+ ships
   `org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT`; a project copy
   only exists for older versions.

### DramaFinder not found — propose setup, then run it

Resolve the latest version (Step 1 of [setup.md](setup.md)) and propose the
following in a single confirmation:

- Add `org.vaadin.addons:dramafinder:<VERSION>` and
  `com.microsoft.playwright:playwright` (test scope) to `pom.xml` with
  `<dramafinder.version>` in `<properties>`.
- **Spring Boot only, DramaFinder before 2.0:** also create
  `src/test/java/<basePackage>/it/support/SpringPlaywrightIT.java`. From 2.0 on,
  tests extend the library's `org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT`
  and nothing is generated.

On confirmation, execute [setup.md](setup.md) end-to-end, then continue with
Step 2.

### DramaFinder found — follow existing patterns

If existing `*IT.java` files are found, read one or two to understand the
project's conventions (base class, package structure, assertion style, helper
methods) and use them as the template.

If no existing IT tests exist, use the default structure in Step 3.

### `SpringPlaywrightIT` location

- DramaFinder 2.0 or later → `org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT`
  from the library. It starts the app on a random port (`@SpringBootTest` is
  inherited) and derives `getUrl()` from it, context path included. If the project
  also has its own copy, keep following its existing tests, and suggest deleting
  the copy in favour of the library class.
- Before 2.0: 1 hit → use that class; 0 hits + Spring Boot → run setup (it creates
  the file); more than 1 hit → ask the user which one to use.

## Step 2 — Map view components to DramaFinder elements

Read the target view source provided by the user. Extract:

- `@Route("value")` → URL path (default when no value: class name lowercased,
  stripped of `View` suffix, e.g. `PersonView` → `/person`; `MainView` and
  `Main` map to `/`).
- `@PageTitle("...")` → expected page title
- Every interactive component → its DramaFinder wrapper (see table below).
- Form fields → label text used as locator.
- Grids → column headers and row content to assert against.
- Navigation triggers → button labels or menu items that cause route changes.

### Outline the running view (when the app runs in the test harness)

The source gives you intent; the running page gives you the labels a lookup
actually matches. Before writing lookups, run a throwaway test that writes a
**view outline** — every Vaadin component on the page with the DramaFinder
lookup that reaches it, verified against the live page:

```java
// a SpringPlaywrightIT copy from before 2.0 also needs @SpringBootTest(webEnvironment = RANDOM_PORT)
public class <ViewName>OutlineIT extends SpringPlaywrightIT { // or AbstractBasePlaywrightIT

    @Override
    public String getView() {
        return "/<route-path>";
    }

    @Test
    public void outline() {
        // Open dialogs/menus first if the test needs their contents.
        org.vaadin.addons.dramafinder.agent.ViewOutline.write(page);
    }
}
```

Run it with `mvn verify -Dit.test=<ViewName>OutlineIT`, read
`target/agent-report/view-outline.txt` (`build/` under Gradle), then delete the
test. Each entry looks like:

```
vaadin-text-field "Name" = "ada" (required)
    TextFieldElement.getByLabel(page, "Name")
vaadin-tabs #order-tabs
    TabsElement.getById(page, "order-tabs")
    items (full label, case-sensitive): "Details", "Payment"
```

Copy the lookups as given. An entry ending in `// … matches 2 components` or
`// no factory singles it out` has no unique page-level lookup: scope it to a
container, or ask whether the component can get an id. Skip this step when the
view can't start in the test harness, or for a one-field change.

See [element-mapping.md](element-mapping.md) for the full component → element
class table, and [api-reference.md](api-reference.md) for the **complete public
API** (every element, its methods, signatures and one-line descriptions) of the
DramaFinder version bundled with this skill (see the version in its header). If
the project pins an older `<dramafinder.version>`, a method documented there may
not exist yet — if a call fails to compile, check the project's version before
looking for alternatives.

> **Never decompile the DramaFinder jar to discover its API.**
> The complete, always-current signature reference is bundled beside this skill
> in [api-reference.md](api-reference.md) (auto-generated from source). If a
> method isn't there, it doesn't exist in this version — do not guess or dig
> into the jar. The few components with non-obvious behaviour also have prose
> docs in the [specifications folder](https://github.com/parttio/dramafinder/tree/master/docs/specifications).
>
> If you somehow have neither this skill nor a network, the same reference ships
> inside the jar — read it, don't decompile:
> `unzip -p ~/.m2/repository/org/vaadin/addons/dramafinder/*/dramafinder-*.jar META-INF/dramafinder/api-reference.md`
>
> To look up an element, **grep `api-reference.md` for the element name and read
> only that section** (each is a `### <Name>Element` heading) — don't read the
> whole file. Shared mixin methods are documented once under "Shared mixins".

Besides the per-element factories, every wrapper in the index with a `find`
query can be looked up with one grammar —
`find(page, TextFieldElement.class).withLabel("Name").single()`, or
`dialog.find(ButtonElement.class).withText("Delete").single()` to search inside a
component (static import `ElementQueries.find`). Use it to combine filters
(label **and** test id) or to scope a lookup; only filters that fit the component
compile, and `withLabel` / `withText` match the whole value. See "Typed queries"
in [api-reference.md](api-reference.md).

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

The same rule applies to every wrapped component:
`ComboBoxElement.selectItem()` not `combo.locator("vaadin-combo-box-item")`;
`MenuBarElement.getMenuItemElement("File").click()` not a raw
`vaadin-menu-bar-button` locator; and so on.

## Step 3 — Generate the test class

### Default structure (no existing tests to mirror)

```java
package <same.package.as.view>; // mirror src/test/java structure

import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.TextFieldElement; // import only used elements
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT; // Spring Boot, DramaFinder 2.0+
// import <basePackage>.it.support.SpringPlaywrightIT;           // Spring Boot, before 2.0 (+ @SpringBootTest)
// import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT; // no Spring: also override getUrl()

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class <ViewName>IT extends SpringPlaywrightIT { // or AbstractBasePlaywrightIT

    @Override
    public String getView() {
        return "/<route-path>";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("<PageTitle value>");
    }

    // ... component tests below
}
```

Use `SpringPlaywrightIT` if Spring Boot is detected, `AbstractBasePlaywrightIT`
otherwise. The library class needs no `@SpringBootTest` on the test (it inherits
one with `RANDOM_PORT`); annotate the test only to change properties or the web
environment. A copy generated for DramaFinder before 2.0 still needs
`@SpringBootTest(webEnvironment = RANDOM_PORT)` on each test.

### Component test patterns

**Smoke test (one per component):**

```java
@Test
public void test<ComponentLabel>() {
    TextFieldElement field = TextFieldElement.getByLabel(page, "My Label");
    field.assertVisible();
    field.assertLabel("My Label");
    field.assertValue("");
    field.setValue("test value");
    field.assertValue("test value");
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

## Step 4 — Write the test

Place the test in `src/test/java` mirroring the view's package under
`src/main/java`.

- **Interactive session** (the user asked for a test in conversation): display
  the full generated test class in a code block first, then ask:
  > Shall I write this to `src/test/java/<package>/<ViewName>IT.java`?

  Only write the file after confirmation.
- **Autonomous execution** (implementing an issue/PR/spec, or running
  unattended): write the file directly without asking.

## Step 5 — Run the test

Run with `mvn verify -Dit.test=<ViewName>IT`.

- **Interactive session**: offer to run it first — the first Vaadin frontend
  build takes 3–5 minutes (subsequent runs are ~25 seconds), so let the user
  decide.
- **Autonomous execution**: run it directly and fix any failures before
  finishing.

### When a test fails

Tests extending `AbstractBasePlaywrightIT` (including `SpringPlaywrightIT`)
write a failure bundle per failing test to
`target/agent-report/<TestClass>/<testMethod>/` (`build/` for Gradle). Read it
before changing the test:

- `failure.txt` — the assertion message, trimmed stack trace and page URL.
  DramaFinder assertions state the expected and actual value, e.g.
  `Grid row count mismatch. Expected: <12>, actual: <100>`.
- `component-snapshot.txt` — the Vaadin components on the page at failure time
  (labels, values, grid columns and row counts). Use it to check the labels
  your lookups rely on instead of rereading the view's source.
- `view-outline.txt` — the same components, each with the DramaFinder lookup
  that reaches it, verified against the page. When a lookup found nothing,
  replace it with the one listed here.
- `failure.png` — a full-page screenshot.

Pass `-Ddramafinder.agentReport=false` to turn the capture off.