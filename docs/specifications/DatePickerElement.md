# DatePickerElement Specification

## Overview

`DatePickerElement` is a Playwright element wrapper for the `<vaadin-date-picker>` web component. It adds convenience methods for `LocalDate` values and lookup by label.

## Tag Name

```
vaadin-date-picker
```

## Class Hierarchy

```
VaadinElement
    └── DatePickerElement
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
| `FIELD_TAG_NAME` | `"vaadin-date-picker"` | HTML tag name |

## API Methods

### Constructor

```java
DatePickerElement(Locator locator)
```

Creates a new `DatePickerElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a date picker by its label.

```java
DatePickerElement datePicker = DatePickerElement.getByLabel(page, "Birth Date");
```

#### getByLabel(Locator locator, String label)

Locates a date picker by label within a scope.

### Value Methods

| Method | Description |
|--------|-------------|
| `setValue(LocalDate date)` | Set value using LocalDate (ISO-8601 format) |
| `setValue(String value)` | Set value as string (dd/mm/yyyy format) |
| `getValueAsLocalDate()` | Get value as LocalDate or null |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(String value)` | Assert value as string (dd/mm/yyyy) |
| `assertValue(LocalDate value)` | Assert value as LocalDate |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getAriaLabelLocator()` | Returns input locator |
| `getFocusLocator()` | Returns input locator |
| `getEnabledLocator()` | Returns input locator |

## Usage Examples

### Basic Usage

```java
DatePickerElement birthDate = DatePickerElement.getByLabel(page, "Birth Date");

// Set value with LocalDate
birthDate.setValue(LocalDate.of(1990, 5, 15));

// Get value
LocalDate date = birthDate.getValueAsLocalDate();

// Assert value
birthDate.assertValue(LocalDate.of(1990, 5, 15));
```

### String Format

```java
DatePickerElement startDate = DatePickerElement.getByLabel(page, "Start Date");

// Set value as formatted string
startDate.setValue("15/05/2023");
startDate.assertValue("15/05/2023");
```

### Clear and Validation

```java
DatePickerElement date = DatePickerElement.getByLabel(page, "Date");
date.clickClearButton();
date.assertValue((LocalDate) null);

date.assertRequired();
date.assertInvalid();
```

## Related Elements

- `TimePickerElement` - For time selection
- `DateTimePickerElement` - Combined date and time picker
