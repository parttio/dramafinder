# ButtonElement Specification

## Overview

`ButtonElement` is a Playwright element wrapper for the `<vaadin-button>` web component. It provides lookup helpers based on accessible name or visible text and exposes common focus, aria-label, enablement, prefix/suffix and theming mixins.

## Tag Name

```
vaadin-button
```

## Class Hierarchy

```
VaadinElement
    └── ButtonElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasAriaLabelElement` | ARIA label accessibility |
| `HasEnabledElement` | Enabled/disabled state |
| `HasPrefixElement` | Prefix slot support |
| `HasStyleElement` | Style attribute support |
| `HasSuffixElement` | Suffix slot support |
| `HasThemeElement` | Theme variants support |
| `HasTooltipElement` | Tooltip support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-button"` | HTML tag name |

## API Methods

### Constructor

```java
ButtonElement(Locator locator)
```

Creates a new `ButtonElement` from a Playwright locator.

### Static Factory Methods

#### getByText(Page page, String text)

Get a button by its accessible name or visible text.

```java
ButtonElement btn = ButtonElement.getByText(page, "Save");
```

#### getByText(Page page, Page.GetByRoleOptions options)

Get a button with custom role options.

```java
ButtonElement btn = ButtonElement.getByText(page,
    new Page.GetByRoleOptions().setName("Submit").setExact(true));
```

#### getByText(Locator locator, String text)

Get a button by text within a scope.

```java
ButtonElement btn = ButtonElement.getByText(formLocator, "Cancel");
```

#### getByText(Locator locator, Locator.GetByRoleOptions options)

Get a button with custom options within a scope.

#### getByLabel(Page page, String text)

Alias for `getByText(Page, String)`.

## Usage Examples

### Basic Usage

```java
// Find button by text
ButtonElement saveBtn = ButtonElement.getByText(page, "Save");

// Click the button
saveBtn.click();

// Check enabled state
saveBtn.assertEnabled();
saveBtn.assertDisabled();

// Focus operations
saveBtn.focus();
saveBtn.blur();
```

### With Theme Assertions

```java
ButtonElement primaryBtn = ButtonElement.getByText(page, "Submit");

// Assert theme variant
primaryBtn.assertHasTheme("primary");
primaryBtn.assertHasTheme("error");
```

## Related Elements

- `MenuItemElement` - For menu bar buttons
