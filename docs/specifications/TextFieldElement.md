# Text Field

`TextFieldElement` wraps `<vaadin-text-field>` (Flow: `TextField`). It lets a test
type a value and assert it, and check everything a text field shows around its
input: label, helper, placeholder, prefix and suffix, clear button, validation
constraints and error message, read-only and disabled state, tooltip and theme.

This page is also the reference for the other text-like fields:
[Email Field](EmailFieldElement.md), [Password Field](PasswordFieldElement.md) and
[Text Area](TextAreaElement.md) extend `TextFieldElement` and inherit everything
below. The number fields ([Number Field](NumberFieldElement.md),
[Integer Field](IntegerFieldElement.md), [Big Decimal Field](BigDecimalFieldElement.md))
share most of the mixins but not the length and pattern constraints.

| | |
|---|---|
| Web component | `<vaadin-text-field>` |
| Flow component | `com.vaadin.flow.component.textfield.TextField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TextFieldElement` |
| Typed query | `find(page, TextFieldElement.class)` → `InputFieldQuery` |
| API | [TextFieldElement](../../skills/vaadin-playwright-test/api-reference.md#textfieldelement) |

## Finding the component

The label is the field's own visible label (`new TextField("Textfield")`).
`getByLabel` matches the **whole** label, case-sensitively;
`getByLabelContaining` matches a case-insensitive substring. Neither applies
`.first()`: two matching fields fail with a strict-mode error instead of picking
one silently.

```java
// Whole visible label
TextFieldElement field = TextFieldElement.getByLabel(page, "Textfield");

// Part of the label, case-insensitive
TextFieldElement validated = TextFieldElement.getByLabelContaining(page, "validated");

// Scoped to a container: only fields inside #container are considered
TextFieldElement inside = TextFieldElement.getByLabel(page.locator("#container"), "My Label");

// Fallbacks for a field without a user-visible name
TextFieldElement byId = TextFieldElement.getById(page, "first-name");
TextFieldElement byTestId = TextFieldElement.getByTestId(page, "first-name"); // Flow: setTestId("first-name")
```

An `aria-label` or a placeholder is **not** a label, so `getByLabel` does not find
a field that only has one of those. Use the typed query instead:

```java
// View
TextField search = new TextField();
search.setPlaceholder("My Placeholder");
TextField hidden = new TextField();
hidden.setAriaLabel("My AriaLabel");
```

```java
// Test
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

TextFieldElement search = find(page, TextFieldElement.class).withPlaceholder("My Placeholder").single();
TextFieldElement hidden = find(page, TextFieldElement.class).withAccessibleName("My AriaLabel").single();

// withAccessibleName also resolves aria-labelledby
TextFieldElement labelledBy = find(page, TextFieldElement.class).withAccessibleName("Labelled by").single();

// Other filters every query has
TextFieldElement styled = find(page, TextFieldElement.class).withClassName("validated-text-field").single();
int fieldCount = find(page, TextFieldElement.class).count();

// Search inside another component
TextFieldElement inDialog = dialog.find(TextFieldElement.class).withLabel("Name").single();
```

`InputFieldQuery` offers `withLabel`, `withLabelContaining`, `withPlaceholder`,
`withAccessibleName`, `withAccessibleNameContaining`, plus `withId`, `withTestId`,
`withAttribute` and `withClassName`. `getByLabel(page, label)` is exactly
`find(page, TextFieldElement.class).withLabel(label)`.

## Setting and reading the value

`setValue` fills the native input, dispatches a `change` event (so Flow's
value-change listeners run) and waits for the server round-trip.
`assertValue` retries until the input shows the expected text.

```java
// View
TextField name = new TextField("Textfield");
Span echo = new Span();
echo.setId("echo");
name.addValueChangeListener(e -> echo.setText(e.getValue()));
```

```java
// Test
TextFieldElement name = TextFieldElement.getByLabel(page, "Textfield");
name.assertValue("");
name.setValue("new value");
name.assertValue("new value");
assertThat(page.locator("#echo")).hasText("new value"); // the server got it

name.clear();             // same as setValue("")
name.assertValue("");
```

When the test really needs the value as a Java string (to reuse it later), read
it with `getValue()`; it is not retried, so assert first:

```java
name.assertValue("new value");
String typed = name.getValue();
```

## Clear button

```java
// View
TextField field = new TextField("TextField with placeholder and clear button");
field.setPlaceholder("Enter text here");
field.setClearButtonVisible(true);
```

```java
// Test
TextFieldElement field = TextFieldElement.getByLabel(page, "TextField with placeholder and clear button");
field.assertClearButtonNotVisible();   // hidden while the field is empty
field.setValue("some value");
field.assertClearButtonVisible();
field.clickClearButton();               // clicks part="clear-button", waits for the server
field.assertValue("");
```

`isClearButtonVisible()` returns the current state without retrying;
`getClearButtonLocator()` gives the raw locator for anything else.

## Placeholder

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "TextField with placeholder and clear button");
field.assertPlaceholder("Enter text here");
String placeholder = field.getPlaceholder();   // when the text is needed

