# Core concepts

DramaFinder is a set of typed wrappers — **elements** — around Playwright
locators, one per Vaadin web component. A test finds an element, acts on it the
way a user would, and asserts the state the user would see.

```java
TextFieldElement email = TextFieldElement.getByLabel(page, "Email"); // find
email.setValue("ada@example.com");                                   // act
email.assertValid();                                                 // assert
```

## Elements

Every wrapper lives in `org.vaadin.addons.dramafinder.element` and is named
after the component's tag: `<vaadin-text-field>` → `TextFieldElement`,
`<vaadin-multi-select-combo-box>` → `MultiSelectComboBoxElement`. The
[Components](../specifications/index.md) section has a page per component, and
the [element index](../../skills/vaadin-playwright-test/api-reference.md#element-index)
lists every tag with its wrapper and factories.

An element is a thin object holding a Playwright `Locator`. Creating one does
not touch the browser: the locator is resolved each time you act or assert, so
an element stays valid while the component re-renders, and a lookup that
matches nothing only fails when it is used.

```java
// Nothing happens in the browser yet
ButtonElement save = ButtonElement.getByText(page, "Save");

ButtonElement.getByText(page, "Edit").click();
save.click(); // resolved now, after the form switched to edit mode
```

## Factories

Each element has static factory methods that find it the way a user would:

| Factory | Matches | Example |
|---|---|---|
| `getByLabel` | the visible label of a field | `TextFieldElement.getByLabel(page, "Name")` |
| `getByLabelContaining` | part of the visible label, any case | `TextFieldElement.getByLabelContaining(page, "name")` |
| `getByText` | the text of a button, badge, tab... | `ButtonElement.getByText(page, "Save")` |
| `getByHeaderText`, `getByTitle`, `getBySummaryText`... | a component-specific caption | `DialogElement.getByHeaderText(page, "Delete user?")` |
| `get` | the first component of that type | `GridElement.get(page)` |
| `getById`, `getByTestId` | the `id` / test id set in Flow | `GridElement.getById(page, "orders")` |

Every factory has a `Locator` overload to search inside a container, and every
wrapper can also be built directly from a locator:
`new TextFieldElement(someLocator)`.

Factories are **not uniform**: an element has exactly the ones listed in its
API section. [Finding components](finding-components.md) explains each kind and
the typed query builder, `find(page, X.class)`, which gives every element the
same grammar.

## Actions

Element methods act like a user: `click()`, `setValue(..)`, `check()`,
`selectItem(..)`, `open()`, `selectTab(..)`... They type into the real input,
click the real overlay item and dispatch the events Vaadin listens for, so the
server sees exactly what it sees in production.

Each action **waits for the server round-trip it triggers**: when
`setValue(..)` returns, the value-change listener has run and its response has
reached the browser. See [Actions and waiting](actions-and-waiting.md).

```java
ComboBoxElement country = ComboBoxElement.getByLabel(page, "Country");
country.selectItem("Finland");          // returns once the server has answered
TextFieldElement.getByLabel(page, "VAT number").assertVisible(); // shown by the listener
```

## Assertions

Every state an element exposes has a retrying `assertX` method next to its
getter: `assertValue`, `assertEnabled`, `assertChecked`, `assertRowCount`,
`assertOpen`... An assertion polls until the state matches or the timeout
expires, so it tolerates client-side rendering that happens after the server
answered. Getters do not retry.

```java
grid.assertRowCount(3);                  // retries until the grid shows 3 rows
assertEquals(3, grid.getTotalRowCount()); // reads once, may be flaky
```

[Assertions](assertions.md) covers the conventions (null semantics, exact text,
Playwright interop).

## Locators: the escape hatch

Every element exposes its Playwright locator, plus locators for its notable
parts, so you can always fall back to Playwright for something the API does not
cover:

| Method | Points at |
|---|---|
| `getLocator()` | the component host, e.g. `<vaadin-text-field>` |
| `getInputLocator()` | the native `<input>` / `<textarea>` of a field |
| `getLabelLocator()`, `getHelperLocator()`, `getErrorMessageLocator()` | the label, helper and error message slots |
| `getPrefixLocator()`, `getSuffixLocator()` | the prefix / suffix slots |
| `getTooltipLocator()` | the tooltip text |
| component specific | `getOverlayLocator()`, `getContentLocator()`, `getHeaderLocator()`... |

```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

TextFieldElement name = TextFieldElement.getByLabel(page, "Name");
assertThat(name.getInputLocator()).hasAttribute("autocomplete", "off");
name.getLocator().screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("name.png")));
```

A slot can host other components. Wrap its locator to use them:

```java
// View: textField.setHelperComponent(new TextField("Internal helper"))
TextFieldElement field = TextFieldElement.getByLabel(page, "With helper component");
TextFieldElement helper = new TextFieldElement(field.getHelperLocator());
helper.setValue("nested");
```

!!! tip "Use the element when there is one"

    Reach for raw locators only for something the element does not offer. Do not
    query a component's internal DOM (`vaadin-grid-cell-content`,
    `vaadin-combo-box-item`...): those tags are implementation details, and the
    element already exposes their content and state.

