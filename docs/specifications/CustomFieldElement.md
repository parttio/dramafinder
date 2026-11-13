# Custom Field

`CustomFieldElement` wraps `<vaadin-custom-field>` (Flow: `CustomField<T>`), a
field built from several wrapped fields. The custom field carries the label,
helper text, error message, required, invalid, disabled and read-only states for
them as a whole; the values live in the wrapped fields, which the test reaches with
`getField(...)`.

| | |
|---|---|
| Web component | `<vaadin-custom-field>` |
| Flow component | `com.vaadin.flow.component.customfield.CustomField` |
| Wrapper | `org.vaadin.addons.dramafinder.element.CustomFieldElement` |
| Typed query | `find(page, CustomFieldElement.class)` → `FieldQuery` |
| API | [CustomFieldElement](../../skills/vaadin-playwright-test/api-reference.md#customfieldelement) |

The examples on this page use a phone field made of a country-code select and a
number text field:

```java
// View
static class PhoneField extends CustomField<String> {
    private final Select<String> code = new Select<>();
    private final TextField number = new TextField();

    PhoneField(String label) {
        setLabel(label);
        code.setItems("+358", "+49", "+1");
        code.setAriaLabel("Country code");   // names the wrapped fields
        number.setAriaLabel("Number");
        add(code, number);
    }

    @Override
    protected String generateModelValue() { ... }   // "+358 40 1234567"

    @Override
    protected void setPresentationValue(String value) { ... }
}

PhoneField phone = new PhoneField("Phone");
phone.setHelperText("Country code and number");
phone.setRequiredIndicatorVisible(true);
Span value = new Span();
value.setId("phone-value");
phone.addValueChangeListener(e -> value.setText(e.getValue()));
```

## Finding the component

```java
CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");                // whole label
CustomFieldElement office = CustomFieldElement.getByLabelContaining(page, "office");   // "Office phone"
CustomFieldElement fax = CustomFieldElement.getByLabel(page.locator("main"), "Fax");    // scoped
CustomFieldElement byId = CustomFieldElement.getById(page, "phone");
CustomFieldElement byTestId = CustomFieldElement.getByTestId(page, "phone");

// Typed query: label filters, plus the filters every query has
CustomFieldElement work = find(page, CustomFieldElement.class).withLabel("Work phone").single();
CustomFieldElement styled = find(page, CustomFieldElement.class).withClassName("work-phone").single();
```

`getByLabel` matches the whole label, case-sensitively:
`getByLabel(page, "Office")` does not find `"Office phone"`.
`getByLabelContaining` matches a case-insensitive substring. A custom field is
never found by the label of a field it wraps: with an inner text field labelled
`"CustomField inner"`, `CustomFieldElement.getByLabel(page, "CustomField inner")`
finds nothing, while `TextFieldElement.getByLabel` finds the inner field.
`FieldQuery` has no placeholder filter.

## Setting values through the wrapped fields

A `vaadin-custom-field`'s client-side `value` is a tab-separated join of the
wrapped fields' client values — a `vaadin-select` contributes its item *key*
(`"2"`), not the label (`"+49"`) — so it says little about what the user sees, and
`CustomFieldElement` has no value getter. Set and assert values on the wrapped
fields, and assert the server-side result through whatever the view renders from
it:

```java
CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
phone.getField(SelectElement.class, "Country code").selectItem("+358");

TextFieldElement number = phone.getField(TextFieldElement.class, "Number");
number.setValue("40 1234567");
number.assertValue("40 1234567");

assertThat(page.locator("#phone-value")).hasText("+358 40 1234567");   // the server-side value
```

## Reaching a wrapped field

### By accessible name

`getField(type, name)` is `find(customField.getLocator(), type).withAccessibleName(name).single()`.
Wrapped fields rarely have a visible label; give them an `aria-label` (Flow:
`setAriaLabel`) and pass that. The name must match in full and case-sensitively,
and two wrapped fields with the same name fail with a strict-mode error.

```java
SelectElement code = phone.getField(SelectElement.class, "Country code");
TextFieldElement number = phone.getField(TextFieldElement.class, "Number");
```

The type must be usable with `find` (it implements `Findable`, as every field
element does). Any other type throws `IllegalArgumentException`, whose message
points to `getField(type, index)`:

```java
assertThrows(IllegalArgumentException.class,
        () -> phone.getField(ContextMenuElement.class, "Country code"));
```

### By position

`getField(type, index)` takes the n-th (0-based) **direct child** of that type, in
DOM order, for fields with no accessible name. The type must carry
`@PlaywrightElement` (every public element does), otherwise it throws
`IllegalArgumentException`.

```java
// View
PhoneField office = new PhoneField("Office phone");
office.setValue("+49 30 1234567");
```

```java
// Test
CustomFieldElement office = CustomFieldElement.getByLabel(page, "Office phone");
office.getField(SelectElement.class, 0).assertValue("+49");
office.getField(TextFieldElement.class, 0).assertValue("30 1234567");
```

`ViewOutline` lists a custom field's wrapped fields under it, with their names, as
`fields (getField(Type.class, label or index)): ...`.

## Label, helper and required

```java
CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
phone.assertLabel("Phone");
phone.assertHelperHasText("Country code and number");
phone.assertRequired();          // Flow: setRequiredIndicatorVisible(true)

CustomFieldElement mobile = CustomFieldElement.getByLabel(page, "Mobile phone");
mobile.assertNotRequired();
```

Required is the host's `required` attribute. `getLabel()` and `getHelperText()`
return the text when the test needs it.

## Validation

```java
// View
PhoneField mobile = new PhoneField("Mobile phone");
mobile.setErrorMessage("Enter a valid number");
Button validate = new Button("Mark mobile invalid", e -> mobile.setInvalid(true));
```

```java
// Test
CustomFieldElement mobile = CustomFieldElement.getByLabel(page, "Mobile phone");
mobile.assertValid();
ButtonElement.getByText(page, "Mark mobile invalid").click();
mobile.assertInvalid();
mobile.assertErrorMessage("Enter a valid number");
```

## Disabled and read-only

```java
// View
PhoneField fax = new PhoneField("Fax");
fax.setEnabled(false);

PhoneField home = new PhoneField("Home phone");
home.setValue("+358 9 7654321");
home.setReadOnly(true);
```

```java
// Test
CustomFieldElement fax = CustomFieldElement.getByLabel(page, "Fax");
fax.assertDisabled();
fax.getField(TextFieldElement.class, "Number").assertDisabled();   // Flow disables the wrapped fields too

CustomFieldElement home = CustomFieldElement.getByLabel(page, "Home phone");
home.assertReadOnly();

CustomFieldElement phone = CustomFieldElement.getByLabel(page, "Phone");
phone.assertEnabled();
phone.assertNotReadOnly();
```

- **Disabled** is the host's `disabled` attribute (the host has the `group` role,
  which Playwright does not treat as disableable), so the wrapper implements
  `HasDisabledAttributeElement`: `isEnabled()`, `assertEnabled()` and
  `assertDisabled()` all read that attribute. Flow also disables every wrapped
  field.
