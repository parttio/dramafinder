# VirtualListElement Specification

## Overview

`VirtualListElement` is a Playwright element wrapper for the `<vaadin-virtual-list>` web component. It wraps a virtualized scrollable list that lazily renders items as the user scrolls. It provides helpers for scrolling, querying visible rows, accessing rendered item content, and retrieving typed component elements inside items.

## Tag Name

```
vaadin-virtual-list
```

## Class Hierarchy

```
VaadinElement
    └── VirtualListElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasStyleElement` | Style attribute support |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-virtual-list"` | HTML tag name |

## API Methods

### Constructor

```java
VirtualListElement(Locator locator)
```

Creates a new `VirtualListElement` from a Playwright locator.

### Static Factory Methods

#### get(Page page)

Get the first `VirtualListElement` on the page.

```java
VirtualListElement list = VirtualListElement.get(page);
```

### Properties / Getters

| Method | Description |
|--------|-------------|
| `getRowCount()` | Total number of items (including items not yet rendered) |
| `getFirstVisibleRowIndex()` | 0-based index of the first visible row |
| `getLastVisibleRowIndex()` | 0-based index of the last visible row |
| `getVisibleRowCount()` | Count of rows visible in the viewport |
| `isRowInView(int rowIndex)` | Whether the given row index is currently visible |

All visibility methods flush the connector debouncer before reading the property.

### Item Query Methods

| Method | Description |
|--------|-------------|
| `getRenderedItems()` | Locator for all currently rendered child elements |
| `getItemByIndex(int index)` | Locator for the rendered item at the given virtual index (must be in view) |
| `getItemByText(String text)` | Locator for the first rendered item containing the given text |

### Component Query Methods

| Method | Description |
|--------|-------------|
| `getItemComponent(int index, Class<T> type)` | Get a typed component from the item at the given virtual index |
| `getItemComponentByText(String text, Class<T> type)` | Get a typed component from the item matching the given text |
| `getComponent(Class<T> type)` | Get the first typed component found in any rendered item |

Component query methods read the tag from the `@PlaywrightElement` annotation on the target class and require a public constructor accepting a single `Locator` parameter.

### Scroll Actions

| Method | Description |
|--------|-------------|
| `scrollToRow(int rowIndex)` | Scroll the list so the given row index becomes visible |
| `scrollToStart()` | Scroll to the very beginning of the list |
| `scrollToEnd()` | Scroll to the very end of the list |

### Assertions

| Method | Description |
|--------|-------------|
| `assertRowCount(int expected)` | Assert the total number of items matches (auto-retry) |
| `assertEmpty()` | Assert the list has zero items |
| `assertRowInView(int rowIndex)` | Assert the given row is currently visible (auto-retry) |
| `assertRowNotInView(int rowIndex)` | Assert the given row is NOT currently visible (auto-retry) |
| `assertFirstVisibleRow(int expected)` | Assert the first visible row index (hasJSProperty, auto-retry) |
| `assertLastVisibleRow(int expected)` | Assert the last visible row index (hasJSProperty, auto-retry) |
| `assertItemRendered(String text)` | Assert an item containing the given text is visible in the DOM |

## Usage Examples

### Basic Scrolling

```java
VirtualListElement list = new VirtualListElement(page.locator("#my-list"));

list.scrollToRow(50);       // scroll to index 50
list.scrollToStart();       // scroll to the top
list.scrollToEnd();         // scroll to the bottom
```

### Querying Items

```java
VirtualListElement list = new VirtualListElement(page.locator("#my-list"));

// Total item count (including non-rendered)
int total = list.getRowCount();

// Visibility info
int first = list.getFirstVisibleRowIndex();
int last = list.getLastVisibleRowIndex();
int visible = list.getVisibleRowCount();
boolean inView = list.isRowInView(50);

// Get rendered items
Locator allRendered = list.getRenderedItems();
Locator itemAtIndex = list.getItemByIndex(0);
Locator itemByText = list.getItemByText("Item 1");
```

### Getting Typed Components Inside Items

```java
VirtualListElement list = new VirtualListElement(page.locator("#my-list"));

// Get a ButtonElement from item at index 0
ButtonElement btn = list.getItemComponent(0, ButtonElement.class);

// Get a ButtonElement from item matching text
ButtonElement btn = list.getItemComponentByText("Row 1", ButtonElement.class);

// Get the first ButtonElement in any rendered item
ButtonElement btn = list.getComponent(ButtonElement.class);
```

### Scroll and Read

```java
VirtualListElement list = new VirtualListElement(page.locator("#my-list"));

list.scrollToRow(50);
list.assertItemRendered("Item 51");
assertThat(list.getItemByText("Item 51")).isVisible();
```

### Assertions

```java
VirtualListElement list = new VirtualListElement(page.locator("#my-list"));

// Row count
list.assertRowCount(100);
list.assertEmpty();

// Visibility
list.assertRowInView(0);
list.assertRowNotInView(99);
list.assertFirstVisibleRow(0);
list.assertLastVisibleRow(10);

// Item content
list.assertItemRendered("Item 1");
```

### Empty List

```java
VirtualListElement empty = new VirtualListElement(page.locator("#empty-list"));
empty.assertEmpty();
assertEquals(0, empty.getRowCount());
```

## Related Elements

- `ListBoxElement` - Non-virtualized list with selection support
