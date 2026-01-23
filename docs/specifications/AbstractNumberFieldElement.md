# AbstractNumberFieldElement Specification

## Overview

`AbstractNumberFieldElement` is an abstract base class for Vaadin number-like fields. It provides shared behavior for numeric inputs, including access to step controls (increase/decrease buttons) and common mixins for validation, input handling, theming, accessibility, and focus.

## Class Hierarchy

```
VaadinElement
    └── AbstractNumberFieldElement (abstract)
            ├── IntegerFieldElement
            └── NumberFieldElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasValidationPropertiesElement` | Validation properties support |
| `HasInputFieldElement` | Input field operations |
| `HasPrefixElement` | Prefix slot support |
| `HasSuffixElement` | Suffix slot support |
| `HasClearButtonElement` | Clear button functionality |
| `HasPlaceholderElement` | Placeholder text support |
| `HasAllowedCharPatternElement` | Character pattern restrictions |
| `HasThemeElement` | Theme variants support |
| `FocusableElement` | Focus operations |
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasEnabledElement` | Enabled/disabled state |
| `HasTooltipElement` | Tooltip support |

## API Methods

### Constructor

```java
AbstractNumberFieldElement(Locator locator)
```

Creates a new `AbstractNumberFieldElement` from a Playwright locator.

### Step Controls

| Method | Description |
|--------|-------------|
| `getHasControls()` | Returns `true` if step buttons are visible |
| `assertHasControls(boolean hasControls)` | Asserts step buttons visibility |
| `clickIncreaseButton()` | Clicks the increase (+) button |
| `clickDecreaseButton()` | Clicks the decrease (-) button |

## Usage Examples

### Using Step Controls

```java
IntegerFieldElement quantity = IntegerFieldElement.getByLabel(page, "Quantity");

// Check if controls are visible
quantity.assertHasControls(true);

// Use increase/decrease buttons
quantity.clickIncreaseButton();
quantity.clickDecreaseButton();
```

## Subclasses

- `IntegerFieldElement` - For integer values with `Integer` type helpers
- `NumberFieldElement` - For decimal values with `Double` type helpers
