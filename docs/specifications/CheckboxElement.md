# Checkbox

`CheckboxElement` wraps `<vaadin-checkbox>` (Flow: `Checkbox`). It checks and
unchecks the box through its native input, asserts the checked and
indeterminate states, and covers label, helper, validation, enablement and
focus.

| | |
|---|---|
| Web component | `<vaadin-checkbox>` |
| Flow component | `com.vaadin.flow.component.checkbox.Checkbox` |
| Wrapper | `org.vaadin.addons.dramafinder.element.CheckboxElement` |
| Typed query | `find(page, CheckboxElement.class)` → `FieldQuery` |
| API | [CheckboxElement](../../skills/vaadin-playwright-test/api-reference.md#checkboxelement) |

## Finding the component

```java
// View
Checkbox terms = new Checkbox("I accept the terms");
terms.setId("terms");
terms.setTestId("terms-checkbox");
terms.addClassName("terms");
Checkbox unlabelled = new Checkbox();
unlabelled.setAriaLabel("Aria label");
```

```java
// Test
CheckboxElement terms = CheckboxElement.getByLabel(page, "I accept the terms");   // whole label
CheckboxElement same = CheckboxElement.getByLabelContaining(page, "accept");     // substring
CheckboxElement byId = CheckboxElement.getById(page, "terms");
CheckboxElement byTestId = CheckboxElement.getByTestId(page, "terms-checkbox");

// Scoped to a container
CheckboxElement inForm = CheckboxElement.getByLabel(page.locator("#form"), "I accept the terms");

// Typed query
CheckboxElement viaQuery = find(page, CheckboxElement.class).withLabel("I accept the terms").single();
CheckboxElement noLabel = find(page, CheckboxElement.class).withAccessibleName("Aria label").single();
```

`getByLabel` matches the text of the checkbox's own `label` slot in full and
case-sensitively; `getByLabelContaining` matches a case-insensitive substring.
Neither applies `.first()`, so two matches fail with a strict-mode error. An
`aria-label` is not a label: find an unlabelled checkbox with
`withAccessibleName(...)`.

Checkboxes inside a [Checkbox Group](CheckboxGroupElement.md) are reached
through the group (`group.getCheckbox("Option 1")`).

## Checking and unchecking

`check()`, `uncheck()` and `setChecked(boolean)` act on the native input (as a
click does) and wait for the server round-trip.

```java
// View
Checkbox checkbox = new Checkbox("Default Checkbox");
Checkbox checked = new Checkbox("Checked by default");
checked.setValue(true);
```

```java
// Test
CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Default Checkbox");
checkbox.assertNotChecked();
checkbox.check();
checkbox.assertChecked();
checkbox.uncheck();
checkbox.assertNotChecked();

checkbox.setChecked(true);      // when the state comes from a parameter
checkbox.assertChecked(true);

CheckboxElement.getByLabel(page, "Checked by default").assertChecked();
```

`isChecked()` reads the state once without retrying; use it only when the test
needs the value.

!!! warning

    `isChecked(boolean)` on `CheckboxElement` is a deprecated **setter** kept
    for compatibility: it checks or unchecks the box. Use `setChecked(boolean)`
    to change the state and `assertChecked(boolean)` to verify it.

## Indeterminate state

```java
// View
Checkbox checkbox = new Checkbox("Indeterminate Checkbox");
checkbox.setIndeterminate(true);
```

```java
// Test
CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Indeterminate Checkbox");
checkbox.assertIndeterminate();

checkbox.check();               // a user click clears the indeterminate state
checkbox.assertNotIndeterminate();
checkbox.assertChecked();

checkbox.setIndeterminate(true); // set the client-side property directly
checkbox.assertIndeterminate();
```

`isIndeterminate()` reads the reflected `indeterminate` attribute once.
`setIndeterminate(boolean)` writes the DOM property from a script, as a test
setup step; it is not something a user can do.

## Label, aria-label and helper

```java
CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Default Checkbox");
checkbox.assertLabel("Default Checkbox");

CheckboxElement unlabelled = find(page, CheckboxElement.class).withAccessibleName("Aria label").single();
unlabelled.assertAriaLabel("Aria label");  // read from the native input
unlabelled.assertLabel(null);              // null: no visible label

checkbox.assertHelperHasText("Helper text");
```

## Validation

```java
// View
Checkbox checkbox = new Checkbox("Required Checkbox");
checkbox.setRequiredIndicatorVisible(true);
checkbox.setI18n(new Checkbox.CheckboxI18n().setRequiredErrorMessage("Required Message"));
```

```java
// Test
CheckboxElement required = CheckboxElement.getByLabel(page, "Required Checkbox");
required.assertValid();
required.check();
required.uncheck();          // the user cleared a required checkbox
required.assertInvalid();
required.assertErrorMessage("Required Message");
```

## Enabled and disabled

The disabled state is read from the native input.

```java
// View
Checkbox checkbox = new Checkbox("Disabled Checkbox");
checkbox.setEnabled(false);
```

```java
// Test
CheckboxElement.getByLabel(page, "Disabled Checkbox").assertDisabled();
CheckboxElement.getByLabel(page, "Default Checkbox").assertEnabled();
```

## Focus and styling

Focus goes to the native input.

```java
CheckboxElement first = CheckboxElement.getByLabel(page, "Default Checkbox");
CheckboxElement second = CheckboxElement.getByLabel(page, "Checked by default");
first.assertIsFocused();       // the view called checkbox.focus()
second.focus();
second.assertIsFocused();
first.assertIsNotFocused();

CheckboxElement terms = CheckboxElement.getByLabel(page, "I accept the terms");
terms.assertCssClass("terms"); // exactly these classes; null asserts none
```

## Pitfalls

- The checked state, the `aria-label`, focus and enablement all live on the
  native `<input slot="input">`, not on the `<vaadin-checkbox>` host. Use
  `getInputLocator()` if you need the input for a raw Playwright assertion.
- `isChecked(boolean)` changes the state (deprecated, to be removed); it is not a
  query.
- `getByLabel` does not match an `aria-label`; use
  `find(page, CheckboxElement.class).withAccessibleName(...)`.

## See also

- [Checkbox Group](CheckboxGroupElement.md), [Switch](SwitchElement.md),
  [Radio Button Group](RadioButtonGroupElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [CheckboxViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/CheckboxViewIT.java)
