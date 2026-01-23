# BigDecimalFieldElement Specification

## Overview

`BigDecimalFieldElement` is a Playwright element wrapper for the `<vaadin-big-decimal-field>` web component.

## Tag Name

```
vaadin-big-decimal-field
```

## Class Hierarchy

```
VaadinElement
    └── BigDecimalFieldElement
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

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-big-decimal-field"` | HTML tag name |

## API Methods

### Constructor

```java
BigDecimalFieldElement(Locator locator)
```

Creates a new `BigDecimalFieldElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a big decimal field by its label on a page.

```java
BigDecimalFieldElement field = BigDecimalFieldElement.getByLabel(page, "Price");
```

#### getByLabel(Locator locator, String label)

Locates a big decimal field by its label within a specific locator context.

```java
BigDecimalFieldElement field = BigDecimalFieldElement.getByLabel(formLocator, "Amount");
```

## Usage Examples

### Basic Usage

```java
BigDecimalFieldElement price = BigDecimalFieldElement.getByLabel(page, "Price");

// Set value (inherited from HasInputFieldElement)
price.setValue("123.45");

// Assert value
price.assertValue("123.45");

// Clear field
price.clickClearButton();
```

## Related Elements

- `NumberFieldElement` - For double precision numbers
- `IntegerFieldElement` - For integer numbers
