# Repository Guidelines

## Project Structure & Module Organization

- Root `pom.xml` — Core Playwright helpers for Vaadin (`src/main/java/...`),
  resources under `src/main/resources`.

## Build, Test, and Development Commands

- Build all modules + unit tests: `./mvnw clean install`
- Run demo locally: `./mvnw -pl dramafinder-demo spring-boot:run` (serves
  at http://localhost:8080)
- Run integration tests (Failsafe, includes `**/*IT.java`):
  `./mvnw -B verify --file pom.xml`
- Run a single test:
    - IT: `./mvnw -Dit.test=MyViewIT -Pit verify`

## Coding Style & Naming Conventions

- Java 21; 4-space indent; organize imports; no trailing whitespace.
- Packages: lowercase (`org.vaadin.dramafinder`); classes: `PascalCase`;
  methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Public API in `dramafinder` should be small, cohesive, and documented with
  Javadoc.
- Prefer aria role for internal locators

### Javadoc Conventions

- Add a class-level Javadoc for each element class and shared mixin:
  - Identify the Vaadin tag using inline code (e.g., `vaadin-text-field`).
  - One–two sentence summary of responsibilities and notable behaviors.
  - For factory helpers (e.g., `getByLabel`), mention the ARIA role used.
- For public methods, document parameters, return values, and null semantics
  (especially for assertion helpers where `null` implies absence).
- Use `{@inheritDoc}` on simple overrides (e.g., locator accessors) to avoid
  duplication.
- Keep Javadoc concise and consistent; prefer present tense and active voice.

## Testing Guidelines

- Frameworks: JUnit 5, Playwright (Java), Axe-core checks in demo.
- Unit tests live with their module; integration tests go in
  `src/test/java/**/*IT.java`.
- Tests should be deterministic; avoid timing sleeps—prefer Playwright waits and
  assertions.

## Commit & Pull Request Guidelines

- Commits: short, imperative subject; optional scope prefix (e.g., `core:`,
  `demo:`). Example: `core: add TestFieldElement to test a Vaadin Textfield`.
- PRs: describe intent and approach, link issues, list test coverage and manual
  steps; include screenshots/gifs for UI changes.
- Keep changes module-scoped; update README/AGENTS.md when commands or workflows
  change.

## Security & Configuration Tips

- Requires Java 21. Vaadin demo uses Node tooling; first runs may download
  frontend and Playwright browser binaries.
- Do not commit generated/build output: `target/`, `frontend/generated/`,
  `vite.generated.ts`.
- Prefer configuration via `application.properties` in each module’s
  `src/main/resources`.

## Pitfalls & Patterns

### Common Pitfalls

#### 1. Locator Specificity
- **Pitfall**: Using overly broad locators that match multiple elements.
- **Solution**: Always use `.first()` when expecting a single element, or filter
  with `Locator.FilterOptions` to narrow results.
```java
// Bad: May match multiple elements
page.locator("vaadin-button");

// Good: Specific by accessible name
ButtonElement.getByText(page, "Save");
```

#### 2. Shadow DOM Piercing
- **Pitfall**: Using XPath or CSS selectors that don't pierce shadow DOM.
- **Solution**: Playwright auto-pierces shadow DOM with CSS selectors. Use
  `xpath=./*` prefix only for direct children to avoid piercing.
```java
// Pierces shadow DOM (default)
getLocator().locator("[slot='input']");

// Does NOT pierce shadow DOM (explicit xpath)
getLocator().locator("xpath=./*[not(@slot)][1]");
```

#### 3. Input vs Component Locator
- **Pitfall**: Calling methods on the wrong locator (component root vs input).
- **Solution**: Use `getInputLocator()` for value/focus operations,
  `getLocator()` for component-level attributes.
```java
// Component-level attribute
getLocator().getAttribute("opened");

// Input-level attribute
getInputLocator().getAttribute("value");
```

#### 4. Async State Changes
- **Pitfall**: Asserting state immediately after an action without waiting.
- **Solution**: Use Playwright assertions which auto-retry. Avoid raw
  `getAttribute()` checks in assertions.
```java
// Bad: No retry, may flake
assertTrue(getLocator().getAttribute("opened") != null);

// Good: Auto-retries until timeout
assertThat(getLocator()).hasAttribute("opened", "");
```

#### 5. Attribute vs Property
- **Pitfall**: Confusing HTML attributes with DOM properties.
- **Solution**: Use `getAttribute()` for HTML attributes, `evaluate()` or
  `hasJSProperty()` for DOM properties.
```java
// HTML attribute
getInputLocator().getAttribute("maxlength");

// DOM property (set via JavaScript)
getLocator().evaluate("(el, v) => el.maxLength = v", max);
assertThat(getLocator()).hasJSProperty("value", expectedValue);
```

#### 6. Null Handling in Assertions
- **Pitfall**: Not handling `null` values in assertion helpers.
- **Solution**: Always check for `null` and assert absence of attribute.
```java
public void assertMinLength(Integer min) {
    if (min != null) {
        assertThat(getInputLocator()).hasAttribute("minLength", min + "");
    } else {
        // Assert attribute is absent
        assertThat(getInputLocator()).not().hasAttribute("minLength", Pattern.compile(".*"));
    }
}
```

#### 7. ARIA Role Mismatch
- **Pitfall**: Using wrong ARIA role in `getByRole()` lookups.
- **Solution**: Check the actual role of the internal element:
  - Text inputs: `TEXTBOX`
  - Number inputs: `SPINBUTTON`
  - Date/time pickers: `COMBOBOX`
  - Buttons: `BUTTON`
  - Checkboxes: `CHECKBOX`
  - Radio buttons: `RADIO`

### Design Patterns

#### 1. Factory Method Pattern
All element classes provide static factory methods for lookup by accessible name:
```java
public static TextFieldElement getByLabel(Page page, String label) {
    return new TextFieldElement(
        page.locator(FIELD_TAG_NAME)
            .filter(new Locator.FilterOptions()
                .setHas(page.getByRole(AriaRole.TEXTBOX,
                    new Page.GetByRoleOptions().setName(label)))
            ).first());
}
```

#### 2. Mixin Interfaces (Composition)
Shared behavior is extracted into interfaces with default implementations:
```java
public interface HasClearButtonElement extends HasLocatorElement {
    default void clickClearButton() {
        getLocator().locator("[part~='clear-button']").click();
    }
}
```

#### 3. Locator Delegation Pattern
Elements delegate to specific internal locators for different operations:
```java
@Override
public Locator getFocusLocator() {
    return getInputLocator();  // Focus goes to input, not wrapper
}

@Override
public Locator getEnabledLocator() {
    return getInputLocator();  // Disabled state is on input
}
```

#### 4. Composite Element Pattern
Complex components compose simpler elements internally:
```java
public class DateTimePickerElement extends VaadinElement {
    private final DatePickerElement datePickerElement;
    private final TimePickerElement timePickerElement;

    public DateTimePickerElement(Locator locator) {
        super(locator);
        datePickerElement = new DatePickerElement(locator.locator(DatePickerElement.FIELD_TAG_NAME));
        timePickerElement = new TimePickerElement(locator.locator(TimePickerElement.FIELD_TAG_NAME));
    }
}
```

#### 5. Assertion Symmetry Pattern
For each state getter, provide matching `assert` methods:
```java
public boolean isOpen() { ... }
public void assertOpen() { ... }
public void assertClosed() { ... }

public boolean isChecked() { ... }
public void assertChecked() { ... }
public void assertNotChecked() { ... }
```

#### 6. Scoped Lookup Pattern
Factory methods support both page-level and scoped lookups:
```java
// Page-level lookup
public static ButtonElement getByText(Page page, String text) { ... }

// Scoped lookup (within a container)
public static ButtonElement getByText(Locator locator, String text) { ... }
```

### Best Practices

1. **Prefer ARIA roles over tag names** for element lookup when possible.
2. **Use `part` selectors** for internal component parts (e.g., `[part~='input']`).
3. **Chain filters** rather than complex XPath for readability.
4. **Document null semantics** in assertion method Javadocs.
5. **Dispatch events** after programmatic value changes if needed:
   ```java
   getLocator().dispatchEvent("change");
   ```
6. **Use constants** for tag names and attribute names to avoid typos.
7. **Inherit behavior** by extending base elements (e.g., `EmailFieldElement extends TextFieldElement`).

## Agent-Specific Instructions

- Follow this file's conventions for any edits. Keep patches minimal and
  focused.
- Use `*IT.java` only for end-to-end tests executed by Failsafe.
- Refer to `docs/specifications/` for detailed element API documentation.
