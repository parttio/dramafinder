# TabElement Specification

## Overview

`TabElement` is a Playwright element wrapper for tabs `<vaadin-tab>`. It represents individual tab buttons within a tab container.

## Tag Name

```
vaadin-tab
```

## Class Hierarchy

```
VaadinElement
    └── TabElement
```

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-tab"` | HTML tag name |
| `FIELD_PARENT_TAG_NAME` | `"vaadin-tabs"` | Parent tabs container |

## API Methods

### Constructor

```java
TabElement(Locator locator)
```

Creates a new `TabElement` from a Playwright locator.

### Static Factory Methods

#### getTabByText(Locator locator, String summary)

Get a tab by visible text within a scope.

```java
TabElement tab = TabElement.getTabByText(containerLocator, "Details");
```

#### getSelectedTab(Locator locator)

Get the currently selected tab within a scope.

```java
TabElement selected = TabElement.getSelectedTab(containerLocator);
```

### State Methods

| Method | Description |
|--------|-------------|
| `isSelected()` | Whether the tab is selected |
| `select()` | Select the tab by clicking |
| `getLabel()` | Get the tab label text |

### Assertions

| Method | Description |
|--------|-------------|
| `assertSelected()` | Assert that tab is selected |
| `assertNotSelected()` | Assert that tab is not selected |

## Usage Examples

### Basic Usage

```java
// Get tab by text
TabElement detailsTab = TabElement.getTabByText(page.locator("body"), "Details");

// Select the tab
detailsTab.select();

// Assert selection
detailsTab.assertSelected();
```

### Through TabSheetElement

```java
TabSheetElement tabSheet = TabSheetElement.get(page);

// Get tab by label
TabElement overview = tabSheet.getTab("Overview");
overview.assertSelected();

// Get currently selected tab
TabElement current = tabSheet.getSelectedTab();
String label = current.getLabel(); // "Overview"
```

### Multiple Tabs

```java
TabSheetElement tabSheet = TabSheetElement.get(page);

// Initial state
TabElement tab1 = tabSheet.getTab("Tab 1");
TabElement tab2 = tabSheet.getTab("Tab 2");

tab1.assertSelected();
tab2.assertNotSelected();

// Switch tabs
tab2.select();
tab2.assertSelected();
tab1.assertNotSelected();
```

### Get Tab Label

```java
TabElement selected = TabElement.getSelectedTab(page.locator("body"));
String currentLabel = selected.getLabel();
System.out.println("Current tab: " + currentLabel);
```

## Related Elements

- `TabSheetElement` - Parent tab sheet container
- `SideNavigationItemElement` - For side navigation
