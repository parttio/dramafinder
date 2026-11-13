# Time Picker

`TimePickerElement` wraps `<vaadin-time-picker>` (Flow: `TimePicker`). It lets a
test set a time as a `LocalTime` or as text, assert the value in both forms, and
check validation (min/max), helper, theme and the other field states.

| | |
|---|---|
| Web component | `<vaadin-time-picker>` |
| Flow component | `com.vaadin.flow.component.timepicker.TimePicker` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TimePickerElement` |
| Typed query | `find(page, TimePickerElement.class)` → `InputFieldQuery` |
| API | [TimePickerElement](../../skills/vaadin-playwright-test/api-reference.md#timepickerelement) |

## Finding the component

```java
// By the visible label (exact, case-sensitive) or part of it
TimePickerElement appointment = TimePickerElement.getByLabel(page, "Appointment time");
TimePickerElement meeting = TimePickerElement.getByLabelContaining(page, "meeting");

// Scoped to a container
TimePickerElement inForm = TimePickerElement.getByLabel(page.locator("#container"), "Start");

// Fallbacks for a picker without a visible label
TimePickerElement byId = TimePickerElement.getById(page, "start");
TimePickerElement byTestId = TimePickerElement.getByTestId(page, "start");

// Typed query: label, placeholder or accessible name
TimePickerElement unlabelled = find(page, TimePickerElement.class).withAccessibleName("Invisible label").single();
TimePickerElement byPlaceholder = find(page, TimePickerElement.class).withPlaceholder("hh:mm").single();
```

`getByLabel` matches the whole visible label and never an `aria-label` or a
placeholder; `getByLabelContaining` matches a case-insensitive substring.
Neither applies `.first()`.

## Setting and reading the value

| Call | Format | What happens |
|---|---|---|
| `setValue(LocalTime)` | `HH:mm` | sets the `value` property directly |
| `setValue(String)` | `HH:mm` (e.g. `"09:00"`) | fills the input and dispatches a `change` event |
| `assertValue(LocalTime)` | `HH:mm` | compares the `value` property |
| `assertValue(String)` | `HH:mm` | compares the text in the input |

`setValue(String)` and `assertValue(String)` are the generic input-field
helpers; pick the overload matching the format you have.

```java
// View
TimePicker timePicker = new TimePicker("Appointment time");
timePicker.setHelperText("Enter the appointment time");
```

```java
// Test
TimePickerElement appointment = TimePickerElement.getByLabel(page, "Appointment time");
appointment.setValue(LocalTime.of(14, 30));
appointment.assertValue(LocalTime.of(14, 30));
appointment.assertValue("14:30");

appointment.setValue("09:00");
appointment.assertValue("09:00");
```

`getValueAsLocalTime()` returns the value as a `LocalTime` (`null` when empty)
when the test needs it; `getValue()` returns the raw `value` property.

## Clearing

With `setClearButtonVisible(true)` click the clear button; `clear()` empties the
input without it. Assert emptiness with the `LocalTime` overload and an explicit
`null` cast to pick the overload:

```java
time.clickClearButton();
time.assertValue((LocalTime) null);

time.clear();
time.assertValue((LocalTime) null);
```

## Min, max, step and validation messages

```java
// View
TimePicker timePicker = new TimePicker("Meeting time");
timePicker.setValue(LocalTime.of(10, 30));
timePicker.setMin(LocalTime.of(9, 0));
timePicker.setMax(LocalTime.of(17, 0));
timePicker.setStep(Duration.ofMinutes(30));
timePicker.setI18n(new TimePicker.TimePickerI18n()
        .setMaxErrorMessage("Maximum time exceeded")
        .setMinErrorMessage("Minimum time exceeded"));
```

```java
// Test
TimePickerElement meeting = TimePickerElement.getByLabel(page, "Meeting time");
meeting.assertValue(LocalTime.of(10, 30));

meeting.setValue(LocalTime.of(11, 0));
meeting.assertValid();

meeting.setValue(LocalTime.of(18, 0));
meeting.assertInvalid();
meeting.assertErrorMessage("Maximum time exceeded");

meeting.setValue(LocalTime.of(8, 0));
meeting.assertErrorMessage("Minimum time exceeded");

meeting.setValue(LocalTime.of(10, 30));
meeting.assertValid();
```

## Label, helper, tooltip, theme and focus

```java
// View
TimePicker timePicker = new TimePicker("Appointment time");
timePicker.setHelperText("Enter the appointment time");
timePicker.setTooltipText("Tooltip for component");
timePicker.addThemeVariants(TimePickerVariant.LUMO_SMALL);
```

```java
// Test
TimePickerElement appointment = TimePickerElement.getByLabel(page, "Appointment time");
appointment.assertLabel("Appointment time");
appointment.assertHelperHasText("Enter the appointment time");
appointment.assertTooltipHasText("Tooltip for component");
appointment.assertTheme("small");

TimePickerElement meeting = TimePickerElement.getByLabel(page, "Meeting time");
meeting.focus();                     // focuses the input
meeting.assertIsFocused();
appointment.assertIsNotFocused();
```

The placeholder helpers and `assertCssClass(..)` are available as on every input
field.

## Accessible name and enabled state

```java
find(page, TimePickerElement.class).withAccessibleName("Invisible label").single()
        .assertAriaLabel("Invisible label");
// The name comes from aria-labelledby: there is no aria-label
find(page, TimePickerElement.class).withAccessibleName("Labelled by").single()
        .assertAriaLabel(null);

TimePickerElement toggled = TimePickerElement.getByLabel(page, "Enabled/Disabled Field");
toggled.assertDisabled();
ButtonElement.getByText(page, "Enable/Disable").click();
toggled.assertEnabled();
```

The `aria-label`, enablement and focus are read on the inner input.

## Pitfalls

- **`assertValue(null)` is ambiguous.** Cast it: `assertValue((LocalTime) null)`.
- **Minutes only.** The `LocalTime` helpers format and parse `HH:mm`
  (`TimePickerElement.LOCAL_TIME`); a picker whose step shows seconds has a value
  they do not read.
- **`setValue(String)` is not typed key by key.** It fills the input and
  dispatches `change`. For a picker inside a
  [Date Time Picker](DateTimePickerElement.md), use that element's
  `setTime(..)`, which types and presses Enter like a user.

## See also

- [Date Picker](DatePickerElement.md), [Date Time Picker](DateTimePickerElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [TimePickerViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TimePickerViewIT.java)
