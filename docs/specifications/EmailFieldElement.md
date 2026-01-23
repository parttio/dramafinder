# EmailFieldElement Specification

## Overview

`EmailFieldElement` is a Playwright element wrapper for the `<vaadin-email-field>` web component. It extends `TextFieldElement` with email-specific functionality.

## Tag Name

```
vaadin-email-field
```

## Class Hierarchy

```
VaadinElement
    └── TextFieldElement
            └── EmailFieldElement
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
| `FIELD_TAG_NAME` | `"vaadin-email-field"` | HTML tag name |

## API Methods

### Constructor

```java
EmailFieldElement(Locator locator)
```

Creates a new `EmailFieldElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates an email field by its label.

```java
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email Address");
```

#### getByLabel(Locator locator, String label)

Locates an email field by label within a scope.

```java
EmailFieldElement email = EmailFieldElement.getByLabel(formLocator, "Contact Email");
```

## Usage Examples

### Basic Usage

```java
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email");

// Set value
email.setValue("user@example.com");

// Assert value
email.assertValue("user@example.com");

// Clear
email.clickClearButton();
```

### Validation

```java
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email");

// Set invalid email
email.setValue("invalid-email");
email.assertInvalid();

// Set valid email
email.setValue("valid@email.com");
email.assertValid();
```

### Inherited Methods

All methods from `TextFieldElement` are available:

```java
EmailFieldElement email = EmailFieldElement.getByLabel(page, "Email");

// Min/max length
email.setMinLength(5);
email.setMaxLength(100);

// Pattern (overrides default email pattern)
email.setPattern("[a-z]+@company\\.com");

// Focus
email.focus();
email.blur();
```

## Related Elements

- `TextFieldElement` - Base text field
- `PasswordFieldElement` - For password input
