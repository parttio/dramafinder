# MenuBarElement Specification

## Overview

`MenuBarElement` is a Playwright element wrapper for the `<vaadin-menu-bar>` web component. It provides utilities to access menu items and open submenus.

## Tag Name

```
vaadin-menu-bar
```

## Class Hierarchy

```
VaadinElement
    └── MenuBarElement
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
| `FIELD_TAG_NAME` | `"vaadin-menu-bar"` | HTML tag name |

## API Methods

### Constructors

```java
MenuBarElement(Page page)
```

Creates a `MenuBarElement` from the first menu bar on the page.

```java
MenuBarElement(Locator locator)
```

Creates a `MenuBarElement` from an existing locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get a menu bar by its accessible label.

```java
MenuBarElement menuBar = MenuBarElement.getByLabel(page, "Main Menu");
```

### Menu Operations

| Method | Description |
|--------|-------------|
| `getMenuItemElement(String name)` | Get a menu item by visible label |
| `openSubMenu(String name)` | Click item and return submenu overlay |

## Usage Examples

### Basic Usage

```java
// Get menu bar
MenuBarElement menuBar = new MenuBarElement(page);

// Or by label
MenuBarElement mainMenu = MenuBarElement.getByLabel(page, "File Menu");

// Get a menu item
MenuItemElement fileItem = menuBar.getMenuItemElement("File");
fileItem.click();
```

### Opening Submenus

```java
MenuBarElement menuBar = new MenuBarElement(page);

// Open a submenu
MenuElement fileMenu = menuBar.openSubMenu("File");

// Select from submenu
fileMenu.getMenuItemElement("New").click();

// Or open nested submenu
MenuElement recentMenu = fileMenu.openSubMenu("Recent Files");
recentMenu.getMenuItemElement("document.txt").click();
```

### Theme Variants

```java
MenuBarElement menuBar = MenuBarElement.getByLabel(page, "Actions");
menuBar.assertHasTheme("primary");
```

## Related Elements

- `MenuElement` - Menu overlay list
- `MenuItemElement` - Individual menu item
- `ContextMenuElement` - Right-click context menu
