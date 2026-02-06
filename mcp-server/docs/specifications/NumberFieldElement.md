# NumberFieldElement Specification

## Overview

`NumberFieldElement` is a Playwright element wrapper for the `<vaadin-number-field>` web component. It provides helpers for numeric attributes (`min`, `max`, `step`) using `Double` types and locator utilities to find the component by its accessible label.

## Tag Name

```
vaadin-number-field
```

## Class Hierarchy

```
VaadinElement
    └── AbstractNumberFieldElement
            └── NumberFieldElement
```

## Inherited Interfaces

All interfaces from `AbstractNumberFieldElement`:
- `HasValidationPropertiesElement`
- `HasInputFieldElement`
- `HasPrefixElement`
- `HasSuffixElement`
- `HasClearButtonElement`
- `HasPlaceholderElement`
- `HasAllowedCharPatternElement`
- `HasThemeElement`
- `FocusableElement`
- `HasAriaLabelElement`
- `HasEnabledElement`
- `HasTooltipElement`

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-number-field"` | HTML tag name |

## API Methods

### Constructor

```java
NumberFieldElement(Locator locator)
```

Creates a new `NumberFieldElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a number field by its label using ARIA role `spinbutton`.

```java
NumberFieldElement price = NumberFieldElement.getByLabel(page, "Price");
```

#### getByLabel(Locator locator, String label)

Locates a number field by label within a scope.

### Step Property

| Method | Description |
|--------|-------------|
| `getStep()` | Get step as Double or null |
| `setStep(double step)` | Set the step value |
| `assertStep(Double step)` | Assert step value or null |

### Min Property

| Method | Description |
|--------|-------------|
| `getMin()` | Get minimum as Double or null |
| `setMin(double min)` | Set the minimum value |
| `assertMin(Double min)` | Assert minimum value or null |

### Max Property

| Method | Description |
|--------|-------------|
| `getMax()` | Get maximum as Double or null |
| `setMax(double max)` | Set the maximum value |
| `assertMax(Double max)` | Assert maximum value or null |

## Usage Examples

### Basic Usage

```java
NumberFieldElement price = NumberFieldElement.getByLabel(page, "Price");

// Set value
price.setValue("99.99");

// Assert value
price.assertValue("99.99");
```

### Constraints

```java
NumberFieldElement temperature = NumberFieldElement.getByLabel(page, "Temperature");

// Set constraints
temperature.setMin(-40.0);
temperature.setMax(100.0);
temperature.setStep(0.5);

// Assert constraints
temperature.assertMin(-40.0);
temperature.assertMax(100.0);
temperature.assertStep(0.5);
```

### Step Controls (from AbstractNumberFieldElement)

```java
NumberFieldElement amount = NumberFieldElement.getByLabel(page, "Amount");
amount.assertHasControls(true);

// Use step buttons
amount.clickIncreaseButton();
amount.clickDecreaseButton();
```

### Validation

```java
NumberFieldElement field = NumberFieldElement.getByLabel(page, "Value");
field.assertRequired();
field.assertInvalid();
field.assertErrorMessage("Value is required");
```

## Related Elements

- `IntegerFieldElement` - For integer values
- `BigDecimalFieldElement` - For BigDecimal values
- `AbstractNumberFieldElement` - Base class
