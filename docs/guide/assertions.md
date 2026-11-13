# Assertions

Every state an element exposes comes with **retrying assertions**. Use them for
every check: they wait for the browser to catch up, and their failure message
says what was expected and what the page showed.

## Assert, don't get

```java
GridElement grid = GridElement.get(page);
TextFieldElement.getByLabel(page, "Filter").setValue("ada");

grid.assertRowCount(1);                    // ✅ polls until the grid shows 1 row
assertEquals(1, grid.getTotalRowCount());  // ❌ reads once: flaky on a lazy grid
```

The action waited for the server, but the browser may still be rendering the
response — a grid fetching its rows, an overlay opening, a debounced field. An
`assertX` call polls until the state matches or the timeout expires; a getter
reads a single snapshot.

Getters are still useful when you need the value itself, e.g. to reuse it:

```java
String orderNumber = TextFieldElement.getByLabel(page, "Order number").getValue();
NotificationElement.getByText(page, orderNumber).assertOpen();
```

## Failure messages

Assertions report the state they checked, the expected value and the last
value they saw:

```text
Grid row count mismatch. Expected: <1>, actual: <42>
```

```text
Locator expected to have value: ada
Received: Ada
```

## Naming conventions

Assertions come in symmetric pairs, often with a boolean variant for
parameterised tests:

```java
checkbox.assertChecked();
checkbox.assertNotChecked();
checkbox.assertChecked(expected);

field.assertEnabled();
field.assertDisabled();
field.assertEnabled(false);

dialog.assertOpen();
dialog.assertClosed();

field.assertReadOnly();
field.assertNotReadOnly();
```

## Visibility

Every element has `assertVisible()` and `assertHidden()`:

```java
// View: vatNumber.setVisible(country.getValue() == Country.FINLAND)
ComboBoxElement.getByLabel(page, "Country").selectItem("Finland");
TextFieldElement.getByLabel(page, "VAT number").assertVisible();
```

Some components refine them. `DialogElement.assertHidden()` checks the overlay,
`ConfirmDialogElement.assertHidden()` also passes when the dialog is gone from
the DOM.

## Values

Fields assert the value **as the user sees it** in the input:

```java
TextFieldElement name = TextFieldElement.getByLabel(page, "Name");
name.setValue("Ada");
name.assertValue("Ada");

ComboBoxElement.getByLabel(page, "Country").assertValue("Finland");      // item label
SelectElement.getByLabel(page, "Size").assertValue("Large");             // item label
CheckboxGroupElement.getByLabel(page, "Toppings").assertSelected("Cheese", "Ham"); // exactly these
```

Typed fields add overloads with Java types:

```java
DatePickerElement.getByLabel(page, "Birthday").assertValue(LocalDate.of(1815, 12, 10));
TimePickerElement.getByLabel(page, "Alarm").assertValue(LocalTime.of(7, 30));
DateTimePickerElement.getByLabel(page, "Meeting").assertValue(LocalDateTime.of(2026, 1, 15, 9, 0));
```

## Validation

```java
// View
TextField email = new TextField("Email");
binder.forField(email).asRequired("Email is required").bind(...);
```

```java
TextFieldElement email = TextFieldElement.getByLabel(page, "Email");
ButtonElement.getByText(page, "Save").click();

email.assertInvalid();
email.assertErrorMessage("Email is required");

email.setValue("ada@example.com");
email.assertValid();
```

Constraints set on the component have their own assertions:

```java
TextFieldElement code = TextFieldElement.getByLabel(page, "Code");
code.assertMinLength(6);
code.assertMaxLength(8);
code.assertPattern("[A-Z0-9]+");
code.assertAllowedCharPattern("[A-Z0-9]");

IntegerFieldElement quantity = IntegerFieldElement.getByLabel(page, "Quantity");
quantity.assertMin(1);
quantity.assertMax(99);
```

## Label, helper, placeholder, prefix and suffix

```java
TextFieldElement price = TextFieldElement.getByLabel(page, "Price");
price.assertLabel("Price");
price.assertHelperHasText("Including VAT");
price.assertPlaceholder("0.00");
price.assertPrefixHasText("€");
price.assertSuffixHasText("/ month");
price.assertTooltipHasText("Monthly price shown to customers");
```

## Enabled and read-only

