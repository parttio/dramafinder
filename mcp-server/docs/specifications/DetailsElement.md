# DetailsElement Specification

## Overview

`DetailsElement` is a Playwright element wrapper for the `<vaadin-details>` web component. It provides helpers to open/close and access the summary/content.

## Tag Name

```
vaadin-details
```

## Class Hierarchy

```
VaadinElement
    └── DetailsElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasStyleElement` | Style attribute support |
| `HasThemeElement` | Theme variants support |
| `HasTooltipElement` | Tooltip support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-details"` | HTML tag name |

## API Methods

### Constructor

```java
DetailsElement(Locator locator)
```

Creates a new `DetailsElement` from a Playwright locator.

### Static Factory Methods

#### getBySummaryText(Page page, String summary)

Get a details component by its summary text.

```java
DetailsElement details = DetailsElement.getBySummaryText(page, "More Information");
```

### State Methods

| Method | Description |
|--------|-------------|
| `isOpen()` | Whether the details is opened |
| `setOpen(boolean open)` | Set opened state by clicking summary |
| `getSummaryText()` | Get the text of the summary |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getSummaryLocator()` | Locator for the summary element |
| `getContentLocator()` | Locator for the visible content |

### Assertions

| Method | Description |
|--------|-------------|
| `assertOpened()` | Assert that details is opened |
| `assertClosed()` | Assert that details is closed |
| `assertEnabled()` | Assert that component is enabled |
| `assertDisabled()` | Assert that component is disabled |
| `assertContentVisible()` | Assert content is visible |
| `assertContentNotVisible()` | Assert content is hidden |

## Usage Examples

### Basic Usage

```java
DetailsElement details = DetailsElement.getBySummaryText(page, "Advanced Options");

// Check and toggle state
details.assertClosed();
details.setOpen(true);
details.assertOpened();

// Verify content visibility
details.assertContentVisible();

// Get summary text
String summary = details.getSummaryText(); // "Advanced Options"
```

### Accessing Content

```java
DetailsElement details = DetailsElement.getBySummaryText(page, "Settings");
details.setOpen(true);

// Access the content area
Locator content = details.getContentLocator();
content.locator("input").first().fill("value");
```

### Disabled State

```java
DetailsElement details = DetailsElement.getBySummaryText(page, "Locked Section");
details.assertDisabled();
```

## Related Elements

- `AccordionElement` - Multiple collapsible panels
- `AccordionPanelElement` - Individual accordion panel
