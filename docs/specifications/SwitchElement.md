# Switch

`SwitchElement` wraps `<vaadin-switch>` (Flow: `Switch`). A switch works like a
checkbox presented as an on/off toggle, with no indeterminate state: the
element turns it on and off, asserts its state, and covers label, helper,
validation, enablement and focus.

| | |
|---|---|
| Web component | `<vaadin-switch>` |
| Flow component | `com.vaadin.flow.component.checkbox.Switch` |
| Wrapper | `org.vaadin.addons.dramafinder.element.SwitchElement` |
| Typed query | `find(page, SwitchElement.class)` → `FieldQuery` |
| API | [SwitchElement](../../skills/vaadin-playwright-test/api-reference.md#switchelement) |

!!! note

    The Flow `Switch` is experimental. The application under test must enable
    the `com.vaadin.experimental.switchComponent` feature flag, e.g. in
    `src/main/resources/vaadin-featureflags.properties`:

    ```properties
    com.vaadin.experimental.switchComponent=true
    ```

## Finding the component

```java
// View
Switch notifications = new Switch("Email notifications");
notifications.setId("notifications");
notifications.setTestId("notifications-switch");
Switch unlabelled = new Switch();
unlabelled.setAriaLabel("Aria label");
```

```java
// Test
SwitchElement notifications = SwitchElement.getByLabel(page, "Email notifications");
SwitchElement partial = SwitchElement.getByLabelContaining(page, "notifications");
SwitchElement byId = SwitchElement.getById(page, "notifications");
SwitchElement byTestId = SwitchElement.getByTestId(page, "notifications-switch");

// Scoped, and the typed query
SwitchElement scoped = SwitchElement.getByLabel(page.locator("#settings"), "Email notifications");
SwitchElement viaQuery = find(page, SwitchElement.class).withLabel("Email notifications").single();
SwitchElement noLabel = find(page, SwitchElement.class).withAccessibleName("Aria label").single();
```

`getByLabel` / `withLabel` match the switch's own visible label in full and
case-sensitively; the `...Containing` variants match a case-insensitive
substring. Several matches fail with a strict-mode error. An `aria-label` is
not a label: use `withAccessibleName(...)` for an unlabelled switch.

## Turning it on and off

The checked-state API comes from `HasCheckedElement`; every action goes
through the native input and waits for the server round-trip.

```java
// View
Switch basic = new Switch("Default Switch");
Switch on = new Switch("Checked by default");
on.setValue(true);
```

```java
// Test
SwitchElement basic = SwitchElement.getByLabel(page, "Default Switch");
basic.assertNotChecked();
basic.check();
basic.assertChecked();
basic.uncheck();
basic.assertNotChecked();

basic.setChecked(true);         // when the state comes from a parameter
basic.assertChecked(true);

SwitchElement.getByLabel(page, "Checked by default").assertChecked();
```

`isChecked()` reads the state once, without retrying; use it only when the test
needs the value.

## Label, aria-label and helper

```java
// View
Switch withHelper = new Switch("Switch with helper");
withHelper.setHelperText("Helper text");
```

```java
// Test
SwitchElement.getByLabel(page, "Default Switch").assertLabel("Default Switch");
SwitchElement.getByLabel(page, "Switch with helper").assertHelperHasText("Helper text");

SwitchElement unlabelled = find(page, SwitchElement.class).withAccessibleName("Aria label").single();
unlabelled.assertAriaLabel("Aria label");  // read from the native input
unlabelled.assertLabel(null);              // null: no visible label
```

## Validation

```java
// View
Switch required = new Switch("Required Switch");
required.setRequiredIndicatorVisible(true);
required.setI18n(new Switch.SwitchI18n().setRequiredErrorMessage("Required Message"));
```

```java
// Test
SwitchElement required = SwitchElement.getByLabel(page, "Required Switch");
required.assertValid();
required.check();
required.uncheck();            // the user turned a required switch off
required.assertInvalid();
required.assertErrorMessage("Required Message");
```

## Enabled, focus and style

The disabled state and focus live on the native input.

```java
// View
Switch disabled = new Switch("Disabled Switch");
disabled.setEnabled(false);
Switch basic = new Switch("Default Switch");
basic.focus();
```

```java
// Test
SwitchElement.getByLabel(page, "Disabled Switch").assertDisabled();

SwitchElement basic = SwitchElement.getByLabel(page, "Default Switch");
SwitchElement other = SwitchElement.getByLabel(page, "Checked by default");
basic.assertIsFocused();
other.focus();
other.assertIsFocused();
basic.assertIsNotFocused();

basic.assertCssClass(null);    // null asserts no CSS class
```

## Pitfalls

- Without the `switchComponent` feature flag the application cannot use the Flow
  `Switch`, so there is no `<vaadin-switch>` for the element to find.
- There is no indeterminate state; use [Checkbox](CheckboxElement.md) for that.
- `getByLabel` does not match an `aria-label`; use
  `find(page, SwitchElement.class).withAccessibleName(...)`.

## See also

- [Checkbox](CheckboxElement.md), [Checkbox Group](CheckboxGroupElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [SwitchViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/SwitchViewIT.java)