## Mixins: shared behaviour

Features that several components share are implemented once, as interfaces
with default methods, and composed into the elements. A `TextFieldElement`, a
`ComboBoxElement` and a `DatePickerElement` all have `assertHelperHasText`
because they all implement `HasHelperElement`.

| Mixin | Gives you |
|---|---|
| `HasValueElement` | `setValue`, `getValue`, `clear`, `assertValue`, `getInputLocator` |
| `HasLabelElement` | `getLabel`, `assertLabel` |
| `HasHelperElement` | `getHelperText`, `assertHelperHasText` |
| `HasPlaceholderElement` | `setPlaceholder`, `getPlaceholder`, `assertPlaceholder` |
| `HasValidationPropertiesElement` | `assertValid`, `assertInvalid`, `assertErrorMessage` |
| `HasEnabledElement` | `isEnabled`, `assertEnabled`, `assertDisabled` |
| `HasReadOnlyElement` | `isReadOnly`, `assertReadOnly`, `assertNotReadOnly` |
| `HasCheckedElement` | `check`, `uncheck`, `setChecked`, `assertChecked`, `assertNotChecked` |
| `HasClearButtonElement` | `clickClearButton`, `assertClearButtonVisible` |
| `HasPrefixElement`, `HasSuffixElement` | `assertPrefixHasText`, `assertSuffixHasText` |
| `HasTooltipElement` | `getTooltip`, `assertTooltipHasText` |
| `HasThemeElement` | `assertTheme`, `assertHasThemeVariant`, `assertHasNoThemeVariant` |
| `HasStyleElement` | `getCssClass`, `assertCssClass` |
| `HasAriaLabelElement` | `getAriaLabel`, `assertAriaLabel` |
| `FocusableElement` | `focus`, `blur`, `assertIsFocused`, `assertIsNotFocused` |
| `HasAllowedCharPatternElement` | `setAllowedCharPattern`, `assertAllowedCharPattern` |

The API reference lists, for each element, the mixins it implements; their
methods are documented once under
[Shared mixins](../../skills/vaadin-playwright-test/api-reference.md#shared-mixins).
[Assertions](assertions.md) shows them in use.

Every element also inherits from `VaadinElement`: `click()`, `getText()`,
`isVisible()`, `assertVisible()`, `assertHidden()`, `getProperty(..)`,
`setProperty(..)` and `find(..)`. See [VaadinElement](../specifications/VaadinElement.md).

## Composite and item elements

Containers return elements for their parts, so you never build those locators
yourself:

```java
TabsElement tabs = TabsElement.get(page);
TabElement details = tabs.getTab("Details");     // item by its whole label
details.select();

GridElement grid = GridElement.get(page);
grid.findRow(0).ifPresent(row -> row.select());   // row element
grid.findCell(0, "Email").get().assertText("ada@example.com");

DateTimePickerElement meeting = DateTimePickerElement.getByLabel(page, "Meeting");
meeting.getDatePicker().setValue(LocalDate.of(2026, 1, 15)); // composed field
```

## Where to go next

- [Finding components](finding-components.md)
- [Assertions](assertions.md)
- [Actions and waiting](actions-and-waiting.md)
- [Recipes](recipes.md)
