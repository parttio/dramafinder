# NotificationElement Specification

## Overview

`NotificationElement` is a Playwright element wrapper for notification cards `<vaadin-notification-card>`. It provides helpers to check visibility and content of toast notifications.

## Tag Name

```
vaadin-notification-card
```

## Class Hierarchy

```
VaadinElement
    └── NotificationElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasThemeElement` | Theme variants support |
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-notification-card"` | HTML tag name |

## API Methods

### Constructors

```java
NotificationElement(Page page)
```

Creates a `NotificationElement` from the first notification card on the page.

```java
NotificationElement(Locator locator)
```

Creates a `NotificationElement` from an existing locator.

The constructor scopes its locator to open cards only (`vaadin-notification-card[slot]`), so closed and never-opened cards are excluded. It targets the single open notification; when several are open at once, use `getByText` to disambiguate.

### Factory Methods

| Method | Description |
|--------|-------------|
| `getByText(Page page, String text)` | Get an open notification by (a substring of) its text |

### State Methods

| Method | Description |
|--------|-------------|
| `isOpen()` | Whether the notification is visible |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getContentLocator()` | Locator for the notification content |

### Assertions

| Method | Description |
|--------|-------------|
| `assertOpen()` | Assert that notification is visible |
| `assertClosed()` | Assert that notification is hidden |
| `assertContent(String content)` | Assert notification contains the given text (substring) |

## Usage Examples

### Basic Usage

```java
// Trigger an action that shows notification
page.locator("vaadin-button").filter(
    new Locator.FilterOptions().setHasText("Save")).click();

// Check notification
NotificationElement notification = new NotificationElement(page);
notification.assertOpen();
notification.assertContent("Changes saved successfully");
```

### Theme Variants

```java
NotificationElement notification = new NotificationElement(page);

// Check for success theme
notification.assertTheme("success");

// Check for error theme
notification.assertTheme("error");

// Check for warning theme
notification.assertTheme("warning");
```

### Waiting for Notification to Close

```java
NotificationElement notification = new NotificationElement(page);
notification.assertOpen();

// Wait for auto-close
notification.assertClosed();
```

### Multiple Notifications

```java
// Get a specific open notification by its text
NotificationElement errorNotification = NotificationElement.getByText(page, "Error");
errorNotification.assertOpen();
errorNotification.assertContent("Error");
```

## Related Elements

- `DialogElement` - For modal dialogs
- `PopoverElement` - For popover overlays
