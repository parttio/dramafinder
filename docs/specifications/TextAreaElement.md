# TextAreaElement Specification

## Overview

`TextAreaElement` is a Playwright element wrapper for the `<vaadin-text-area>` web component. It extends `TextFieldElement` with a textarea input slot and label-based lookup.

## Tag Name

```
vaadin-text-area
```

## Class Hierarchy

```
VaadinElement
    └── TextFieldElement
            └── TextAreaElement
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
| `FIELD_TAG_NAME` | `"vaadin-text-area"` | HTML tag name |

## API Methods

### Constructor

```java
TextAreaElement(Locator locator)
```

Creates a new `TextAreaElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Locates a text area by its label.

```java
TextAreaElement description = TextAreaElement.getByLabel(page, "Description");
```

### Overridden Methods

#### getInputLocator()

Returns the textarea slot locator instead of input.

```java
public Locator getInputLocator() {
    return getLocator().locator("*[slot=\"textarea\"]").first();
}
```

## Usage Examples

### Basic Usage

```java
TextAreaElement description = TextAreaElement.getByLabel(page, "Description");

// Set value
description.setValue("This is a multi-line\ntext description.");

// Assert value
description.assertValue("This is a multi-line\ntext description.");

// Clear
description.clickClearButton();
```

### Length Constraints

```java
TextAreaElement notes = TextAreaElement.getByLabel(page, "Notes");

// Set constraints
notes.setMinLength(10);
notes.setMaxLength(500);

// Assert constraints
notes.assertMinLength(10);
notes.assertMaxLength(500);
```

### Validation

```java
TextAreaElement comments = TextAreaElement.getByLabel(page, "Comments");

// Check required
comments.assertRequired();
comments.assertInvalid();

// Fill and validate
comments.setValue("My comment");
comments.assertValid();
```

### Focus and State

```java
TextAreaElement feedback = TextAreaElement.getByLabel(page, "Feedback");

// Focus
feedback.focus();

// Enabled state
feedback.assertEnabled();

// Placeholder
feedback.assertPlaceholder("Enter your feedback...");
```

### Inherited Methods

All methods from `TextFieldElement` are available:

```java
TextAreaElement bio = TextAreaElement.getByLabel(page, "Biography");

// Pattern validation
bio.setPattern("[A-Za-z\\s]+");

// ARIA label
bio.assertAriaLabel("Biography");

// Tooltip
bio.assertTooltip("Tell us about yourself");
```

## Related Elements

- `TextFieldElement` - Single-line text input
- `EmailFieldElement` - For email input
- `PasswordFieldElement` - For password input
