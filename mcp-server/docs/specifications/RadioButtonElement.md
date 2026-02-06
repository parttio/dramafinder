# RadioButtonElement Specification

## Overview

`RadioButtonElement` is a Playwright element wrapper for the `<vaadin-radio-button>` web component. It is package-private and intended for internal use by `RadioButtonGroupElement`.

## Tag Name

```
vaadin-radio-button
```

## Class Hierarchy

```
VaadinElement
    └── RadioButtonElement (package-private)
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasEnabledElement` | Enabled/disabled state |
| `HasHelperElement` | Helper text support |
| `HasValueElement` | Value operations |
| `HasStyleElement` | Style attribute support |
| `HasLabelElement` | Label support |
| `HasValidationPropertiesElement` | Validation properties |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-radio-button"` | HTML tag name |

## API Methods

### Constructor

```java
RadioButtonElement(Locator locator)
```

Creates a new `RadioButtonElement` from a Playwright locator.

### Static Factory Methods (package-private)

#### getByLabel(Locator locator, String label)

Get a radio button by its label within a scope.

### Checked State (package-private)

| Method | Description |
|--------|-------------|
| `isChecked()` | Whether the radio is checked |
| `check()` | Check the radio button |
| `assertChecked()` | Assert that radio is checked |
| `assertNotChecked()` | Assert that radio is not checked |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getEnabledLocator()` | Returns input locator |
| `getAriaLabelLocator()` | Returns input locator |
| `getFocusLocator()` | Returns input locator |

## Usage Examples

Radio buttons should be used through `RadioButtonGroupElement`:

```java
RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Gender");

// Select by label
group.selectByLabel("Male");

// Get specific radio button
RadioButtonElement maleRadio = group.getRadioButtonByLabel("Male");
maleRadio.assertChecked();

RadioButtonElement femaleRadio = group.getRadioButtonByLabel("Female");
femaleRadio.assertNotChecked();
```

## Related Elements

- `RadioButtonGroupElement` - Parent group container
- `CheckboxElement` - For multiple selections