- **Read-only** is a property the web component does not reflect, so
  `isReadOnly()` / `assertReadOnly()` / `assertNotReadOnly()` read the property.
  Flow does not pass read-only on to the wrapped fields; check them separately if
  it matters.

## Tooltip, CSS class and theme

```java
// View
PhoneField work = new PhoneField("Work phone");
work.setTooltipText("Your direct line");
work.addClassName("work-phone");
```

```java
// Test
CustomFieldElement work = CustomFieldElement.getByLabel(page, "Work phone");
work.assertTooltipHasText("Your direct line");
work.assertCssClass("work-phone");
work.assertTheme(null);          // no theme variant applied
```

## Own slots only

`getHelperLocator()`, `getErrorMessageLocator()`, `getLabelLocator()` and
`getTooltip()` read the custom field's own slotted elements, never those of a
wrapped field. A wrapped field's helper or tooltip is reached through the wrapped
field:

```java
// When the view gives the wrapped number field its own helper text
phone.getField(TextFieldElement.class, "Number").assertHelperHasText("Without the leading zero");
```

## Pitfalls

- **No value getter.** The client-side value is a tab-separated join of client
  values (select keys, not labels). Assert the wrapped fields or what the server
  renders.
- **Wrapped fields need a name or a position.** They rarely have a visible
  label: name them with `setAriaLabel` and use `getField(type, name)`, or use
  `getField(type, index)`.
- **`getField(type, index)` counts direct children only.** A field nested inside a
  layout within the custom field is not counted; give it an `aria-label` and use
  `getField(type, name)`, which searches the whole subtree.
- **Read-only does not propagate.** A read-only custom field's wrapped fields stay
  editable as far as their own state goes.

## See also

- [Text Field](TextFieldElement.md), [Select](SelectElement.md),
  [Combo Box](ComboBoxElement.md), [Date Time Picker](DateTimePickerElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [CustomFieldViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/CustomFieldViewIT.java)
