# Repository Guidelines

## Project Structure & Module Organization

- Single-module Maven project. Root `pom.xml` builds the library — core
  Playwright helpers for Vaadin live in
  `src/main/java/org/vaadin/addons/dramafinder/`.
- The demo views used to exercise the library are test-only, under
  `src/test/java/.../tests/testuis/`; there is no standalone runnable demo app.
  Views are served by the Spring Boot IT harness (`@SpringBootTest`) when the
  `*IT.java` tests run.

## Build, Test, and Development Commands

- Build + unit tests: `./mvnw clean install`
- Run unit tests only: `./mvnw test`
- Run integration tests (Failsafe, `**/*IT.java`, requires the `it` profile):
  `./mvnw -Pit verify`
- Run a single IT: `./mvnw -Dit.test=MyViewIT -Pit verify`
- Debug an IT with a visible browser: `./mvnw -Pdebug-ui -Dit.test=MyViewIT verify`
  (or `-Dheadless=false`)

## Coding Style & Naming Conventions

- Java 21; 4-space indent; organize imports; no trailing whitespace.
- Packages: lowercase (`org.vaadin.addons.dramafinder`); classes: `PascalCase`;
  methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Public API in `dramafinder` should be small, cohesive, and documented with
  Javadoc.
- Prefer aria role for internal locators

### Javadoc Conventions

- Add a class-level Javadoc for each element class and shared mixin:
  - Identify the Vaadin tag using inline code (e.g., `vaadin-text-field`).
  - One–two sentence summary of responsibilities and notable behaviors.
  - For factory helpers, say what they match: `getByLabel` the visible label,
    other name-keyed factories the accessible name (and the ARIA role, if any).
- For public methods, document parameters, return values, and null semantics
  (especially for assertion helpers where `null` implies absence).
- Use `{@inheritDoc}` on simple overrides (e.g., locator accessors) to avoid
  duplication.
