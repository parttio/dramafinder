# SideNavigationElement Specification

## Overview

`SideNavigationElement` is a Playwright element wrapper for the `<vaadin-side-nav>` web component. It provides methods to navigate and interact with sidebar navigation menus.

## Tag Name

```
vaadin-side-nav
```

## Class Hierarchy

```
VaadinElement
    └── SideNavigationElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasLabelElement` | Label support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-side-nav"` | HTML tag name |

## API Methods

### Constructor

```java
SideNavigationElement(Locator locator)
```

Creates a new `SideNavigationElement` from a Playwright locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get side navigation by its accessible label.

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Main Navigation");
```

### Navigation Methods

| Method | Description |
|--------|-------------|
| `getItem(String label)` | Get navigation item by label text |
| `clickItem(String label)` | Click a navigation item |
| `toggle()` | Toggle the collapsed state via label click |

### State Methods

| Method | Description |
|--------|-------------|
| `isCollapsed()` | Whether the nav is collapsed |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getLabelLocator()` | Locator for the label slot |

### Assertions

| Method | Description |
|--------|-------------|
| `assertCollapsed()` | Assert nav is collapsed |
| `assertExpanded()` | Assert nav is expanded |
| `assertCollapsible()` | Assert nav can be collapsed |
| `assertNotCollapsible()` | Assert nav cannot be collapsed |

## Usage Examples

### Basic Usage

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Main Menu");

// Click a navigation item
nav.clickItem("Dashboard");

// Get specific item
SideNavigationItemElement settings = nav.getItem("Settings");
settings.assertCurrent();
```

### Collapsed State

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Menu");

// Check collapsibility
nav.assertCollapsible();

// Expand if collapsed
if (nav.isCollapsed()) {
    nav.toggle();
}
nav.assertExpanded();

// Collapse
nav.toggle();
nav.assertCollapsed();
```

### Nested Navigation

```java
SideNavigationElement nav = SideNavigationElement.getByLabel(page, "Admin");

// Navigate to nested item
// First expand parent
SideNavigationItemElement users = nav.getItem("Users");
users.toggle(); // Expand
users.assertExpanded();

// Click nested item
nav.clickItem("User List");
```

## Related Elements

- `SideNavigationItemElement` - Individual navigation item
- `TabSheetElement` - Tab-based navigation
