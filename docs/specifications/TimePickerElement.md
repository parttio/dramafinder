# TimePickerElement Specification

## Overview

`TimePickerElement` is a Playwright element wrapper for the `<vaadin-time-picker>` web component. It adds convenience methods for `LocalTime` values and lookup by label.

## Tag Name

```
vaadin-time-picker
```

## Class Hierarchy

```
VaadinElement
    └── TimePickerElement
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
| `FIELD_TAG_NAME` | `"vaadin-time-picker"` | HTML tag name |
| `LOCAL_TIME` | DateTimeFormatter | HH:mm format |

## API Methods

### Constructor

```java
TimePickerElement(Locator locator)
```

Creates a new `TimePickerElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a time picker by its label.

```java
TimePickerElement time = TimePickerElement.getByLabel(page, "Start Time");
```

#### getByLabel(Locator locator, String label)

Locates a time picker by label within a scope.

### Value Methods

| Method | Description |
|--------|-------------|
| `setValue(LocalTime time)` | Set value using LocalTime (HH:mm format) |
| `getValueAsLocalTime()` | Get value as LocalTime or null |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(LocalTime value)` | Assert value as LocalTime |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getAriaLabelLocator()` | Returns input locator |
| `getFocusLocator()` | Returns input locator |
| `getEnabledLocator()` | Returns input locator |

## Usage Examples

### Basic Usage

```java
TimePickerElement startTime = TimePickerElement.getByLabel(page, "Start Time");

// Set value with LocalTime
startTime.setValue(LocalTime.of(14, 30));

// Get value
LocalTime time = startTime.getValueAsLocalTime(); // 14:30

// Assert value
startTime.assertValue(LocalTime.of(14, 30));
```

### String Format

```java
TimePickerElement time = TimePickerElement.getByLabel(page, "Meeting Time");

// Set using string (inherited from HasInputFieldElement)
time.setValue("09:00");
time.assertValue("09:00");
```

### Clear and Validation

```java
TimePickerElement time = TimePickerElement.getByLabel(page, "Time");

// Clear
time.clickClearButton();
time.assertValue((LocalTime) null);

// Validation
time.assertRequired();
time.assertInvalid();

time.setValue(LocalTime.of(10, 0));
time.assertValid();
```

### Enabled State

```java
TimePickerElement time = TimePickerElement.getByLabel(page, "Closing Time");
time.assertEnabled();

// After disabling
time.assertDisabled();
```

## Related Elements

- `DatePickerElement` - For date selection
- `DateTimePickerElement` - Combined date and time picker
