# PopoverElement Specification

## Overview

`PopoverElement` is a Playwright element wrapper for the `<vaadin-popover>` web component. It provides helpers to check visibility and access content of popover overlays.

## Tag Name

```
vaadin-popover
```

## Class Hierarchy

```
VaadinElement
    └── PopoverElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasThemeElement` | Theme variants support |
| `HasStyleElement` | Style attribute support |
| `HasAriaLabelElement` | ARIA label accessibility |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-popover"` | HTML tag name |

## API Methods

### Constructors

```java
PopoverElement(Page page)
```

Creates a `PopoverElement` by resolving the popover with ARIA dialog role.

```java
PopoverElement(Locator locator)
```

Creates a `PopoverElement` from an existing locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get a popover by its accessible label.

```java
PopoverElement popover = PopoverElement.getByLabel(page, "Help Info");
```

### State Methods

| Method | Description |
|--------|-------------|
| `isOpen()` | Whether the popover is visible |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getContentLocator()` | Locator for the popover content |

### Assertions

| Method | Description |
|--------|-------------|
| `assertOpen()` | Assert that popover is open |
| `assertClosed()` | Assert that popover is hidden |

## Usage Examples

### Basic Usage

```java
// Click trigger element
page.locator(".help-icon").click();

// Check popover
PopoverElement popover = new PopoverElement(page);
popover.assertOpen();

// Access content
Locator content = popover.getContentLocator();
assertThat(content).containsText("Help information");
```

### By Label

```java
// Get specific popover by accessible name
PopoverElement helpPopover = PopoverElement.getByLabel(page, "Field Help");
helpPopover.assertOpen();
```

### Theme Variants

```java
PopoverElement popover = new PopoverElement(page);
popover.assertHasTheme("arrow");
```

### Interacting with Content

```java
PopoverElement popover = new PopoverElement(page);
popover.assertOpen();

// Click a button inside the popover
Locator content = popover.getContentLocator();
content.locator("vaadin-button").click();

// Popover may close after action
popover.assertClosed();
```

## Related Elements

- `DialogElement` - For modal dialogs
- `NotificationElement` - For toast notifications
- `ContextMenuElement` - For context menus
