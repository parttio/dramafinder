# ContextMenuElement Specification

## Overview

`ContextMenuElement` is a Playwright element wrapper for context menu overlays `<vaadin-context-menu-overlay>`. It provides helpers to open a menu via a context-click on the target, inspect the overlay list box, and pick menu items by their accessible label using the `menu` role.

## Tag Name

```
vaadin-context-menu
```

## Class Hierarchy

```
VaadinElement
    └── ContextMenuElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-context-menu"` | HTML tag name |
| `FIELD_LIST_BOX_TAG_NAME` | `"vaadin-context-menu-list-box"` | List box tag name |

## API Methods

### Constructors

```java
ContextMenuElement(Page page)
```

Creates a `ContextMenuElement` from the first opened context menu on the page.

```java
ContextMenuElement(Locator locator)
```

Creates a `ContextMenuElement` from an existing locator.

### Static Methods

#### openOn(Locator target)

Open the context menu by right-clicking on the provided target.

```java
ContextMenuElement.openOn(page.locator(".my-element"));
```

### Menu Operations

| Method | Description |
|--------|-------------|
| `selectItem(String itemLabel)` | Select a menu item by label |
| `openSubMenu(String itemLabel)` | Open a submenu and return its overlay |
| `getListBoxLocator()` | Locator for the menu list box |

### Assertions

| Method | Description |
|--------|-------------|
| `assertOpen()` | Assert that menu is open |
| `assertClosed()` | Assert that menu is closed |
| `assertItemEnabled(String itemLabel)` | Assert menu item is enabled |
| `assertItemDisabled(String itemLabel)` | Assert menu item is disabled |
| `assertItemChecked(String itemLabel)` | Assert checkable item is checked |
| `assertItemNotChecked(String itemLabel)` | Assert checkable item is not checked |

## Usage Examples

### Basic Usage

```java
// Right-click to open context menu
ContextMenuElement.openOn(page.locator(".target-element"));

// Get the context menu
ContextMenuElement menu = new ContextMenuElement(page);
menu.assertOpen();

// Select an item
menu.selectItem("Copy");
```

### Submenus

```java
ContextMenuElement.openOn(page.locator(".target"));
ContextMenuElement menu = new ContextMenuElement(page);

// Open a submenu
ContextMenuElement submenu = menu.openSubMenu("More Options");
submenu.selectItem("Advanced Settings");
```

### Checkable Items

```java
ContextMenuElement menu = new ContextMenuElement(page);
menu.assertItemChecked("Show Grid");
menu.selectItem("Show Grid"); // Toggle
menu.assertItemNotChecked("Show Grid");
```

## Related Elements

- `MenuBarElement` - For menu bar navigation
- `MenuElement` - For menu bar dropdowns
