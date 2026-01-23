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
| `assertContent(String content)` | Assert notification contains exact text |

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
notification.assertHasTheme("success");

// Check for error theme
notification.assertHasTheme("error");

// Check for warning theme
notification.assertHasTheme("warning");
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
// Get specific notification by content
Locator notifications = page.locator("vaadin-notification-card");
NotificationElement errorNotification = new NotificationElement(
    notifications.filter(new Locator.FilterOptions().setHasText("Error"))
);
errorNotification.assertOpen();
```

## Related Elements

- `DialogElement` - For modal dialogs
- `PopoverElement` - For popover overlays
