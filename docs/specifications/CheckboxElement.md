# CheckboxElement Specification

## Overview

`CheckboxElement` is a Playwright element wrapper for the `<vaadin-checkbox>` web component. It provides helpers to read and modify checked/indeterminate state and access common mixins for label, helper, validation and enablement.

## Tag Name

```
vaadin-checkbox
```

## Class Hierarchy

```
VaadinElement
    └── CheckboxElement
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
| `FIELD_TAG_NAME` | `"vaadin-checkbox"` | HTML tag name |

## API Methods

### Constructor

```java
CheckboxElement(Locator locator)
```

Creates a new `CheckboxElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get a checkbox by its accessible label.

```java
CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Accept Terms");
```

### Checked State

| Method | Description |
|--------|-------------|
| `isChecked()` | Whether the checkbox is checked |
| `check()` | Check the checkbox |
| `uncheck()` | Uncheck the checkbox |
| `assertChecked()` | Assert that checkbox is checked |
| `assertNotChecked()` | Assert that checkbox is not checked |

### Indeterminate State

| Method | Description |
|--------|-------------|
| `isIndeterminate()` | Whether checkbox is indeterminate |
| `setIndeterminate(boolean)` | Set the indeterminate state |
| `assertIndeterminate()` | Assert indeterminate state |
| `assertNotIndeterminate()` | Assert not indeterminate |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getEnabledLocator()` | Returns input locator for enabled state |
| `getAriaLabelLocator()` | Returns input locator for ARIA label |
| `getFocusLocator()` | Returns input locator for focus |

## Usage Examples

### Basic Usage

```java
CheckboxElement terms = CheckboxElement.getByLabel(page, "I accept the terms");

// Check/uncheck
terms.check();
terms.assertChecked();

terms.uncheck();
terms.assertNotChecked();
```

### Indeterminate State

```java
CheckboxElement selectAll = CheckboxElement.getByLabel(page, "Select All");

// Set indeterminate
selectAll.setIndeterminate(true);
selectAll.assertIndeterminate();

// Clear indeterminate by checking
selectAll.check();
selectAll.assertNotIndeterminate();
```

### Validation

```java
CheckboxElement required = CheckboxElement.getByLabel(page, "Required Field");
required.assertRequired();
required.assertInvalid();
```

## Related Elements

- `RadioButtonElement` - Single selection from group
- `RadioButtonGroupElement` - Group of radio buttons