field.setPlaceholder("Type here");             // client-side only, see Pitfalls
field.assertPlaceholder("Type here");
```

## Label, helper, prefix and suffix

```java
// View
TextField field = new TextField("Textfield");
field.setHelperText("Helper text");
field.setPrefixComponent(new Span("Prefix"));
field.setSuffixComponent(new Span("Suffix"));
```

```java
// Test
TextFieldElement field = TextFieldElement.getByLabel(page, "Textfield");
field.assertLabel("Textfield");
field.assertHelperHasText("Helper text");
field.assertPrefixHasText("Prefix");
field.assertSuffixHasText("Suffix");
```

`null` asserts absence: `assertLabel(null)` passes when the label is hidden
(a field named only by `aria-label`), and `assertPrefixHasText(null)` /
`assertSuffixHasText(null)` pass when there is no visible prefix or suffix.

```java
TextFieldElement plain = TextFieldElement.getByLabel(page, "TextField with helper component");
plain.assertPrefixHasText(null);
plain.assertSuffixHasText(null);
```

A helper can be a component (`setHelperComponent`). Wrap its locator in the
matching element to test it:

```java
// View
TextField field = new TextField("TextField with helper component");
TextField helper = new TextField("Helper field");
helper.setHelperText("Internal helper");
field.setHelperComponent(helper);
```

```java
// Test
TextFieldElement field = TextFieldElement.getByLabel(page, "TextField with helper component");
TextFieldElement helper = new TextFieldElement(field.getHelperLocator());
helper.assertVisible();
helper.assertHelperHasText("Internal helper");
```

The getters `getLabel()`, `getHelperText()`, `getPrefixText()` and
`getSuffixText()` return the current text; the locators `getLabelLocator()`,
`getHelperLocator()`, `getPrefixLocator()` and `getSuffixLocator()` are there for
Playwright assertions the wrapper does not provide:

```java
assertThat(field.getPrefixLocator()).isVisible();
```

## Validation

The wrapper reads the constraints from the native input, and the state from the
component's `invalid` attribute.

```java
// View
TextField code = new TextField("Validated Textfield");
code.setRequired(true);
code.setAllowedCharPattern("[0-8]");
code.setPattern("\\d{7}");
code.setMinLength(6);
code.setMaxLength(7);
code.setI18n(new TextField.TextFieldI18n()
        .setRequiredErrorMessage("Field is required")
        .setMinLengthErrorMessage("Minimum length is 6 characters")
        .setMaxLengthErrorMessage("Maximum length is 7 characters")
        .setPatternErrorMessage("Invalid code format"));
