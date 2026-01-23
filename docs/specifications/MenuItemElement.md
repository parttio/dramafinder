# MenuItemElement Specification

## Overview

`MenuItemElement` is a Playwright element wrapper for individual menu items `<vaadin-menu-bar-button>`. It represents a clickable item within a menu bar or menu overlay.

## Tag Name

```
vaadin-menu-bar-button
```

## Class Hierarchy

```
VaadinElement
    └── MenuItemElement
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
| `FIELD_TAG_NAME` | `"vaadin-menu-bar-button"` | HTML tag name |

## API Methods

### Constructor

```java
MenuItemElement(Locator locator)
```

Creates a `MenuItemElement` from an existing locator.

### Static Factory Methods

#### getByLabel(Locator locator, String label)

Get a menu item by its accessible label within a scope.

```java
MenuItemElement item = MenuItemElement.getByLabel(menuLocator, "Save");
```

### State Assertions

| Method | Description |
|--------|-------------|
| `assertExpanded()` | Assert that item shows submenu (expanded) |
| `assertCollapsed()` | Assert that item is collapsed |

### Inherited Methods

From `VaadinElement`:
- `click()` - Click the menu item
- `getText()` - Get the item text

## Usage Examples

### Basic Usage

```java
// Get menu item within a menu bar
MenuBarElement menuBar = new MenuBarElement(page);
MenuItemElement fileItem = MenuItemElement.getByLabel(menuBar.getLocator(), "File");

// Click the item
fileItem.click();
```

### Checking Expansion State

```java
MenuBarElement menuBar = new MenuBarElement(page);
MenuItemElement fileItem = menuBar.getMenuItemElement("File");

// Initially collapsed
fileItem.assertCollapsed();

// Click to expand
fileItem.click();
fileItem.assertExpanded();
```

### Within Menu Overlay

```java
MenuBarElement menuBar = new MenuBarElement(page);
MenuElement menu = menuBar.openSubMenu("Edit");

// Get item from overlay
MenuItemElement copyItem = menu.getMenuItemElement("Copy");
copyItem.click();
```

## Related Elements

- `MenuBarElement` - Parent menu bar
- `MenuElement` - Menu overlay containing items
- `ButtonElement` - For standalone buttons