- An override that *changes* behaviour needs a sentence of its own instead of
  a bare `{@inheritDoc}`: the generated `api-reference.md` lists an override
  only when it documents itself or redefines a `VaadinElement` method, so an
  undocumented one stays invisible to anyone reading the reference.
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
- **Every element action waits for the server round-trip** it may trigger
  (`AutoWait`, issue #202): a public method that clicks, fills, presses, checks,
  selects, hovers, focuses/blurs, uploads or sets a property ends with
  `waitAfterAction()` (from `HasLocatorElement`), placed right after the raw
  Playwright action — before any wait specific to the component (e.g. Grid's
  `waitForGridToStopLoading()`). A new action must do the same; never call
  `waitForVaadinIdle()` there, because it ignores the opt-out
  (`AutoWait.withoutWaiting(...)`, `-Ddramafinder.autoWait=false`).
  `ActionsWaitForTheServerTest` scans the element sources and fails on an action
  (a Playwright action on a locator, or a DOM property written from a script)
  without the wait. The server
  half only: client-side rendering after the response (an overlay opening, a
  grid re-rendering) still needs a retrying assertion.

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

#### 7. Disabled State on Non-Form Hosts
- **Pitfall**: Implementing `HasEnabledElement` on a component whose host is not
  a native form control, e.g. `vaadin-side-nav-item` (`listitem`),
  `vaadin-list-box` (`list`) or a plain container. Playwright only treats an
  element as disabled when it is a native control with `disabled`, or has
  `aria-disabled="true"` on a role that supports it (`button`, `link`,
  `menuitem`, `tab`, `option`, …), so `isEnabled()` returns `true` on a disabled
  Vaadin host even though `DisabledMixin` set `disabled` on it.
- **Solution**: Implement `HasDisabledAttributeElement` instead. It redefines
  `isEnabled()`, `assertEnabled()` and `assertDisabled()` in terms of the
  `disabled` attribute of `getEnabledLocator()`, so the getter and the
  assertions stay consistent. Never override only the assert methods: that
  leaves `isEnabled()` / `isEnabled(boolean)` reporting the opposite.
- Keep plain `HasEnabledElement` when the enablement locator is a native form
  control (typically via `getEnabledLocator()` returning `getInputLocator()`) or
  has a role Playwright understands — its checks also cover `aria-disabled` and
  state inherited from an ancestor `fieldset`.
```java
// Host is <vaadin-side-nav-item role="listitem"> - attribute based
public class SideNavigationItemElement extends VaadinElement
        implements HasDisabledAttributeElement { }

// Enablement lives on the inner <input> - Playwright based
public class TextFieldElement extends VaadinElement implements HasEnabledElement {
    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }
}
```

#### 8. ARIA Role Mismatch
- **Pitfall**: Using wrong ARIA role in `getByRole()` lookups.
- **Solution**: Check the actual role of the internal element:
  - Text inputs: `TEXTBOX`
  - Number inputs: `SPINBUTTON`
  - Date/time pickers: `COMBOBOX`
  - Buttons: `BUTTON`
  - Checkboxes: `CHECKBOX`
  - Radio buttons: `RADIO`

#### 9. Theme Variant Order
- **Pitfall**: Asserting a single theme variant with `assertTheme("primary")` on
  a component that has several. Vaadin combines every applied variant into one
  space-separated `theme` attribute, so a component with `LUMO_PRIMARY` and
  `LUMO_SMALL` fails `assertTheme("primary")` — the attribute holds both.
- **Solution**: `assertTheme(...)` is for the whole attribute; it compares the
  variants as an unordered set, so `"small primary"` and `"primary small"` both
  pass, but every applied variant has to be listed. To assert one variant among
  several, use `HasThemeElement.assertHasThemeVariant(...)` /
  `assertHasNoThemeVariant(...)`, which match the variant as a whole token
  regardless of the others. Both are inherited by every element implementing
  `HasThemeElement` — never re-implement the token regex per element.

#### 10. Substring Label Matching
- **Pitfall**: Keying an item lookup on `setName(label)`, `getByText(label)` or
  `setHasText(label)` and closing with `.first()`. All three are
  case-insensitive substring matches, so `getCheckbox("Option 1")` happily
  returns the `"Option 10"` checkbox when that one comes first in the DOM, and
  `assertSelected("Option 1")` then passes against it — a green test over a
  wrong UI state.
- **Solution**: For item-within-container lookups use `ItemLocator`, which
  matches the full label and leaves the
  locator unnarrowed so duplicates raise a strict-mode error. See "Exact vs
  substring matching" under the Scoped Lookup Pattern for which lookups stay
  substring-based.
```java
// Bad: "Option 1" also matches "Option 10", and .first() hides it
locator.getByRole(AriaRole.TAB, new Locator.GetByRoleOptions().setName(label)).first();

// Good: full label, strict-mode error on a real duplicate
ItemLocator.byExactName(getLocator(), AriaRole.TAB, label);
```

#### 11. Light-DOM Text vs `textContent`
- **Pitfall**: Reading a component's own text with `locator.textContent()` (or
  Playwright's `hasText`) when the component also renders content in its shadow
  DOM or accepts slotted children. Both traverse the shadow DOM and include
  slotted light-DOM text, so `vaadin-badge` with an icon and a number reports
  the icon glyph and the number as part of its text.
- **Solution**: Read only the child nodes that carry no `slot` attribute, e.g.
  via `evaluate` over `childNodes` (see `BadgeElement.getText()`), or scope a
  locator with `> :not([slot])` (see `CardElement.getContentLocator()`).

### Design Patterns

#### 1. Factory Method Pattern
Element classes provide static factory methods. A **label** is the visible label
(the component's own `label` slot), not the accessible name (see `GLOSSARY.md`):
`getByLabel` never matches an `aria-label` or a placeholder. It delegates to the
typed query, so the two lookups cannot drift apart:
```java
public static TextFieldElement getByLabel(Page page, String label) {
    return new TextFieldElement(
        ElementQueries.find(page, TextFieldElement.class).withLabel(label).getLocator());
}
```
`getByLabel` and `getByLabelContaining`, each with a `Page` and a `Locator`
overload, exist **exactly** on the elements whose query has `withLabel`;
`LabelLookupParityTest` fails otherwise, and `LabelLookupIT` checks each one
against the visible label, an `aria-label` and a placeholder. A component with no
visible label gets no `getByLabel`: tests find it by accessible name with
`find(..).withAccessibleName(..)` (deprecated 1.x `getByLabel` factories on such
elements stay until 3.0). Don't add static `getByAccessibleName` factories; the
query filter is the one place that rule lives.

Name-keyed factories match the **whole** name with no `.first()`; the `…Containing`
siblings are the partial matches (see "Exact vs substring matching").

Every public element also has `getById` and `getByTestId`, each with a `Page` and a
`Locator` overload, built on `IdLocator` (issue #203): exact, scoped to the element's
tag, no `.first()`. They are fallbacks for components without a user-visible name. A
new element gets all four; `IdFactoriesTest` fails otherwise. The exceptions are
wrappers whose tag is not where Flow puts the id (notification card, tooltip, menu
overlays, menu-bar button) — listed in that test.

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

##### Exact vs substring matching

Playwright's `setName(...)`, `getByText(...)` and `setHasText(...)` all default to
a **case-insensitive substring** match, so `"Option 1"` also matches
`"Option 10"`. Combined with a trailing `.first()` that silently resolves to
whichever matched element comes first in the DOM — and, because the assertion
helper re-runs the same lookup, a test then goes green against the wrong
element. The library therefore matches labels and names **exactly** by default and
keeps substring matching for lookups that say so in their name or Javadoc:

- **Item-within-container lookups** — one option out of a combo box, select,
  list box or checkbox/radio group, one tab, one menu item, one side-nav item,
  one accordion panel, one breadcrumb item, one upload row. The caller passes the
  item's whole label, so these match **exactly** and carry **no `.first()`**: a
  genuine duplicate fails with a Playwright strict-mode error instead of being
  masked. Build them with `ItemLocator` rather than hand-rolling the chain:
  ```java
  // ARIA role + exact accessible name
  ItemLocator.byExactName(getLocator(), AriaRole.TAB, label);
  // exact visible text, matching the item or an element inside it
  ItemLocator.byExactText(getLocator(), FIELD_ITEM_TAG_NAME, label);
  // exact visible text of the item ALONE, for items that nest items
  ItemLocator.byOwnExactText(getLocator(), FIELD_TAG_NAME, label);
  ```
  `byExactText` matches an element whose whole subtree text equals the label, so
  it also finds a label wrapped in a renderer's `<span>`; `byOwnExactText`
  matches only the item's own text nodes, which is what a container whose items
  can hold items of the same kind (`vaadin-side-nav-item`) needs — matching the
  subtree would read a parent as `"Parent Child 1 Child 2"`.
- **Page-level and scoped factories** — `getByLabel` (the visible label, through
  `withLabel`), `ButtonElement.getByText`, `getByHeaderText`, `getByTitle`,
  `getBySummaryText`, `getByButtonText`, `AvatarElement.getByName`. Same rule as
  items: the whole name, case-sensitively, **no `.first()`**.
- **Explicit partial matches** — `getByLabelContaining` (through
  `withLabelContaining`), `ButtonElement.getByTextContaining` and the query's
  `withAccessibleNameContaining`: case-insensitive substring, still no
  `.first()`, so an ambiguous fragment fails rather than picking the first match.
- **"Contains" text helpers** — `BadgeElement`, `NotificationElement`,
  `TooltipElement` `getByText` and `VirtualListElement.getItemByText`. These keep
  substring matching and `.first()` as a deliberate convenience: they match
  free-form content rather than a label, and a virtual-list item renders an
  arbitrary component with no single label. Say so in the Javadoc — "matched
  case-insensitively as a substring" — so it reads as a choice rather than a
  Playwright default that leaked through.

A new label- or name-keyed factory follows the first rule and gets a
`...Containing` sibling when partial matching is useful.

`MessageListElement.getMessageByUserName` is the documented exception among item
lookups: it matches the author exactly but keeps `.first()`, because several
messages from one author is normal data, not an ambiguity.

Two consequences worth spelling out in the Javadoc of an exact lookup: the match
becomes **case-sensitive**, and a lookup keyed on a partial label stops working.

#### 7. Typed Query Pattern
`ElementQueries.find(page, X.class)` (and `element.find(X.class)` for a scoped
search) is a query builder alongside the factories, in the
`org.vaadin.addons.dramafinder.element.query` package. Each element declares
its query type by implementing `Findable<Q>`; Java infers `Q` from the class
literal, so a filter that cannot apply to the component does not compile:
```java
TextFieldElement name = find(page, TextFieldElement.class).withLabel("Name").single();
dialog.find(ButtonElement.class).withText("Delete").single().click();
find(page, ButtonElement.class).withLabel("Save"); // compile error
```
- Filters come from mixins (`LabelFilters`, `TextFilters`, `PlaceholderFilters`)
  composed by small query classes (`InputFieldQuery`, `FieldQuery`, `ButtonQuery`,
  `TextQuery`, `TooltipQuery`, `SideNavItemQuery`, `SelectQuery`,
  `DateTimePickerQuery`, `ComponentQuery`). A mixin's default methods only call
  `refine(...)`; a query whose component keeps its text, label or placeholder
  elsewhere overrides them (`TooltipQuery`, `SideNavItemQuery`, `SelectQuery`,
  `DateTimePickerQuery`).
- `withAccessibleName` and `withAccessibleNameContaining` are on every query
  (`ElementQuery` implements `AccessibleNameFilters`). It reads the host or the field's own
  `slot="input|textarea|value"` child — never a nested component, so a dialog
  is not matched by a field it holds. `ButtonQuery` and `TextQuery` fall back to
  the component's own text, as name-from-content roles do — never Playwright's
  role name, which grows by the tooltip text while a slotted tooltip is open
  (`ButtonElement.getByText` relies on this).
- Text filters read the component's **own** text (pitfall 11): light DOM only,
  via XPath, with named-slot children left out — never Playwright `hasText`.
- A new element gets `@PlaywrightElement` and `implements Findable<...>` with the
  query whose filters really match it — verify each filter in an IT before
  binding (e.g. `vaadin-select` does not reflect `placeholder` on its host, so
  `SelectQuery` reads it from the value button). `ElementQueriesTest` fails when a public element is not
  findable; overlay-bound wrappers are listed there as exceptions.
- Exact filters (`withLabel`, `withText`, `withAccessibleName`,
  `withPlaceholder`) match the whole value case-sensitively and carry no
  `.first()`, like item lookups; `*Containing` variants are the substring ones.
- A subclass inherits its parent's binding (Java forbids two
  parameterizations of one interface): `find(page, TreeGridElement.class)`
  returns `TreeGridElement` instances typed as `GridElement`.

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
- **Never decompile the DramaFinder jar to discover its API.**
  The complete public API (test setup, every element, signatures, one-line
  descriptions) is in `skills/vaadin-playwright-test/api-reference.md`,
  auto-generated from source by `tools/generate-api-reference.java`. To look
  something up, **grep that file for the element name and read only its
  `### <Name>Element` section** — don't read the whole file. In a consumer
  project where it isn't checked out, fetch it (one request) from
  `https://raw.githubusercontent.com/parttio/dramafinder/master/skills/vaadin-playwright-test/api-reference.md`,
  or, with no network, read the copy bundled in the jar:
  `unzip -p ~/.m2/repository/org/vaadin/addons/dramafinder/*/dramafinder-*.jar META-INF/dramafinder/api-reference.md`.
- The build copies `api-reference.md` and `agent-api-reference.md` into the jar
  under `META-INF/dramafinder/` (see `maven-resources-plugin` in `pom.xml`), next
  to the hand-written `src/main/resources/META-INF/dramafinder/START-HERE.md` and
  `MIGRATION-2.0.md` (the 1.x → 2.0 guide: keep its list of changed methods in
  step with any further change to label-lookup behaviour before 2.0 ships).
  That bundled set is the only documentation an offline agent can reach — Maven
  puts nothing but the jar and the pom in `~/.m2` — so keep it working:
  `DocumentationBundledInJarTest` fails if a file stops being bundled, and
  `START-HERE.md` needs a manual look whenever the setup API or the pitfalls
  change.
- `api-reference.md` is generated — never edit it by hand. After changing any
  element's public API, regenerate it (`jbang tools/generate-api-reference.java`)
  and commit the result; CI fails if it is stale.
- The `org.vaadin.addons.dramafinder.agent` helpers (`VisualVerificationTest`,
  `AgentReporting`, …) have their own generated reference at
  `skills/vaadin-playwright-screenshot/agent-api-reference.md`
  (`tools/generate-agent-api-reference.java`; same never-edit-by-hand, CI-enforced
  rule). After changing that package's public API, regenerate and commit it.
- Refer to `docs/specifications/` for the prose docs on components with
  non-obvious behaviour (Grid, TreeGrid, VirtualList, and extension guidance).
- The skills in `skills/` ship three ways: as Claude Code plugins
  (`.claude-plugin/marketplace.json` plus each skill's
  `.claude-plugin/plugin.json`), as a Tessl tile (`tile.json`), and through
  `llms.txt`. When a change alters what a skill tells agents (its `SKILL.md`, a
  file beside it, or the generated references it bundles), bump that skill's
  `plugin.json` version — Claude Code uses it to decide whether an installed
  plugin needs updating — and keep `tile.json`'s version equal to
  `vaadin-playwright-test`'s. A new skill goes into `marketplace.json` and
  `tile.json`; `SkillDistributionTest` fails when one is missing or the tile
  version drifts.
- **Spring is optional.** `spring-boot-test` is a `provided` dependency, used only
  by `spring.SpringPlaywrightIT` (issue #205); nothing else may reference Spring,
  and no Spring or Vaadin dependency may become compile/runtime scope, so
  DramaFinder never adds either to a consumer's classpath. `DependencyScopeTest`
  fails otherwise. The repo's own ITs extend the library's `SpringPlaywrightIT`.
- `com.vaadin:flow-server` is a `provided` dependency, used only by
  `RouteNavigation` (navigation by route class). Keep Vaadin types out of
  `AbstractBasePlaywrightIT`'s method signatures — JUnit reflects over them, and a
  test classpath without Flow must still load the class; its `navigate`/`assertAt`
  wrappers take `Class<?>`/`Object` and delegate. `RouteNavigationTest` checks this.
- `GEMINI.md` only imports this file; put agent instructions here.

## Agent skills

### Issue tracker

Issues live in GitHub Issues for `parttio/dramafinder`, managed with the `gh` CLI. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: one `GLOSSARY.md` and `docs/adr/` at the repo root, created as terms and decisions come up. See `docs/agents/domain.md`.
