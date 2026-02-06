# DialogElement Specification

## Overview

`DialogElement` is a Playwright element wrapper for the `<vaadin-dialog>` web component. It provides access to header/content/footer slots, modal flags and open state.

## Tag Name

```
vaadin-dialog
```

## Class Hierarchy

```
VaadinElement
    └── DialogElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasThemeElement` | Theme variants support |
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-dialog"` | HTML tag name |

## API Methods

### Constructors

```java
DialogElement(Page page)
```

Creates a `DialogElement` by resolving the dialog with ARIA role on the page.

```java
DialogElement(Locator locator)
```

Creates a `DialogElement` from an existing locator.

### Static Factory Methods

#### getByHeaderText(Page page, String summary)

Get a dialog by its header text (accessible name).

```java
DialogElement dialog = DialogElement.getByHeaderText(page, "Confirm Action");
```

### State Methods

| Method | Description |
|--------|-------------|
| `isOpen()` | Whether the dialog is open (visible) |
| `isModal()` | Whether the dialog is modal (not modeless) |
| `closeWithEscape()` | Close the dialog using Escape key |
| `getHeaderText()` | Get the header text from title slot |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getHeaderLocator()` | Locator for header-content slot |
| `getContentLocator()` | Locator for dialog content (non-slotted) |
| `getFooterLocator()` | Locator for footer slot |

### Assertions

| Method | Description |
|--------|-------------|
| `assertOpen()` | Assert that dialog is open |
| `assertClosed()` | Assert that dialog is closed (hidden) |
| `assertModal()` | Assert that dialog is modal |
| `assertModeless()` | Assert that dialog is modeless |
| `assertHeaderText(String)` | Assert header text matches |

## Usage Examples

### Basic Usage

```java
// Get dialog by ARIA role
DialogElement dialog = new DialogElement(page);
dialog.assertOpen();

// Get dialog by header
DialogElement confirmDialog = DialogElement.getByHeaderText(page, "Confirm Delete");
confirmDialog.assertHeaderText("Confirm Delete");

// Close with Escape
confirmDialog.closeWithEscape();
confirmDialog.assertClosed();
```

### Modal vs Modeless

```java
DialogElement modal = new DialogElement(page);
modal.assertModal();

DialogElement modeless = DialogElement.getByHeaderText(page, "Info");
modeless.assertModeless();
```

### Accessing Content

```java
DialogElement dialog = DialogElement.getByHeaderText(page, "Edit User");

// Access content area
Locator content = dialog.getContentLocator();
content.locator("input[name='email']").fill("user@example.com");

// Access footer for buttons
Locator footer = dialog.getFooterLocator();
footer.locator("vaadin-button").filter(
    new Locator.FilterOptions().setHasText("Save")).click();
```

## Related Elements

- `PopoverElement` - For popover overlays
- `NotificationElement` - For toast notifications