```

### Asserting the constraints

```java
TextFieldElement code = TextFieldElement.getByLabel(page, "Validated Textfield");
code.assertMinLength(6);
code.assertMaxLength(7);
code.assertPattern("\\d{7}");
code.assertAllowedCharPattern("[0-8]");
```

`null` asserts that the constraint is absent:

```java
TextFieldElement free = TextFieldElement.getByLabel(page, "Textfield");
free.assertMinLength(null);
free.assertMaxLength(null);
free.assertPattern(null);
```

The getters `getMinLength()`, `getMaxLength()` (both `Integer`, `null` when not
set), `getPattern()` and `getAllowedCharPattern()` return the current values.

### Asserting the validation state

```java
code.setValue("1");
code.assertInvalid();
code.assertErrorMessage("Minimum length is 6 characters");

code.setValue("123845");
code.assertInvalid();
code.assertErrorMessage("Invalid code format");

code.setValue("1238456");
code.assertValid();
```

A required field turns invalid once it is emptied:

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "Textfield");
field.setValue("new value");
field.assertValid();
field.setValue("");
field.assertInvalid();
field.assertErrorMessage("Field is required");
```

`getErrorMessageLocator()` returns the component's own `slot="error-message"`
child, for a Playwright assertion such as `isVisible()`.

### Changing constraints from the test

`setMinLength(int)`, `setMaxLength(int)`, `setPattern(String)` and
`setAllowedCharPattern(String)` set the property in the browser, then wait for the
server round-trip. Use them to exercise client-side validation; see Pitfalls.

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "Textfield");
field.setMaxLength(3);
field.assertMaxLength(3);
```

## Read-only and disabled

```java
// View
TextField readOnly = new TextField("Read-only Field");
readOnly.setValue("Read-only value");
readOnly.setReadOnly(true);
Button toggle = new Button("Toggle read-only",
        e -> readOnly.setReadOnly(!readOnly.isReadOnly()));
toggle.setId("toggle-read-only-button");

TextField disabled = new TextField("Enabled/Disabled Field");
disabled.setEnabled(false);
Button enable = new Button("Enable/Disable", e -> disabled.setEnabled(!disabled.isEnabled()));
enable.setId("enable-disable-button");
```

```java
// Test
TextFieldElement readOnly = TextFieldElement.getByLabel(page, "Read-only Field");
readOnly.assertReadOnly();
ButtonElement.getById(page, "toggle-read-only-button").click();
readOnly.assertNotReadOnly();
readOnly.assertReadOnly(false);      // boolean form, handy in parameterized tests

TextFieldElement disabled = TextFieldElement.getByLabel(page, "Enabled/Disabled Field");
disabled.assertDisabled();
ButtonElement.getById(page, "enable-disable-button").click();
disabled.assertEnabled();
disabled.assertEnabled(true);
```

Read-only comes from the host's reflected `readonly` attribute. Enablement is
checked on the native input (`getEnabledLocator()` returns `getInputLocator()`), so
Playwright's own enabled check applies, including a disabled ancestor `fieldset`.
`isReadOnly()`, `isReadOnly(boolean)`, `isEnabled()` and `isEnabled(boolean)`
return the state without retrying.

## Focus and blur

`focus()` and `blur()` act on the native input and wait for the server round-trip
they may trigger (Flow's `FocusEvent` / `BlurEvent` listeners).

```java
// View
TextField field = new TextField("Textfield");
field.focus();
```

```java
// Test
TextFieldElement field = TextFieldElement.getByLabel(page, "Textfield");
TextFieldElement other = TextFieldElement.getByLabel(page, "TextField with helper component");
field.assertIsFocused();
other.assertIsNotFocused();

other.focus();
other.assertIsFocused();
other.blur();
other.assertIsNotFocused();
```

`getTabIndex()` returns the input's `tabindex` attribute.

## Accessible name

The `aria-label` lives on the native input.

```java
TextFieldElement hidden = find(page, TextFieldElement.class).withAccessibleName("Invisible label").single();
hidden.assertAriaLabel("Invisible label");
hidden.assertLabel(null);   // no visible label

