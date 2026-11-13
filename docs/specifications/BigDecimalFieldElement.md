# Big Decimal Field

`BigDecimalFieldElement` wraps `<vaadin-big-decimal-field>` (Flow:
`BigDecimalField`), the field for exact decimal values such as amounts of money.
It handles the value as text, and has the same field features as the other
number fields: label, helper, placeholder, prefix and suffix, clear button,
validation, allowed char pattern, theme, focus, enablement and tooltip.

| | |
|---|---|
| Web component | `<vaadin-big-decimal-field>` |
| Flow component | `com.vaadin.flow.component.textfield.BigDecimalField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.BigDecimalFieldElement` |
| Typed query | `find(page, BigDecimalFieldElement.class)` → `InputFieldQuery` |
| API | [BigDecimalFieldElement](../../skills/vaadin-playwright-test/api-reference.md#bigdecimalfieldelement) |

## Finding the component

```java
BigDecimalFieldElement amount = BigDecimalFieldElement.getByLabel(page, "Amount");   // whole label
BigDecimalFieldElement partial = BigDecimalFieldElement.getByLabelContaining(page, "amou");
BigDecimalFieldElement scoped = BigDecimalFieldElement.getByLabel(page.locator("#container"), "My Label");
BigDecimalFieldElement byId = BigDecimalFieldElement.getById(page, "amount");
BigDecimalFieldElement byTestId = BigDecimalFieldElement.getByTestId(page, "amount");

// Placeholder or aria-label only: use the typed query
BigDecimalFieldElement byPlaceholder = find(page, BigDecimalFieldElement.class).withPlaceholder("My Placeholder").single();
BigDecimalFieldElement byName = find(page, BigDecimalFieldElement.class).withAccessibleName("My AriaLabel").single();
```

`getByLabel` matches the whole visible label, case-sensitively;
`getByLabelContaining` a case-insensitive substring. A placeholder or an
`aria-label` is not a label.

## Find, set and assert

```java
// View
BigDecimalField amount = new BigDecimalField("Amount");
amount.setValue(new BigDecimal("123.456789"));
amount.setHelperText("Enter a precise decimal value");
```

```java
// Test
BigDecimalFieldElement amount = BigDecimalFieldElement.getByLabel(page, "Amount");
amount.assertHelperHasText("Enter a precise decimal value");
amount.assertValue("123.456789");

amount.setValue("98765.4321");
amount.assertValue("98765.4321");

amount.clear();
amount.assertValue("");
```

There is no typed (`BigDecimal`) overload: pass and assert the text exactly as the
field shows it. When the test needs the number, convert the text itself:

```java
amount.assertValue("98765.4321");
BigDecimal value = new BigDecimal(amount.getValue());
```

## Prefix and clear button

The mixins behave as on [Text Field](TextFieldElement.md):

```java
// View
BigDecimalField price = new BigDecimalField("Price");
price.setPrefixComponent(new Span("€"));
price.setClearButtonVisible(true);
```

```java
// Test
BigDecimalFieldElement price = BigDecimalFieldElement.getByLabel(page, "Price");
price.assertPrefixHasText("€");
price.setValue("19.90");
price.assertClearButtonVisible();
price.clickClearButton();
price.assertValue("");
```

## Pitfalls

- **Not a number field subclass.** `BigDecimalFieldElement` extends
  `VaadinElement` directly, not `AbstractNumberFieldElement`: it has no step
  buttons and no `min` / `max` / `step` helpers (the web component has none).
- **Text value only.** Compare the exact text: `assertValue("2.5")` does not match
  `2.50`. Flow formats the value with the decimal separator of the field's locale.
- **No read-only helpers; root locators for focus, enablement and `aria-label`.**
  As with the [number fields](NumberFieldElement.md#pitfalls), the wrapper keeps the
  default (host) locators, which the repository's ITs do not cover; fall back to
  Playwright on `getInputLocator()` or `getLocator()` if needed.

## See also

- [Number Field](NumberFieldElement.md), [Integer Field](IntegerFieldElement.md),
  [Text Field](TextFieldElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md)
- Full examples: [BigDecimalFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/BigDecimalFieldViewIT.java),
  [BigDecimalFieldGetByLabelIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/BigDecimalFieldGetByLabelIT.java)
