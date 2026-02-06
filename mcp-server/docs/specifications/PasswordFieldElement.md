# PasswordFieldElement Specification

## Overview

`PasswordFieldElement` is a Playwright element wrapper for the `<vaadin-password-field>` web component. It extends `TextFieldElement` with password-specific functionality including masked input.

## Tag Name

```
vaadin-password-field
```

## Class Hierarchy

```
VaadinElement
    └── TextFieldElement
            └── PasswordFieldElement
```

## Inherited Interfaces

All interfaces from `TextFieldElement`:
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
| `FIELD_TAG_NAME` | `"vaadin-password-field"` | HTML tag name |

## API Methods

### Constructor

```java
PasswordFieldElement(Locator locator)
```

Creates a new `PasswordFieldElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a password field by its label.

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "Password");
```

## Usage Examples

### Basic Usage

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "Password");

// Set value
password.setValue("secretPassword123");

// Assert value
password.assertValue("secretPassword123");

// Clear
password.clickClearButton();
```

### Validation

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "Password");

// Set min length
password.setMinLength(8);
password.assertMinLength(8);

// Check validation
password.setValue("short");
password.assertInvalid();

password.setValue("validPassword123");
password.assertValid();
```

### Pattern Validation

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "Password");

// Require at least one digit and one uppercase
password.setPattern("(?=.*\\d)(?=.*[A-Z]).+");

password.setValue("nodigits");
password.assertInvalid();

password.setValue("Password1");
password.assertValid();
```

### Inherited Methods

All methods from `TextFieldElement` are available:

```java
PasswordFieldElement password = PasswordFieldElement.getByLabel(page, "Password");

// Focus operations
password.focus();
password.blur();

// Enabled state
password.assertEnabled();

// Placeholder
password.assertPlaceholder("Enter your password");
```

## Related Elements

- `TextFieldElement` - Base text field
- `EmailFieldElement` - For email input
