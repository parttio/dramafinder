# TextFieldElement Specification

## Overview

`TextFieldElement` is a Playwright element wrapper for the `<vaadin-text-field>` web component. It provides a comprehensive API for interacting with and testing Vaadin text field components.

## Tag Name

```
vaadin-text-field
```

## Class Hierarchy

```
VaadinElement
    └── TextFieldElement
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
| `FIELD_TAG_NAME` | `"vaadin-text-field"` | HTML tag name |
| `MAXLENGTH_ATTRIBUTE` | `"maxlength"` | Maximum length attribute |
| `PATTERN_ATTRIBUTE` | `"pattern"` | Validation pattern attribute |
| `MIN_LENGTH_ATTRIBUTE` | `"minLength"` | Minimum length attribute |

## API Methods

### Constructor

```java
TextFieldElement(Locator locator)
```

Creates a new `TextFieldElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a text field by its label on a page.

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "Username");
```

#### getByLabel(Locator locator, String label)

Locates a text field by its label within a specific locator context.

```java
TextFieldElement field = TextFieldElement.getByLabel(formLocator, "Email");
```

### Property Methods

#### Minimum Length

| Method | Description |
|--------|-------------|
| `getMinLength()` | Returns the minimum length or `null` if not set |
| `setMinLength(int min)` | Sets the minimum length |
| `assertMinLength(Integer min)` | Asserts the minimum length value |

#### Maximum Length

| Method | Description |
|--------|-------------|
| `getMaxLength()` | Returns the maximum length or `null` if not set |
| `setMaxLength(int max)` | Sets the maximum length |
| `assertMaxLength(Integer max)` | Asserts the maximum length value |

#### Pattern

| Method | Description |
|--------|-------------|
| `getPattern()` | Returns the validation pattern or `null` if not set |
| `setPattern(String pattern)` | Sets the validation pattern |
| `assertPattern(String pattern)` | Asserts the pattern value |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getFocusLocator()` | Returns the locator for focus operations |
| `getAriaLabelLocator()` | Returns the locator for ARIA label |
| `getEnabledLocator()` | Returns the locator for enabled state |

## Usage Examples

### Basic Usage

```java
// Get text field by label
TextFieldElement username = TextFieldElement.getByLabel(page, "Username");

// Set value (inherited from HasInputFieldElement)
username.setValue("john.doe");

// Assert value
username.assertValue("john.doe");
```

### Validation

```java
TextFieldElement email = TextFieldElement.getByLabel(page, "Email");

// Set validation constraints
email.setMinLength(5);
email.setMaxLength(50);
email.setPattern("[a-z]+@[a-z]+\\.[a-z]+");

// Assert constraints
email.assertMinLength(5);
email.assertMaxLength(50);
email.assertPattern("[a-z]+@[a-z]+\\.[a-z]+");
```

### Focus and State

```java
TextFieldElement field = TextFieldElement.getByLabel(page, "Name");

// Focus the field
field.focus();

// Check enabled state
field.assertEnabled();
field.assertDisabled();
```

## Related Elements

- `EmailFieldElement` - Specialized for email input
- `PasswordFieldElement` - Specialized for password input
- `TextAreaElement` - Multi-line text input
- `NumberFieldElement` - Numeric input
