# AccordionElement Specification

## Overview

`AccordionElement` is a Playwright element wrapper for the `<vaadin-accordion>` web component. It provides helpers to access panels by their summary text and to assert open/closed state and panel count.

## Tag Name

```
vaadin-accordion
```

## Class Hierarchy

```
VaadinElement
    └── AccordionElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-accordion"` | HTML tag name |

## API Methods

### Constructor

```java
AccordionElement(Locator locator)
```

Creates a new `AccordionElement` from a Playwright locator.

### Panel Management

| Method | Description |
|--------|-------------|
| `getPanel(String summary)` | Get a panel by its summary text |
| `openPanel(String summary)` | Open a panel by its summary text |
| `closePanel(String summary)` | Close a panel by its summary text |
| `getOpenedPanel()` | Get the currently opened panel |
| `isPanelOpened(String summary)` | Check if a panel is open |

### Assertions

| Method | Description |
|--------|-------------|
| `assertPanelCount(int count)` | Assert the number of panels |
| `assertPanelOpened(String summary)` | Assert that a panel is open |
| `assertPanelClosed(String summary)` | Assert that a panel is closed |

## Usage Examples

### Basic Usage

```java
AccordionElement accordion = new AccordionElement(page.locator("vaadin-accordion"));

// Assert panel count
accordion.assertPanelCount(3);

// Open and close panels
accordion.openPanel("Personal Information");
accordion.assertPanelOpened("Personal Information");

accordion.closePanel("Personal Information");
accordion.assertPanelClosed("Personal Information");

// Get the currently opened panel
AccordionPanelElement openedPanel = accordion.getOpenedPanel();
```

## Related Elements

- `AccordionPanelElement` - Individual accordion panel
