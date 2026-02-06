# MenuElement Specification

## Overview

`MenuElement` is a Playwright element wrapper for the menu overlay list `<vaadin-menu-bar-list-box>`. It represents the dropdown menu that appears when a menu bar item is clicked.

## Tag Name

```
vaadin-menu-bar-list-box
```

## Class Hierarchy

```
VaadinElement
    └── MenuElement
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
| `FIELD_TAG_NAME` | `"vaadin-menu-bar-list-box"` | HTML tag name |

## API Methods

### Constructors

```java
MenuElement(Page page)
```

Creates a `MenuElement` from the first menu list box on the page.

```java
MenuElement(Locator locator)
```

Creates a `MenuElement` from an existing locator.

### Static Factory Methods

#### getByLabel(Page page, String label)

Get a menu overlay by its accessible label.

```java
MenuElement menu = MenuElement.getByLabel(page, "File Menu");
```

### Menu Operations

| Method | Description |
|--------|-------------|
| `getMenuItemElement(String name)` | Get a menu item by visible label |
| `openSubMenu(String name)` | Click item and return next submenu overlay |

## Usage Examples

### Basic Usage

```java
// After opening menu bar item
MenuBarElement menuBar = new MenuBarElement(page);
MenuElement fileMenu = menuBar.openSubMenu("File");

// Get menu item
MenuItemElement newItem = fileMenu.getMenuItemElement("New");
newItem.click();
```

### Nested Submenus

```java
MenuBarElement menuBar = new MenuBarElement(page);

// Open first level
MenuElement editMenu = menuBar.openSubMenu("Edit");

// Open nested submenu
MenuElement formatMenu = editMenu.openSubMenu("Format");

// Select from nested menu
formatMenu.getMenuItemElement("Bold").click();
```

### By Label

```java
// Get menu directly by accessible name
MenuElement menu = MenuElement.getByLabel(page, "Insert Menu");
menu.getMenuItemElement("Table").click();
```

## Related Elements

- `MenuBarElement` - Parent menu bar
- `MenuItemElement` - Individual menu item
- `ContextMenuElement` - Right-click context menu
