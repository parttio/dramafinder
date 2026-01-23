# IntegerFieldElement Specification

## Overview

`IntegerFieldElement` is a Playwright element wrapper for the `<vaadin-integer-field>` web component. It provides typed helpers to read and modify integer-specific attributes such as `min`, `max`, and `step`, and convenient factory methods to locate the element by its accessible label.

## Tag Name

```
vaadin-integer-field
```

## Class Hierarchy

```
VaadinElement
    └── AbstractNumberFieldElement
            └── IntegerFieldElement
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
| `FIELD_TAG_NAME` | `"vaadin-integer-field"` | HTML tag name |

## API Methods

### Constructor

```java
IntegerFieldElement(Locator locator)
```

Creates a new `IntegerFieldElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates an integer field by its label using ARIA role `spinbutton`.

```java
IntegerFieldElement quantity = IntegerFieldElement.getByLabel(page, "Quantity");
```

#### getByLabel(Locator locator, String label)

Locates an integer field by label within a scope.

### Step Property

| Method | Description |
|--------|-------------|
| `getStep()` | Get step as Integer or null |
| `setStep(int step)` | Set the step value |
| `assertStep(Integer step)` | Assert step value or null |

### Min Property

| Method | Description |
|--------|-------------|
| `getMin()` | Get minimum as Integer or null |
| `setMin(int min)` | Set the minimum value |
| `assertMin(Integer min)` | Assert minimum value or null |

### Max Property

| Method | Description |
|--------|-------------|
| `getMax()` | Get maximum as Integer or null |
| `setMax(int max)` | Set the maximum value |
| `assertMax(Integer max)` | Assert maximum value or null |

## Usage Examples

### Basic Usage

```java
IntegerFieldElement quantity = IntegerFieldElement.getByLabel(page, "Quantity");

// Set value
quantity.setValue("10");

// Assert value
quantity.assertValue("10");
```

### Constraints

```java
IntegerFieldElement age = IntegerFieldElement.getByLabel(page, "Age");

// Set constraints
age.setMin(0);
age.setMax(120);
age.setStep(1);

// Assert constraints
age.assertMin(0);
age.assertMax(120);
age.assertStep(1);
```

### Step Controls (from AbstractNumberFieldElement)

```java
IntegerFieldElement counter = IntegerFieldElement.getByLabel(page, "Counter");
counter.assertHasControls(true);

// Use step buttons
counter.clickIncreaseButton();
counter.clickDecreaseButton();
```

## Related Elements

- `NumberFieldElement` - For decimal (double) values
- `BigDecimalFieldElement` - For BigDecimal values
- `AbstractNumberFieldElement` - Base class