// Named through aria-labelledby: it has an accessible name, but no aria-label
TextFieldElement labelledBy = find(page, TextFieldElement.class).withAccessibleName("Labelled by").single();
labelledBy.assertAriaLabel(null);
```

`assertAriaLabel(null)` asserts the attribute is absent; `getAriaLabel()` returns
it, or `null`.

## Tooltip

```java
// View
field.setTooltipText("Tooltip for textfield");
```

```java
// Test
TextFieldElement field = TextFieldElement.getByLabel(page, "Textfield");
field.assertTooltipHasText("Tooltip for textfield");

// The full tooltip API: open it, check its position
TooltipElement tooltip = field.getTooltip();
tooltip.hoverTarget();
tooltip.assertOpened();
```

`assertTooltipHasText(null)` asserts the field shows no tooltip content.
`getTooltipText()` and `getTooltipLocator()` are the raw forms. See
[Tooltip](TooltipElement.md).

## Theme variants and CSS classes

```java
// View
TextField small = new TextField("TextField with theme");
small.addThemeVariants(TextFieldVariant.LUMO_SMALL);
small.addClassName("custom-text-field");
```

```java
// Test
TextFieldElement small = TextFieldElement.getByLabel(page, "TextField with theme");
small.assertTheme("small");                  // the whole theme attribute
small.assertHasThemeVariant("small");        // one variant among others
small.assertHasNoThemeVariant("align-right");
small.assertCssClass("custom-text-field");   // exactly these classes
```

`assertTheme` compares the full set of variants (in any order); with several
variants applied, list them all or use `assertHasThemeVariant`.
`assertTheme(null)` and `assertCssClass(null)` assert absence. `getTheme()` and
`getCssClass()` return the raw attributes.

## Visibility and raw locators

```java
field.assertVisible();
field.assertHidden();
assertThat(field.getInputLocator()).hasAttribute("maxlength", "7");
```

`getLocator()` is the `<vaadin-text-field>` host (component attributes such as
`invalid`, `readonly`, `theme`); `getInputLocator()` is the slotted native
`<input>` (value, focus, `minlength`, `maxlength`, `pattern`, `aria-label`).

## Pitfalls

- **Exact label match.** `getByLabel(page, "Name")` does not find a field labelled
  `"Name *"` or `"Full name"`; use `getByLabelContaining`. Placeholders and
  `aria-label`s are never labels: use `withPlaceholder` / `withAccessibleName`.
- **Value on the input, not the host.** `assertValue` checks the native input's
  value. `getValue()` reads the host's `value` property and does not retry.
- **Client-side setters.** `setMinLength`, `setMaxLength`, `setPattern`,
  `setAllowedCharPattern` and `setPlaceholder` change the web component in the
  browser. Flow's server-side `TextField` does not learn about them, so server
  validation (binder, `setRequired`) keeps the view's settings. Configure
  constraints in the view when the server's behaviour is what you test.
- **Error message without invalid.** `assertErrorMessage` only checks the text of
  the error slot; pair it with `assertInvalid()` to check that it is shown.
- **Several theme variants.** `assertTheme("small")` fails on a field that also
  has `align-right`; use `assertHasThemeVariant("small")`.
- **`assertAriaLabel(null)` is not "no accessible name".** A field named by
  `aria-labelledby` has no `aria-label` attribute.
- **Server-side reactions.** Every action waits for the server round-trip; what
  the browser renders afterwards (an error message appearing, a notification)
  still needs a retrying assertion such as `assertInvalid()`.

## See also

- [Email Field](EmailFieldElement.md), [Password Field](PasswordFieldElement.md),
  [Text Area](TextAreaElement.md), [Number Field](NumberFieldElement.md),
  [Integer Field](IntegerFieldElement.md), [Big Decimal Field](BigDecimalFieldElement.md),
  [Custom Field](CustomFieldElement.md), [Tooltip](TooltipElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full examples: [TextFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TextFieldViewIT.java),
  [TextFieldGetByLabelIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TextFieldGetByLabelIT.java)
