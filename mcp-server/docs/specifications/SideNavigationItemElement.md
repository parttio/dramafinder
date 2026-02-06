# SideNavigationItemElement Specification

## Overview

`SideNavigationItemElement` is a Playwright element wrapper for the `<vaadin-side-nav-item>` web component. It represents individual items within a side navigation menu.

## Tag Name

```
vaadin-side-nav-item
```

## Class Hierarchy

```
VaadinElement
    └── SideNavigationItemElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasEnabledElement` | Enabled/disabled state |
| `HasPrefixElement` | Prefix slot support |
| `HasSuffixElement` | Suffix slot support |
| `HasLabelElement` | Label support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-side-nav-item"` | HTML tag name |

## API Methods

### Constructor

```java
SideNavigationItemElement(Locator locator)
```

Creates a new `SideNavigationItemElement` from a Playwright locator.

### State Methods

| Method | Description |
|--------|-------------|
| `isExpanded()` | Whether the item is expanded (has children) |
| `toggle()` | Toggle the expansion state |
| `navigate()` | Click the link to navigate |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getLabelLocator()` | Returns the item locator |

### Assertions

| Method | Description |
|--------|-------------|
| `assertExpanded()` | Assert item is expanded |
| `assertCollapsed()` | Assert item is collapsed |
| `assertEnabled()` | Assert item is enabled |
| `assertDisabled()` | Assert item is disabled |
| `assertCurrent()` | Assert item is current route |
| `assertNotCurrent()` | Assert item is not current |

## Usage Examples

### Basic Usage

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Menu");
SideNavigationItemElement dashboard = nav.getItem("Dashboard");

// Navigate
dashboard.navigate();

// Check if current
dashboard.assertCurrent();
```

### Expandable Items with Children

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Menu");
SideNavigationItemElement admin = nav.getItem("Administration");

// Check and toggle expansion
admin.assertCollapsed();
admin.toggle();
admin.assertExpanded();

// Navigate to child (now visible)
SideNavigationItemElement users = nav.getItem("Users");
users.navigate();
```

### Disabled Items

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Menu");
SideNavigationItemElement restricted = nav.getItem("Restricted Area");

// Check disabled state
restricted.assertDisabled();
```

### Current Route

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Menu");

// Navigate to dashboard
SideNavigationItemElement dashboard = nav.getItem("Dashboard");
dashboard.navigate();
dashboard.assertCurrent();

// Navigate to settings
SideNavigationItemElement settings = nav.getItem("Settings");
settings.navigate();
settings.assertCurrent();
dashboard.assertNotCurrent();
```

### Prefix/Suffix Icons

```java
SideNavigationItemElement item = nav.getItem("Settings");

// Access prefix (icon)
Locator prefix = item.getPrefixLocator();
assertThat(prefix).isVisible();

// Access suffix (badge)
Locator suffix = item.getSuffixLocator();
assertThat(suffix).hasText("3");
```

## Related Elements

- `SideNavigationElement` - Parent navigation container
- `TabElement` - For tab-based navigation
