# DateTimePickerElement Specification

## Overview

`DateTimePickerElement` is a Playwright element wrapper for the `<vaadin-date-time-picker>` web component. It composes a `DatePickerElement` and `TimePickerElement` and exposes helpers to interact using `LocalDateTime`.

## Tag Name

```
vaadin-date-time-picker
```

## Class Hierarchy

```
VaadinElement
    └── DateTimePickerElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasInputFieldElement` | Input field operations |
| `HasValidationPropertiesElement` | Validation properties |
| `HasClearButtonElement` | Clear button functionality |
| `HasPlaceholderElement` | Placeholder text support |
| `HasThemeElement` | Theme variants support |
| `FocusableElement` | Focus operations |
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasEnabledElement` | Enabled/disabled state |
| `HasTooltipElement` | Tooltip support |
| `HasLabelElement` | Label support |
| `HasHelperElement` | Helper text support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-date-time-picker"` | HTML tag name |
| `ISO_LOCAL_DATE_TIME` | DateTimeFormatter | ISO format with custom time |

## API Methods

### Constructor

```java
DateTimePickerElement(Locator locator)
```

Creates a new `DateTimePickerElement` from a Playwright locator. Internally creates `DatePickerElement` and `TimePickerElement` children.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a date-time picker by its label.

```java
DateTimePickerElement picker = DateTimePickerElement.getByLabel(page, "Appointment");
```

#### getByLabel(Locator locator, String label)

Locates a date-time picker by label within a scope.

### Value Methods

| Method | Description |
|--------|-------------|
| `setValue(LocalDateTime date)` | Set value using LocalDateTime |
| `setValue(String value)` | Set value as string (dd/mm/yyyy hh:mm) |
| `getValueAsLocalDateTime()` | Get value as LocalDateTime or null |

### Partial Value Methods

| Method | Description |
|--------|-------------|
| `setDate(String date)` | Set only the date part |
| `setTime(String time)` | Set only the time part |
| `assertDateValue(String date)` | Assert date sub-field value |
| `assertTimeValue(String time)` | Assert time sub-field value |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(String value)` | Assert value as string |
| `assertValue(LocalDateTime value)` | Assert value as LocalDateTime |

### Overridden Methods

| Method | Description |
|--------|-------------|
| `getAriaLabel()` | Returns date picker's ARIA label |
| `assertAriaLabel(String)` | Asserts both date and time ARIA labels |
| `isEnabled()` | Returns true if both sub-fields enabled |
| `assertEnabled()` | Asserts both sub-fields enabled |
| `assertDisabled()` | Asserts both sub-fields disabled |

## Usage Examples

### Basic Usage

```java
DateTimePickerElement appointment = DateTimePickerElement.getByLabel(page, "Appointment");

// Set value with LocalDateTime
appointment.setValue(LocalDateTime.of(2023, 5, 15, 14, 30));

// Get value
LocalDateTime dateTime = appointment.getValueAsLocalDateTime();

// Assert value
appointment.assertValue(LocalDateTime.of(2023, 5, 15, 14, 30));
```

### Partial Updates

```java
DateTimePickerElement meeting = DateTimePickerElement.getByLabel(page, "Meeting");

// Set date and time separately
meeting.setDate("15/05/2023");
meeting.setTime("14:30");

// Assert individual parts
meeting.assertDateValue("15/05/2023");
meeting.assertTimeValue("14:30");
```

### Enabled State

```java
DateTimePickerElement picker = DateTimePickerElement.getByLabel(page, "Schedule");

// Both date and time must be enabled
picker.assertEnabled();

// Both date and time must be disabled
picker.assertDisabled();
```

## Related Elements

- `DatePickerElement` - For date only selection
- `TimePickerElement` - For time only selection
