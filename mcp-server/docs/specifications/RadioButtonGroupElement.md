# RadioButtonGroupElement Specification

## Overview

`RadioButtonGroupElement` is a Playwright element wrapper for the `<vaadin-radio-group>` web component. It provides helpers to select by label/value and assert selected state.

## Tag Name

```
vaadin-radio-group
```

## Class Hierarchy

```
VaadinElement
    └── RadioButtonGroupElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasLabelElement` | Label support |
| `HasEnabledElement` | Enabled/disabled state |
| `HasHelperElement` | Helper text support |
| `HasValidationPropertiesElement` | Validation properties |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-radio-group"` | HTML tag name |

## API Methods

### Constructor

```java
RadioButtonGroupElement(Locator locator)
```

Creates a new `RadioButtonGroupElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get a radio group by its accessible label.

```java
RadioButtonGroupElement gender = RadioButtonGroupElement.getByLabel(page, "Gender");
```

### Selection Methods

| Method | Description |
|--------|-------------|
| `selectByLabel(String label)` | Select radio by its label text |
| `selectByValue(String value)` | Select radio by its value |
| `setValue(String value)` | Set selected value by label |
| `getRadioButtonByLabel(String label)` | Get specific radio button |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(String value)` | Assert selected value by label |

## Usage Examples

### Basic Usage

```java
RadioButtonGroupElement gender = RadioButtonGroupElement.getByLabel(page, "Gender");

// Select by label
gender.selectByLabel("Male");

// Assert selection
gender.assertValue("Male");
```

### Select by Value

```java
RadioButtonGroupElement priority = RadioButtonGroupElement.getByLabel(page, "Priority");

// Select using internal value
priority.selectByValue("high");
```

### Access Individual Radio Buttons

```java
RadioButtonGroupElement options = RadioButtonGroupElement.getByLabel(page, "Options");

// Get specific radio
RadioButtonElement optionA = options.getRadioButtonByLabel("Option A");
optionA.assertChecked();

RadioButtonElement optionB = options.getRadioButtonByLabel("Option B");
optionB.assertNotChecked();

// Check enabled state
optionA.assertEnabled();
optionB.assertDisabled();
```

### Validation

```java
RadioButtonGroupElement required = RadioButtonGroupElement.getByLabel(page, "Selection");

// Assert empty (no selection)
required.assertValue("");

// Check validation state
required.assertRequired();
required.assertInvalid();

// Make selection
required.selectByLabel("Option 1");
required.assertValid();
```

### Helper Text

```java
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Choose");
group.assertHelperText("Select one option");
```

## Related Elements

- `RadioButtonElement` - Individual radio button
- `CheckboxElement` - For multiple selections
- `ListBoxElement` - Alternative single/multi selection