```java
ButtonElement save = ButtonElement.getByText(page, "Save");
save.assertDisabled();
TextFieldElement.getByLabel(page, "Name").setValue("Ada");
save.assertEnabled();

TextFieldElement.getByLabel(page, "Customer number").assertReadOnly();
```

## Theme variants and CSS classes

Vaadin writes every theme variant into one space-separated `theme` attribute.
`assertTheme` compares the **whole** attribute, in any order; use
`assertHasThemeVariant` to check one variant among several:

```java
// View: save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL)
ButtonElement save = ButtonElement.getByText(page, "Save");
save.assertTheme("small primary");          // every variant, any order
save.assertHasThemeVariant("primary");      // one variant
save.assertHasNoThemeVariant("error");

save.assertCssClass("save-button");         // exactly these classes
```

## Focus

```java
TextFieldElement name = TextFieldElement.getByLabel(page, "Name");
name.assertIsFocused();       // e.g. after the dialog opened with setAutofocus(true)
page.keyboard().press("Tab");
name.assertIsNotFocused();
```

## `null` means "absent"

Assertions that take the expected text treat `null` as "there is none", so a
test can assert the absence of a label, an attribute or a slot:

```java
field.assertLabel(null);           // no visible label
field.assertTooltipHasText(null);  // no tooltip content
field.assertPrefixHasText(null);   // empty prefix slot
field.assertMinLength(null);       // no minLength attribute
field.assertAriaLabel(null);       // no aria-label
button.assertTheme(null);          // no theme attribute
button.assertCssClass((String[]) null); // no class
card.assertTitle(null);            // no title
```

Each method's Javadoc says whether it accepts `null`; the
[API reference](../../skills/vaadin-playwright-test/api-reference.md) carries
the one-liner.

## Text is compared exactly

Text assertions compare the component's whole text, case-sensitively (most
after collapsing whitespace). An element's own text never includes slotted
children or text rendered in its shadow DOM: a button's `getText()` leaves out
its tooltip, a badge's `assertText` leaves out its number.

```java
// new Badge("unread messages", 5)
BadgeElement badge = BadgeElement.getByText(page, "unread messages");
badge.assertText("unread messages");
badge.assertNumber(5);
```

## Playwright assertions

Element assertions cover the component's state; Playwright's own assertions
cover the rest — a page title, a URL, a plain HTML element, or a locator an
element hands you:

```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

assertThat(page).hasTitle("Orders | Acme");
assertThat(page.getByText("No orders yet")).isVisible();

DialogElement dialog = DialogElement.getByHeaderText(page, "Order 42");
assertThat(dialog.getContentLocator()).containsText("Shipped");

var cells = GridElement.get(page).getHeaderCells(0);
assertThat(cells.get(0).getCellContentLocator()).hasText("Name");
```

To assert that something is **not** there, use an assertion that retries until
it disappears, not a one-shot check:

```java
NotificationElement.getByText(page, "Saving").assertClosed();
GridElement.get(page).assertColumnNotPresent("Internal id");
assertThat(find(page, ButtonElement.class).withText("Delete").getLocator()).hasCount(0);
// not: assertFalse(find(page, ButtonElement.class).withText("Delete").exists());
```

## Timeouts

Assertions poll until a timeout expires:

| Assertion kind | Timeout | Change it with |
|---|---|---|
| Mixin assertions built on Playwright (`assertValue`, `assertEnabled`, `assertLabel`...) and your own `assertThat` | 5 s | `PlaywrightAssertions.setDefaultAssertionTimeout(ms)` |
| Component assertions that compute a value (`assertRowCount`, `assertCellContent`, `assertSelected`...) | the page's default timeout, 15 s in the base classes | `page.setDefaultTimeout(ms)` |

Raise them for a slow environment rather than adding sleeps.

## Keep tests focused

A test that checks one behaviour fails for one reason. Prefer several short
tests over one long scenario, and name each after the behaviour:

```java
@Test
void saveIsDisabledUntilTheFormIsValid() {
    ButtonElement.getByText(page, "Save").assertDisabled();
}

@Test
void emailIsRequired() {
    ButtonElement.getByText(page, "Validate").click();
    TextFieldElement.getByLabel(page, "Email").assertErrorMessage("Email is required");
}
```

See also the [testing guidelines](../../skills/vaadin-playwright-test/TESTING.md).
