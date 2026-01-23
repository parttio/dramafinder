# AccordionPanelElement Specification

## Overview

`AccordionPanelElement` is a Playwright element wrapper for the `<vaadin-accordion-panel>` web component. It offers utilities to toggle open state, read summary and access content.

## Tag Name

```
vaadin-accordion-panel
```

## Class Hierarchy

```
VaadinElement
    └── AccordionPanelElement
```

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-accordion-panel"` | HTML tag name |
| `FIELD_HEADING_TAG_NAME` | `"vaadin-accordion-heading"` | Heading tag name |

## API Methods

### Constructor

```java
AccordionPanelElement(Locator locator)
```

Creates a new `AccordionPanelElement` from a Playwright locator.

### Static Factory Methods

#### getAccordionPanelBySummary(Locator locator, String summary)

Get an accordion panel by its summary text within a scope.

#### getOpenedAccordionPanel(Locator locator)

Get the currently opened accordion panel within a scope.

### State Methods

| Method | Description |
|--------|-------------|
| `isOpen()` | Whether the panel is open |
| `setOpen(boolean open)` | Set the open state by clicking summary |
| `getSummaryText()` | Get the text content of the summary |

### Locator Methods

| Method | Description |
|--------|-------------|
| `getSummaryLocator()` | Locator for the summary heading |
| `getContentLocator()` | Locator for the content element |

### Assertions

| Method | Description |
|--------|-------------|
| `assertOpened()` | Assert that the panel is opened |
| `assertClosed()` | Assert that the panel is closed |
| `assertEnabled()` | Assert that the panel is enabled |
| `assertDisabled()` | Assert that the panel is disabled |
| `assertContentVisible()` | Assert that content is visible |
| `assertContentNotVisible()` | Assert that content is hidden |

## Usage Examples

### Basic Usage

```java
AccordionPanelElement panel = AccordionPanelElement.getAccordionPanelBySummary(
    page.locator("vaadin-accordion"), "Details");

// Check and toggle state
if (!panel.isOpen()) {
    panel.setOpen(true);
}
panel.assertOpened();

// Access content
String summary = panel.getSummaryText();
Locator content = panel.getContentLocator();
```

## Related Elements

- `AccordionElement` - Parent accordion container
