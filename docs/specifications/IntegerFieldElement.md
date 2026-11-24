# Integer Field

`IntegerFieldElement` wraps `<vaadin-integer-field>` (Flow: `IntegerField`). It is
the whole-number sibling of [Number Field](NumberFieldElement.md): the same
`AbstractNumberFieldElement` base (step buttons and the field mixins), with the
value, `min`, `max` and `step` typed as `Integer`.

| | |
|---|---|
| Web component | `<vaadin-integer-field>` |
| Flow component | `com.vaadin.flow.component.textfield.IntegerField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.IntegerFieldElement` (extends `AbstractNumberFieldElement`) |
| Typed query | `find(page, IntegerFieldElement.class)` → `InputFieldQuery` |
| API | [IntegerFieldElement](../../skills/vaadin-playwright-test/api-reference.md#integerfieldelement), [AbstractNumberFieldElement](../../skills/vaadin-playwright-test/api-reference.md#abstractnumberfieldelement) |

## Finding the component

```java
IntegerFieldElement quantity = find(page, IntegerFieldElement.class).withLabel("Quantity").single();   // whole label
IntegerFieldElement score = find(page, IntegerFieldElement.class).withLabelContaining("score").single();
IntegerFieldElement scoped = find(page.locator("#container"), IntegerFieldElement.class).withLabel("My Label").single();
IntegerFieldElement byId = IntegerFieldElement.getById(page, "quantity");
IntegerFieldElement byTestId = IntegerFieldElement.getByTestId(page, "quantity");

// Placeholder or aria-label only: use the typed query
IntegerFieldElement byName = find(page, IntegerFieldElement.class).withAccessibleName("Items").single();
```

`withLabel` matches the whole visible label, case-sensitively;
`withLabelContaining` a case-insensitive substring. Only
`<vaadin-integer-field>` elements are considered.

## Find, set and assert

```java
// View
IntegerField quantity = new IntegerField("Quantity");
quantity.setHelperText("Enter the number of items");
Span serverValue = new Span();
serverValue.setId("quantity-server-value");
quantity.addValueChangeListener(e -> serverValue.setText("Server value: " + e.getValue()));
```

```java
// Test
IntegerFieldElement quantity = find(page, IntegerFieldElement.class).withLabel("Quantity").single();
quantity.assertHelperHasText("Enter the number of items");

quantity.setValue(42);
quantity.assertValue(42);
quantity.assertValue("42");      // the text form works too
assertThat(page.locator("#quantity-server-value")).hasText("Server value: 42");

quantity.setValue(-7);
quantity.assertValue(-7);

quantity.setValue("123");        // text overload
quantity.assertValue(123);
```

`setValue(Integer)` types the number in plain decimal form. `assertValue(Integer)`
retries until the field holds that number; text that is not a whole number within
`int` range never matches. `getValueAsInteger()` returns the value, `null` when
empty, and throws `NumberFormatException` when the field holds text that is not a
whole number (for instance `3000000000`, beyond `Integer.MAX_VALUE`).

`null` means empty; cast it, since a bare `null` is ambiguous with the `String`
overloads:

```java
IntegerFieldElement score = find(page, IntegerFieldElement.class).withLabel("Score").single();
score.setValue((Integer) null);    // same as clear()
score.assertValue((Integer) null);
```

## Min, max and step

```java
// View
IntegerField score = new IntegerField("Score");
score.setHelperText("Value between 0 and 100, in steps of 10");
score.setValue(10);
score.setMin(0);
score.setMax(100);
score.setStep(10);
```

```java
// Test
IntegerFieldElement score = find(page, IntegerFieldElement.class).withLabel("Score").single();
score.assertMin(0);
score.assertMax(100);
score.assertStep(10);
score.assertValue(10);

score.setValue(110);        // above max: kept, but invalid
score.assertValue(110);
score.assertInvalid();

score.setValue(-10);        // below min
score.assertInvalid();

score.setValue(20);
score.assertValid();
```

`getMin()`, `getMax()` and `getStep()` return an `Integer`. `setMin(int)`,
`setMax(int)` and `setStep(int)` change the constraint in the browser only and
wait for the server round-trip.

## Step buttons

```java
// View
IntegerField withControls = new IntegerField("With Controls");
withControls.setStepButtonsVisible(true);
```

```java
// Test
IntegerFieldElement field = find(page, IntegerFieldElement.class).withLabel("With Controls").single();
field.assertHasControls(true);
field.assertValue((Integer) null);

field.clickIncreaseButton();
field.assertValue(1);
field.clickIncreaseButton();
field.assertValue(2);
field.clickDecreaseButton();
field.assertValue(1);
```

The other shared features (placeholder, prefix and suffix, clear button, helper,
validation, theme, tooltip) are described on
[Number Field](NumberFieldElement.md#the-abstractnumberfieldelement-base) and
[Text Field](TextFieldElement.md).

## Pitfalls

- **An unset bound reads as `"undefined"` on the input.** The web component
  writes an unset `min` / `max` to the native input as the string
  `"undefined"`. `getMin()` / `getMax()` return `null` for it, and
  `assertMin(null)` / `assertMax(null)` pass for it or for a missing attribute.
- **`getValueAsInteger()` throws on non-integers;** `assertValue(Integer)` simply
  does not match. Prefer the assertion.
- The [Number Field pitfalls](NumberFieldElement.md#pitfalls) apply too: no
  read-only helpers, host-level focus/enablement/`aria-label` locators, literal `null`.

## See also

- [Number Field](NumberFieldElement.md), [Big Decimal Field](BigDecimalFieldElement.md),
  [Text Field](TextFieldElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md)
- Full example: [IntegerFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/IntegerFieldViewIT.java)
