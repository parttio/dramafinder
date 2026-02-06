# ListBoxElement Specification

## Overview

`ListBoxElement` is a Playwright element wrapper for the `<vaadin-list-box>` web component. It supports single and multiple selection, item-level enablement assertions, and label-based lookup.

## Tag Name

```
vaadin-list-box
```

## Class Hierarchy

```
VaadinElement
    └── ListBoxElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasStyleElement` | Style attribute support |
| `HasTooltipElement` | Tooltip support |
| `HasEnabledElement` | Enabled/disabled state |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-list-box"` | HTML tag name |
| `FIELD_ITEM_TAG_NAME` | `"vaadin-item"` | Item tag name |
| `MULTIPLE_ATTRIBUTE` | `"multiple"` | Multiple selection attribute |

## API Methods

### Constructor

```java
ListBoxElement(Locator locator)
```

Creates a new `ListBoxElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get a list box by its accessible label.

```java
ListBoxElement list = ListBoxElement.getByLabel(page, "Select Option");
```

### Selection Methods

| Method | Description |
|--------|-------------|
| `selectItem(String item)` | Select item by text (toggles in multi mode) |
| `getSingleSelectedValue()` | Get selected value for single-select |
| `getSelectedValue()` | Get all selected values as List |

### Mode Methods

| Method | Description |
|--------|-------------|
| `isMultiple()` | Whether multiple selection is enabled |
| `assertMultiple()` | Assert multiple selection mode |
| `assertSingle()` | Assert single selection mode |

### Assertions

| Method | Description |
|--------|-------------|
| `assertSelectedValue(String...)` | Assert selected values match |
| `assertEnabled()` | Assert list box is enabled |
| `assertDisabled()` | Assert list box is disabled |
| `assertItemEnabled(String item)` | Assert specific item is enabled |
| `assertItemDisabled(String item)` | Assert specific item is disabled |

## Usage Examples

### Single Selection

```java
ListBoxElement list = ListBoxElement.getByLabel(page, "Country");
list.assertSingle();

// Select an item
list.selectItem("France");

// Get selected value
String selected = list.getSingleSelectedValue(); // "France"

// Assert selection
list.assertSelectedValue("France");
```

### Multiple Selection

```java
ListBoxElement list = ListBoxElement.getByLabel(page, "Skills");
list.assertMultiple();

// Select multiple items
list.selectItem("Java");
list.selectItem("Python");
list.selectItem("JavaScript");

// Get all selected
List<String> selected = list.getSelectedValue();

// Assert all selections
list.assertSelectedValue("Java", "Python", "JavaScript");

// Toggle off
list.selectItem("Python");
list.assertSelectedValue("Java", "JavaScript");
```

### Item State

```java
ListBoxElement list = ListBoxElement.getByLabel(page, "Options");

// Check item states
list.assertItemEnabled("Option A");
list.assertItemDisabled("Option B");
```

## Related Elements

- `SelectElement` - Dropdown select component
- `RadioButtonGroupElement` - Single selection radio group
