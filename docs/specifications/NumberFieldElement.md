# Number Field

`NumberFieldElement` wraps `<vaadin-number-field>` (Flow: `NumberField`, a
`Double` field). Next to the text value every field has, it reads and sets the
value as a `Double`, asserts `min`, `max` and `step`, and clicks the step buttons.

| | |
|---|---|
| Web component | `<vaadin-number-field>` |
| Flow component | `com.vaadin.flow.component.textfield.NumberField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.NumberFieldElement` (extends `AbstractNumberFieldElement`) |
| Typed query | `find(page, NumberFieldElement.class)` → `InputFieldQuery` |
| API | [NumberFieldElement](../../skills/vaadin-playwright-test/api-reference.md#numberfieldelement), [AbstractNumberFieldElement](../../skills/vaadin-playwright-test/api-reference.md#abstractnumberfieldelement) |

## Finding the component

```java
NumberFieldElement weight = NumberFieldElement.getByLabel(page, "Weight");          // whole label
NumberFieldElement measure = NumberFieldElement.getByLabelContaining(page, "measure");
NumberFieldElement scoped = NumberFieldElement.getByLabel(page.locator("#container"), "My Label");
NumberFieldElement byId = NumberFieldElement.getById(page, "weight");
NumberFieldElement byTestId = NumberFieldElement.getByTestId(page, "weight");

// Placeholder or aria-label only: use the typed query
NumberFieldElement byPlaceholder = find(page, NumberFieldElement.class).withPlaceholder("kg").single();
NumberFieldElement byName = find(page, NumberFieldElement.class).withAccessibleName("Weight in kg").single();
```

`getByLabel` matches the whole visible label, case-sensitively;
`getByLabelContaining` a case-insensitive substring. Only `<vaadin-number-field>`
elements are considered, not integer or big-decimal fields.

## Setting and reading the value

The value can be handled as text (inherited `setValue(String)` /
`assertValue(String)`) or as a number:

```java
// View
NumberField weight = new NumberField("Weight");
weight.setHelperText("Enter the weight in kg");
Span serverValue = new Span();
serverValue.setId("weight-server-value");
weight.addValueChangeListener(e -> serverValue.setText("Server value: " + e.getValue()));
```

```java
// Test
NumberFieldElement weight = NumberFieldElement.getByLabel(page, "Weight");
weight.assertHelperHasText("Enter the weight in kg");

// As a number
weight.setValue(170.0);
weight.assertValue("170");           // typed the way a user would: no ".0"
weight.assertValue(170.0);
assertThat(page.locator("#weight-server-value")).hasText("Server value: 170.0");

weight.setValue(180.5);
weight.assertValue(180.5);

// As text
weight.setValue("12.3");
weight.assertValue("12.3");
```

`assertValue(Double)` compares numerically and retries: `2.5` matches the text
`2.50`, and `0.0` matches `-0`. Text that is not a number never matches.

```java
weight.setValue("2.50");
weight.assertValue(2.5);
```

`null` means empty. Cast it, because a bare `null` is ambiguous between the
`String` and `Double` overloads:

```java
NumberFieldElement measurement = NumberFieldElement.getByLabel(page, "Measurement");
measurement.setValue((Double) null);   // same as clear()
measurement.assertValue((Double) null);
measurement.assertValue("");
```

`getValueAsDouble()` returns the value when the test needs it, or `null` when the
field is empty (the browser also reports text it does not accept as a number as
empty). `setValue(Double)` throws `IllegalArgumentException` for `NaN` and
infinities.

## Min, max and step

```java
// View
NumberField measurement = new NumberField("Measurement");
measurement.setValue(1.5);
measurement.setMin(0.5);
measurement.setMax(10);
measurement.setStep(0.5);
```

```java
// Test
NumberFieldElement measurement = NumberFieldElement.getByLabel(page, "Measurement");
measurement.assertMin(0.5);
measurement.assertMax(10.0);      // the attribute "10" matches 10.0
measurement.assertStep(0.5);
measurement.assertValue(1.5);

// Out of range: the value is kept, the field turns invalid
measurement.setValue("11.5");
measurement.assertValue("11.5");
measurement.assertInvalid();

measurement.setValue("0");
measurement.assertInvalid();

measurement.setValue("2.5");
measurement.assertValid();
```

The getters `getMin()`, `getMax()` and `getStep()` return a `Double`, or `null`
when the input has no such attribute. `setMin(double)`, `setMax(double)` and
`setStep(double)` change the constraint in the browser only (Flow's server-side
field keeps its own settings) and wait for the server round-trip.

## Step buttons

```java
// View
NumberField withControls = new NumberField("With Controls");
withControls.setStepButtonsVisible(true);
withControls.setStep(0.1);
```

```java
// Test
NumberFieldElement field = NumberFieldElement.getByLabel(page, "With Controls");
field.assertHasControls(true);
field.assertValue("");

field.clickIncreaseButton();
field.assertValue("0.1");
field.clickIncreaseButton();
field.assertValue("0.2");
field.clickDecreaseButton();
field.assertValue("0.1");
```

`assertHasControls(false)` asserts the buttons are hidden. Each click waits for the
server round-trip.

## The AbstractNumberFieldElement base

`NumberFieldElement` and [`IntegerFieldElement`](IntegerFieldElement.md) extend
`AbstractNumberFieldElement`, which holds what both share:

| Method | What it does |
|---|---|
| `assertHasControls(boolean)` / `getHasControls()` | step buttons shown (the `step-buttons-visible` attribute) |
| `clickIncreaseButton()` / `clickDecreaseButton()` | click `part~="increase-button"` / `part~="decrease-button"` |

and the field mixins: value (`setValue(String)`, `assertValue(String)`, `clear()`),
label, helper, placeholder, prefix and suffix, clear button, validation
(`assertValid`, `assertInvalid`, `assertErrorMessage`), allowed char pattern,
theme, CSS class, focus, `aria-label`, enablement and tooltip. They behave as on
[Text Field](TextFieldElement.md):

```java
// View
NumberField price = new NumberField("Price");
price.setPrefixComponent(new Span("€"));
price.setClearButtonVisible(true);
price.setPlaceholder("0.00");
price.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
```

```java
// Test
NumberFieldElement price = NumberFieldElement.getByLabel(page, "Price");
price.assertPlaceholder("0.00");
price.assertPrefixHasText("€");
price.assertHasThemeVariant("align-right");
price.setValue(9.99);
price.clickClearButton();
price.assertValue((Double) null);
```

`AbstractNumberFieldElement` is abstract and has no factories; use one of the
concrete elements. [`BigDecimalFieldElement`](BigDecimalFieldElement.md) has the
same mixins but does not extend it (no step buttons, no min/max/step).

## Pitfalls

- **No read-only helpers.** The number wrappers do not implement
  `HasReadOnlyElement`. Assert the host attribute with Playwright:
  `assertThat(field.getLocator()).hasAttribute("readonly", "")`.
- **Root locators for focus, enablement and `aria-label`.** Unlike
  `TextFieldElement`, which points these at the native input, the number wrappers
  keep the default (the component host). The repository's ITs do not cover
  disabled, focused or `aria-label` number fields; if `assertDisabled()`,
  `assertIsFocused()` or `assertAriaLabel(...)` does not behave as expected, assert
  on `getInputLocator()` with Playwright (`isDisabled()`, `isFocused()`,
  `hasAttribute("aria-label", ...)`).
- **`getHasControls()` and the empty attribute.** `step-buttons-visible` is a
  boolean attribute with an empty value, which `getHasControls()` parses as
  `false`. Use `assertHasControls(true)`, which checks the attribute's presence.
- **`null` bounds.** `assertMin(null)`, `assertMax(null)` and `assertStep(null)`
  check against the web component's "unset" marker values (`undefined` for
  min/max, `any` for step) rather than a missing attribute; prefer asserting
  concrete bounds.
- **Literal `null`.** `setValue(null)` / `assertValue(null)` do not compile
  (ambiguous overloads): write `(Double) null` or call `clear()`.
- **Out-of-range values are kept.** Typing `11.5` into a field with `max = 10`
  leaves `11.5` in the field and marks it invalid; it is not clamped.

## See also

- [Integer Field](IntegerFieldElement.md), [Big Decimal Field](BigDecimalFieldElement.md),
  [Text Field](TextFieldElement.md) (shared field features), [Slider](SliderElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [NumberFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/NumberFieldViewIT.java)
