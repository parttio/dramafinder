# Date Picker

`DatePickerElement` wraps `<vaadin-date-picker>` (Flow: `DatePicker`). It lets a
test set a date either as a `LocalDate` or by typing text as a user does, assert
the value in both forms, and check validation (min/max, bad input), helper,
theme and the other field states.

| | |
|---|---|
| Web component | `<vaadin-date-picker>` |
| Flow component | `com.vaadin.flow.component.datepicker.DatePicker` |
| Wrapper | `org.vaadin.addons.dramafinder.element.DatePickerElement` |
| Typed query | `find(page, DatePickerElement.class)` → `InputFieldQuery` |
| API | [DatePickerElement](../../skills/vaadin-playwright-test/api-reference.md#datepickerelement) |

## Finding the component

```java
// By the visible label (exact, case-sensitive) or part of it
DatePickerElement appointment = DatePickerElement.getByLabel(page, "Appointment date");
DatePickerElement vacation = DatePickerElement.getByLabelContaining(page, "vacation");

// Scoped to a container
DatePickerElement inForm = DatePickerElement.getByLabel(page.locator("#container"), "Birthday");

// Fallbacks for a picker without a visible label
DatePickerElement byId = DatePickerElement.getById(page, "birthday");
DatePickerElement byTestId = DatePickerElement.getByTestId(page, "birthday");

// Typed query: label, placeholder or accessible name
DatePickerElement byPlaceholder = find(page, DatePickerElement.class).withPlaceholder("My Placeholder").single();
DatePickerElement unlabelled = find(page, DatePickerElement.class).withAccessibleName("Invisible label").single();
```

`getByLabel` matches the whole visible label and never an `aria-label` or a
placeholder; `getByLabelContaining` matches a case-insensitive substring.
Neither applies `.first()`.

## Setting and reading the value

There are two ways in, and they use different formats:

| Call | Format | What happens |
|---|---|---|
| `setValue(LocalDate)` | ISO-8601 (`2025-09-20`) | sets the `value` property directly, no parsing |
| `setValue(String)` | the localized display format (`dd/mm/yyyy` in the test views, e.g. `"15/05/2023"`) | types the text and presses Enter, as a user does |
| `assertValue(LocalDate)` | ISO-8601 | compares the `value` property |
| `assertValue(String)` | the localized display format | compares the text in the input |

Pick the overload that matches the format you work with.

```java
// View
DatePicker datePicker = new DatePicker("Appointment date");
datePicker.setHelperText("Enter the appointment date");
```

```java
// Test
DatePickerElement appointment = DatePickerElement.getByLabel(page, "Appointment date");
appointment.setValue(LocalDate.of(2025, 9, 20));
appointment.assertValue(LocalDate.of(2025, 9, 20));   // the value property
appointment.assertValue("20/09/2025");                // the input text
```

`getValueAsLocalDate()` returns the value as a `LocalDate` (`null` when empty)
when the test needs it; `getValue()` returns the raw ISO `value` property, not
the input text.

## Typing as a user does

`setValue(String)` fills the input and presses Enter on it. No synthetic
`change` event is dispatched, so the picker parses and commits the text itself,
exactly as in a browser, and Flow sees the events of a real user. That makes the
bad-input path testable: text the picker cannot parse leaves the value empty and
the field invalid, while the text stays in the input.

```java
// View
DatePicker datePicker = new DatePicker("Typed date");
datePicker.setI18n(new DatePicker.DatePickerI18n().setBadInputErrorMessage("Bad input."));
```

```java
// Test
DatePickerElement date = DatePickerElement.getByLabel(page, "Typed date");
date.setValue("20/09/2025");
date.assertValue(LocalDate.of(2025, 9, 20));
date.assertValid();

date.setValue("dsdds");
date.assertInvalid();
date.assertErrorMessage("Bad input.");
date.assertValue("dsdds");                                   // the input keeps the text
assertThat(date.getLocator()).hasJSProperty("value", "");    // the value is empty
```

## Clearing

`clear()` types an empty string, which empties the value. With
`setClearButtonVisible(true)` you can click the clear button instead. Either
way, assert emptiness with the `LocalDate` overload and an explicit `null` cast
to pick the overload:

```java
date.clear();
date.assertValue((LocalDate) null);

date.clickClearButton();             // when the app shows the clear button
date.assertValue((LocalDate) null);
```

`assertValue((LocalDate) null)` asserts that the input is empty.

## Min, max and validation messages

```java
// View
DatePicker datePicker = new DatePicker("Vacation date");
LocalDate now = LocalDate.now();
datePicker.setValue(now.plusDays(7));
datePicker.setMin(now);
datePicker.setMax(now.plusDays(30));
datePicker.setI18n(new DatePicker.DatePickerI18n()
        .setMaxErrorMessage("Maximum number of days exceeded.")
        .setMinErrorMessage("Minimum number of days exceeded."));
```

```java
// Test
DatePickerElement vacation = DatePickerElement.getByLabel(page, "Vacation date");
LocalDate today = LocalDate.now();
vacation.assertValue(today.plusDays(7));

vacation.setValue(today.plusDays(31));
vacation.assertInvalid();
vacation.assertErrorMessage("Maximum number of days exceeded.");

vacation.setValue(today.minusDays(1));
vacation.assertErrorMessage("Minimum number of days exceeded.");

vacation.setValue(today.plusDays(7));
vacation.assertValid();
```

## Label, helper, tooltip, theme and focus

```java
// View
DatePicker datePicker = new DatePicker("Appointment date");
datePicker.setHelperText("Enter the appointment date");
datePicker.setTooltipText("Tooltip for datepicker");
datePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
```

```java
// Test
DatePickerElement appointment = DatePickerElement.getByLabel(page, "Appointment date");
appointment.assertLabel("Appointment date");
appointment.assertHelperHasText("Enter the appointment date");
appointment.assertTooltipHasText("Tooltip for datepicker");
appointment.assertTheme("small");

DatePickerElement vacation = DatePickerElement.getByLabel(page, "Vacation date");
vacation.focus();                    // focuses the input
vacation.assertIsFocused();
appointment.assertIsNotFocused();
```

The placeholder helpers (`assertPlaceholder`, `getPlaceholder`,
`setPlaceholder`) and `assertCssClass(..)` are available as on every input
field.

## Accessible name and enabled state

```java
find(page, DatePickerElement.class).withAccessibleName("Invisible label").single()
        .assertAriaLabel("Invisible label");
// The name comes from aria-labelledby: there is no aria-label
find(page, DatePickerElement.class).withAccessibleName("Labelled by").single()
        .assertAriaLabel(null);

DatePickerElement toggled = DatePickerElement.getByLabel(page, "Enabled/Disabled Field");
toggled.assertDisabled();
ButtonElement.getByText(page, "Enable/Disable").click();
toggled.assertEnabled();
```

The `aria-label`, enablement and focus are read on the inner input.

## Pitfalls

- **Two formats.** `String` overloads use the display format of the picker's
  locale; `LocalDate` overloads use ISO-8601. `assertValue("2025-09-20")` fails
  against an input showing `20/09/2025`.
- **`getValue()` is ISO, `assertValue(String)` is the display text.** They are
  not each other's counterpart; use `getInputLocator().inputValue()` for the
  text the user sees.
- **`setValue(LocalDate)` skips parsing.** To test what the user types (and bad
  input), use `setValue(String)`.
- **`assertValue(null)` is ambiguous.** Cast it: `assertValue((LocalDate) null)`.
- **No open/close helpers.** The element does not drive the calendar overlay;
  type the date instead.

## See also

- [Time Picker](TimePickerElement.md), [Date Time Picker](DateTimePickerElement.md)
- [Finding components](../guide/finding-components.md),
  [Assertions](../guide/assertions.md),
  [Actions and waiting](../guide/actions-and-waiting.md),
  [Core concepts](../guide/core-concepts.md)
- Full example: [DatePickerViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/DatePickerViewIT.java)
