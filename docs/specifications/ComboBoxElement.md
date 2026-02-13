# ComboBoxElement Specification

## Overview

`ComboBoxElement` is a Playwright element wrapper for the `<vaadin-combo-box>` web component. It provides helpers to open the overlay, filter items, and pick items by visible text, along with aria/placeholder/validation mixins. It supports both in-memory and lazy-loaded data providers.

## Tag Name

```
vaadin-combo-box
```

## Class Hierarchy

```
VaadinElement
    └── ComboBoxElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasInputFieldElement` | Input field operations (value, helper, label, style) |
| `HasPrefixElement` | Prefix slot support |
| `HasThemeElement` | Theme variants support |
| `HasPlaceholderElement` | Placeholder text support |
| `HasEnabledElement` | Enabled/disabled state |
| `HasTooltipElement` | Tooltip support |
| `HasValidationPropertiesElement` | Validation properties |
| `HasClearButtonElement` | Clear button functionality |
| `HasAllowedCharPatternElement` | Character pattern restrictions |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-combo-box"` | HTML tag name |
| `FIELD_ITEM_TAG_NAME` | `"vaadin-combo-box-item"` | Item tag name |

## API Methods

### Constructor

```java
ComboBoxElement(Locator locator)
```

Creates a new `ComboBoxElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a combo box by its accessible label on a page. Uses `AriaRole.COMBOBOX` for matching.

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Country");
```

#### getByLabel(Locator locator, String label)

Locates a combo box by its label within a specific locator scope.

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(formLocator, "Country");
```

### Selection Methods

| Method | Description |
|--------|-------------|
| `selectItem(String item)` | Open the overlay and click the matching item by visible label |
| `filterAndSelectItem(String filter, String item)` | Type filter text into the input, then click the matching item |

### Filter Methods

| Method | Description |
|--------|-------------|
| `setFilter(String filter)` | Open the overlay and type into the input to trigger filtering |
| `getFilter()` | Get the current filter value from the DOM property |

### Overlay Methods

| Method | Description |
|--------|-------------|
| `open()` | Open the combo box overlay |
| `close()` | Close the combo box overlay |
| `isOpened()` | Whether the overlay is currently open |
| `clickToggleButton()` | Click the dropdown toggle button |
| `getOverlayItemCount()` | Count visible overlay items |

### Value Methods

| Method | Description |
|--------|-------------|
| `getValue()` | Get the displayed value from the input element |
| `assertValue(String expected)` | Assert the displayed value equals the expected string |

### Read-Only Methods

| Method | Description |
|--------|-------------|
| `isReadOnly()` | Whether the combo box is read-only |
| `assertReadOnly()` | Assert the combo box is read-only |
| `assertNotReadOnly()` | Assert the combo box is not read-only |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getFocusLocator()` | Returns the input locator |
| `getAriaLabelLocator()` | Returns the input locator |
| `getEnabledLocator()` | Returns the input locator |
| `getToggleButtonLocator()` | Returns the toggle button part locator |

### Assertions

| Method | Description |
|--------|-------------|
| `assertValue(String expected)` | Assert the displayed value |
| `assertOpened()` | Assert the overlay is open |
| `assertClosed()` | Assert the overlay is closed |
| `assertReadOnly()` | Assert read-only state |
| `assertNotReadOnly()` | Assert not read-only state |
| `assertItemCount(int expected)` | Assert the overlay contains exactly the expected number of visible items |

## Usage Examples

### Basic Usage

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");

// Select an item
comboBox.selectItem("Rating: high to low");

// Get selected value
String selected = comboBox.getValue(); // "Rating: high to low"

// Assert selection
comboBox.assertValue("Rating: high to low");
```

### Filtering and Selection

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Fruit");

// Filter and select in one step
comboBox.filterAndSelectItem("Apr", "Apricot");
comboBox.assertValue("Apricot");

// Or filter manually
comboBox.setFilter("Ban");
comboBox.assertItemCount(1);
comboBox.close();
```

### Lazy Loading

ComboBox supports lazy data providers for large datasets. The element API works the same way regardless of the data loading strategy.

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Lazy ComboBox");

// Filter narrows results from the server
comboBox.filterAndSelectItem("Item 250", "Item 250");
comboBox.assertValue("Item 250");

// Verify filtered item count
comboBox.setFilter("Item 500");
comboBox.assertItemCount(1);
comboBox.close();
```

### Overlay State

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Country");

// Assert initial state
comboBox.assertClosed();

// Open and verify
comboBox.open();
comboBox.assertOpened();

// Close and verify
comboBox.close();
comboBox.assertClosed();
```

### Clear Button

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Category");

comboBox.selectItem("Electronics");
comboBox.assertValue("Electronics");

comboBox.clickClearButton();
comboBox.assertValue("");
```

### Read-Only

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Read-only ComboBox");

comboBox.assertReadOnly();
comboBox.assertValue("Banana");
```

### Validation

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Required Field");

comboBox.assertValid();

// Trigger validation
ButtonElement.getByText(page, "Validate").click();
comboBox.assertInvalid();
```

### Prefix and Helper

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");

// Prefix
comboBox.assertPrefixHasText("Prefix");

// Helper text
comboBox.assertHelperHasText("Helper text");
```

### Focus and Accessibility

```java
ComboBoxElement comboBox = ComboBoxElement.getByLabel(page, "Sort by");

// Focus
comboBox.focus();
comboBox.assertIsFocused();

// ARIA label
ComboBoxElement ariaCombo = ComboBoxElement.getByLabel(page, "Invisible label");
ariaCombo.assertAriaLabel("Invisible label");
```

## Related Elements

- `SelectElement` - Simpler overlay selection without filtering or lazy loading
- `ListBoxElement` - Scrollable inline list of options
- `RadioButtonGroupElement` - Radio-based selection with all options visible
