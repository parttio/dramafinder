# SelectElement Specification

## Overview

`SelectElement` is a Playwright element wrapper for the `<vaadin-select>` web component. It provides helpers to open the overlay and pick items by visible text, along with aria/placeholder/validation mixins.

## Tag Name

```
vaadin-select
```

## Class Hierarchy

```
VaadinElement
    └── SelectElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasInputFieldElement` | Input field operations |
| `HasPrefixElement` | Prefix slot support |
| `HasThemeElement` | Theme variants support |
| `HasPlaceholderElement` | Placeholder text support |
| `HasEnabledElement` | Enabled/disabled state |
| `HasTooltipElement` | Tooltip support |
| `HasValidationPropertiesElement` | Validation properties |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-select"` | HTML tag name |
| `FIELD_ITEM_TAG_NAME` | `"vaadin-select-item"` | Item tag name |
| `FIELD_OVERLAY_TAG_NAME` | `"vaadin-select-list-box"` | Overlay tag name |

## API Methods

### Constructor

```java
SelectElement(Locator locator)
```

Creates a new `SelectElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a select by its label.

```java
SelectElement country = SelectElement.getByLabel(page, "Country");
```

### Selection Methods

| Method | Description |
|--------|-------------|
| `selectItem(String item)` | Select item by visible label |
| `getValue()` | Get selected value label |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getInputLocator()` | Returns value slot locator |
| `getAriaLabelLocator()` | Returns screen reader label |
| `getFocusLocator()` | Returns input locator |
| `getEnabledLocator()` | Returns input locator |
| `getLabelLocator()` | Returns label slot |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(String expected)` | Assert selected value label |
| `assertPlaceholder(String placeholder)` | Assert placeholder text |
| `assertAriaLabel(String ariaLabel)` | Assert ARIA label |

## Usage Examples

### Basic Usage

```java
SelectElement country = SelectElement.getByLabel(page, "Country");

// Select an item
country.selectItem("France");

// Get selected value
String selected = country.getValue(); // "France"

// Assert selection
country.assertValue("France");
```

### Placeholder

```java
SelectElement category = SelectElement.getByLabel(page, "Category");

// Assert placeholder
category.assertPlaceholder("Select a category...");

// Make selection
category.selectItem("Electronics");
category.assertValue("Electronics");
```

### Validation

```java
SelectElement required = SelectElement.getByLabel(page, "Type");

// Check required and invalid
required.assertRequired();
required.assertInvalid();

// Make selection
required.selectItem("Premium");
required.assertValid();
```

### Enabled State

```java
SelectElement select = SelectElement.getByLabel(page, "Options");
select.assertEnabled();

// After disabling
select.assertDisabled();
```

## Related Elements

- `ListBoxElement` - List-based selection
- `RadioButtonGroupElement` - Radio-based selection
