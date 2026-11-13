# Date Time Picker

`DateTimePickerElement` wraps `<vaadin-date-time-picker>` (Flow:
`DateTimePicker`). It lets a test set a `LocalDateTime`, type into the date and
time halves as a user does, assert the value as a whole or per half, and check
validation (min/max, bad and incomplete input).

| | |
|---|---|
| Web component | `<vaadin-date-time-picker>` |
| Flow component | `com.vaadin.flow.component.datetimepicker.DateTimePicker` |
| Wrapper | `org.vaadin.addons.dramafinder.element.DateTimePickerElement` |
| Typed query | `find(page, DateTimePickerElement.class)` → `DateTimePickerQuery` |
| API | [DateTimePickerElement](../../skills/vaadin-playwright-test/api-reference.md#datetimepickerelement) |

## Finding the component

```java
// By the visible label (exact, case-sensitive) or part of it
DateTimePickerElement appointment = DateTimePickerElement.getByLabel(page, "Appointment date");
DateTimePickerElement typed = DateTimePickerElement.getByLabelContaining(page, "typed");

// Scoped to a container
DateTimePickerElement inForm = DateTimePickerElement.getByLabel(page.locator("#container"), "Meeting");

// Fallbacks for a picker without a visible label
DateTimePickerElement byId = DateTimePickerElement.getById(page, "meeting");
DateTimePickerElement byTestId = DateTimePickerElement.getByTestId(page, "meeting");

// Typed query: label, date placeholder or accessible name
DateTimePickerElement byPlaceholder =
        find(page, DateTimePickerElement.class).withPlaceholder("My Placeholder").single();
DateTimePickerElement unlabelled =
        find(page, DateTimePickerElement.class).withAccessibleName("Invisible label").single();
```

The label is the composite's own label, never the label of a nested half.
`DateTimePickerQuery.withPlaceholder(..)` reads the **date** picker's
placeholder. `getByLabel` matches the whole label and never an `aria-label` or a
placeholder; `getByLabelContaining` matches a case-insensitive substring.

## A composite of two halves

`DateTimePickerElement` composes a [`DatePickerElement`](DatePickerElement.md)
and a [`TimePickerElement`](TimePickerElement.md), exposed by `getDatePicker()`
and `getTimePicker()`. Use them for anything the halves offer on their own:

```java
DateTimePickerElement typed = DateTimePickerElement.getByLabel(page, "Typed date time");
typed.typeText("20/09/2025", "10:00");
typed.getDatePicker().assertValue(LocalDate.of(2025, 9, 20));
typed.getTimePicker().assertValue(LocalTime.of(10, 0));
```

Being a composite changes several inherited behaviours:

| Method | On a date time picker |
|---|---|
| `getAriaLabel()` | the **date** picker's `aria-label` |
| `assertAriaLabel(String)` | asserts it on **both** halves |
| `isEnabled()`, `assertEnabled()`, `assertDisabled()` | require **both** halves to be in that state |
| `setValue(String)`, `assertValue(String)` | touch the **date** half's input only |
| `clear()` | empties **both** halves |
| `focus()` | focuses the date input |

## Setting and reading the value

`setValue(LocalDateTime)` sets both halves' values directly and dispatches a
`change` event on the host. `assertValue(LocalDateTime)` compares the host's ISO
value (`2025-09-20T10:00`).

```java
// View
DateTimePicker dateTimePicker = new DateTimePicker("Appointment date");
dateTimePicker.setHelperText("Enter the appointment date and time");
```

```java
// Test
DateTimePickerElement appointment = DateTimePickerElement.getByLabel(page, "Appointment date");
LocalDateTime meeting = LocalDateTime.of(2025, 9, 20, 10, 0);
appointment.setValue(meeting);
appointment.assertValue(meeting);

// Each half shows the value in its locale's format
appointment.assertDateValue("20/09/2025");
appointment.assertTimeValue("10:00");
```

`getValueAsLocalDateTime()` returns the value (`null` when empty), and
`getDateValue()` / `getTimeValue()` the text of each half as the user sees it,
when the test needs the value itself.

## Typing as a user does

`typeText(date, time)`, `setDate(date)` and `setTime(time)` fill a half's input
and press Enter on it, then leave the field. No synthetic `change` event is
dispatched. The picker checks its halves when focus leaves it, so:

- an unparsable half (`typeText("dsdds", "10:00")`) leaves the value empty and
  the field invalid (bad input);
- one empty half (`typeText("", "10:00")`) leaves the value empty and the field
  invalid (incomplete);
- `setDate` alone leaves the field invalid (incomplete) until `setTime` fills
  the other half; use `typeText` to type both halves before leaving.

Each half uses its locale's format (`dd/mm/yyyy` and `hh:mm` in the test views).

```java
// View
DateTimePicker dateTimePicker = new DateTimePicker("Typed date time");
dateTimePicker.setI18n(new DateTimePicker.DateTimePickerI18n()
        .setBadInputErrorMessage("Bad input.")
        .setIncompleteInputErrorMessage("Incomplete input."));
```

```java
// Test
DateTimePickerElement typed = DateTimePickerElement.getByLabel(page, "Typed date time");

typed.typeText("15/05/2023", "14:30");
typed.assertDateValue("15/05/2023");
typed.assertTimeValue("14:30");
typed.assertValue(LocalDateTime.of(2023, 5, 15, 14, 30));
typed.assertValid();

typed.typeText("dsdds", "10:00");
typed.assertInvalid();
typed.assertErrorMessage("Bad input.");
assertThat(typed.getLocator()).hasJSProperty("value", "");

typed.typeText("", "10:00");
typed.assertErrorMessage("Incomplete input.");
```

Setting the halves one by one:

```java
DateTimePickerElement appointment = DateTimePickerElement.getByLabel(page, "Appointment date");
appointment.setDate("20/09/2025");     // invalid (incomplete) until the time is set
appointment.setTime("10:00");
appointment.assertValue(LocalDateTime.of(2025, 9, 20, 10, 0));
appointment.assertValid();

appointment.setTime("");               // empty the time half again
appointment.assertInvalid();
```

## Clearing

`clear()` empties both halves and leaves the field, so the value becomes empty
(`null` on the server); it is the same as `typeText("", "")`. Assert emptiness
with the `LocalDateTime` overload and an explicit `null` cast:

```java
typed.clear();
typed.assertDateValue("");
typed.assertTimeValue("");
typed.assertValue((LocalDateTime) null);
typed.assertValid();
```

Don't clear with `setValue("")`: it only empties the date half and leaves the
time.

## Min, max and validation messages

```java
// View
DateTimePicker dateTimePicker = new DateTimePicker("Vacation date");
LocalDateTime start = LocalDateTime.of(2025, 9, 18, 14, 30);
dateTimePicker.setValue(start.plusDays(7));
dateTimePicker.setMin(start);
dateTimePicker.setMax(start.plusDays(30));
dateTimePicker.setI18n(new DateTimePicker.DateTimePickerI18n()
        .setMaxErrorMessage("Maximum exceeded.")
        .setMinErrorMessage("Minimum exceeded."));
```

```java
// Test
DateTimePickerElement vacation = DateTimePickerElement.getByLabel(page, "Vacation date");
LocalDateTime start = LocalDateTime.of(2025, 9, 18, 14, 30);
vacation.assertValue(start.plusDays(7));

vacation.setValue(start.plusDays(31));
vacation.assertInvalid();
vacation.assertErrorMessage("Maximum exceeded.");

vacation.setValue(start.minusDays(1));
vacation.assertErrorMessage("Minimum exceeded.");

vacation.setValue(start.plusDays(7));
vacation.assertValid();
```

## Label, helper, tooltip, theme and focus

```java
// View
DateTimePicker dateTimePicker = new DateTimePicker("Appointment date");
dateTimePicker.setHelperText("Enter the appointment date and time");
dateTimePicker.setTooltipText("Tooltip for datetimepicker");
dateTimePicker.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
```

```java
// Test
DateTimePickerElement appointment = DateTimePickerElement.getByLabel(page, "Appointment date");
appointment.assertLabel("Appointment date");
appointment.assertHelperHasText("Enter the appointment date and time");
appointment.assertTooltipHasText("Tooltip for datetimepicker");
appointment.assertTheme("small");

DateTimePickerElement vacation = DateTimePickerElement.getByLabel(page, "Vacation date");
vacation.focus();
vacation.assertIsFocused();
appointment.assertIsNotFocused();
```

## Accessible name and enabled state

```java
// View: dateTimePicker.setAriaLabel("Invisible label");
find(page, DateTimePickerElement.class).withAccessibleName("Invisible label").single()
        .assertAriaLabel("Invisible label");   // checked on both halves

DateTimePickerElement toggled = DateTimePickerElement.getByLabel(page, "Enabled/Disabled Field");
toggled.assertDisabled();                       // both halves disabled
ButtonElement.getByText(page, "Enable/Disable").click();
toggled.assertEnabled();                        // both halves enabled
```

## Pitfalls

- **`setValue(String)` / `assertValue(String)` see the date half only.**
  `setValue("")` leaves the time; use `typeText(date, time)` and `clear()` for
  the whole field, `assertDateValue` / `assertTimeValue` for each half.
- **Validation runs when focus leaves the field.** `setDate` alone makes the
  field invalid (incomplete) until the time is set.
- **Two formats.** Typed text uses each half's locale format; `LocalDateTime`
  values use ISO-8601 with minutes (`yyyy-MM-ddTHH:mm`).
- **`assertValue(null)` is ambiguous.** Cast it:
  `assertValue((LocalDateTime) null)`.
- **Placeholders live on the halves.** `withPlaceholder` reads the date half's
  placeholder; assert each one with `getDatePicker().assertPlaceholder(..)` /
  `getTimePicker().assertPlaceholder(..)`.

## See also

- [Date Picker](DatePickerElement.md), [Time Picker](TimePickerElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [DateTimePickerViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/DateTimePickerViewIT.java)
