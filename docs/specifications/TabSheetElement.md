# TabSheetElement Specification

## Overview

`TabSheetElement` is a Playwright element wrapper for the `<vaadin-tabsheet>` web component. It provides helpers to access/select tabs and current content panel.

## Tag Name

```
vaadin-tabsheet
```

## Class Hierarchy

```
VaadinElement
    └── TabSheetElement
```

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-tabsheet"` | HTML tag name |

## API Methods

### Constructor

```java
TabSheetElement(Locator locator)
```

Creates a new `TabSheetElement` from a Playwright locator.

### Static Factory Methods

#### get(Page page)

Get the first tabsheet instance on the page.

```java
TabSheetElement tabSheet = TabSheetElement.get(page);
```

### Tab Methods

| Method | Description |
|--------|-------------|
| `getTab(String label)` | Get a tab by its label |
| `getSelectedTab()` | Get the currently selected tab |
| `selectTab(String label)` | Select a tab by label text |

### Content Methods

| Method | Description |
|--------|-------------|
| `getContentLocator()` | Locator for visible content panel |

### Assertions

| Method | Description |
|--------|-------------|
| `assertTabsCount(int count)` | Assert the number of tabs |

## Usage Examples

### Basic Usage

```java
TabSheetElement tabSheet = TabSheetElement.get(page);

// Assert tab count
tabSheet.assertTabsCount(3);

// Select a tab
tabSheet.selectTab("Settings");

// Get selected tab
TabElement selected = tabSheet.getSelectedTab();
selected.assertSelected();
```

### Accessing Tab Content

```java
TabSheetElement tabSheet = TabSheetElement.get(page);

// Select tab
tabSheet.selectTab("Profile");

// Access content area
Locator content = tabSheet.getContentLocator();
content.locator("input[name='email']").fill("user@example.com");
```

### Working with Individual Tabs

```java
TabSheetElement tabSheet = TabSheetElement.get(page);

// Get specific tab
TabElement profileTab = tabSheet.getTab("Profile");
TabElement settingsTab = tabSheet.getTab("Settings");

// Check states
profileTab.assertSelected();
settingsTab.assertNotSelected();

// Switch
settingsTab.select();
settingsTab.assertSelected();
profileTab.assertNotSelected();
```

### Tab Navigation Flow

```java
TabSheetElement wizard = TabSheetElement.get(page);

// Step 1
wizard.selectTab("Step 1");
Locator content = wizard.getContentLocator();
content.locator("input").fill("value");

// Next step
wizard.selectTab("Step 2");
// ... continue

// Assert final tab
wizard.getSelectedTab().assertSelected();
```

## Related Elements

- `TabElement` - Individual tab
- `AccordionElement` - For accordion-style content
