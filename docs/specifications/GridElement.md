# GridElement Specification

## Overview

`GridElement` is a Playwright element wrapper for the `<vaadin-grid>` web component. It provides helpers for scrolling, querying visible rows, accessing cell content by row/column index or header text, and interacting with the grid's selection, sorting, and row details features. Cell access uses JavaScript evaluation on the grid's internal APIs since body cells live in shadow DOM and are virtualized.

## Tag Name

```
vaadin-grid
```

## Class Hierarchy

```
VaadinElement
    └── GridElement
```

## Implemented Interfaces

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasStyleElement` | CSS class support |
| `HasThemeElement` | Theme attribute support |
| `HasEnabledElement` | Enabled/disabled state |
| `HasGridSelectionElement` | Single/multi row selection |
| `HasGridSortingElement` | Column sorting |
| `HasGridDetailsElement` | Row details panel |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-grid"` | HTML tag name |

## API Methods

### Constructor

```java
GridElement(Locator locator)
```

Creates a new `GridElement` from a Playwright locator.

### Static Factory Methods

#### get(Page page)

Get the first `GridElement` on the page.

```java
GridElement grid = GridElement.get(page);
```

#### get(Locator parent)

Get the first `GridElement` within a parent locator.

```java
GridElement grid = GridElement.get(page.locator("#container"));
```

#### getById(Page page, String id)

Get a `GridElement` by its `id` attribute.

```java
GridElement grid = GridElement.getById(page, "my-grid");
```

### Properties / Getters

| Method | Description |
|--------|-------------|
| `getRowCount()` | Total number of data items |
| `getColumnCount()` | Number of visible (non-hidden) columns |
| `getFirstVisibleRowIndex()` | 0-based index of the first visible row |
| `getLastVisibleRowIndex()` | 0-based index of the last visible row |
| `getVisibleRowCount()` | Count of rows visible in the viewport |
| `isRowInView(int rowIndex)` | Whether the given row is between first and last visible index |
| `isAllRowsVisible()` | Whether `allRowsVisible` is enabled (no vertical scroll) |
| `isMultiSort()` | Whether multi-sort is enabled |
| `isColumnReorderingAllowed()` | Whether column reordering is allowed |

### Header Access Methods

| Method | Description |
|--------|-------------|
| `getHeaderCellContent(int colIndex)` | Text content of a header cell by column index |
| `getHeaderCellContents()` | Text content of all visible header cells |
| `getHeaderCellLocator(int colIndex)` | Locator for a header cell content element |

### Cell Content Access Methods

| Method | Description |
|--------|-------------|
| `getCellContent(int rowIndex, int colIndex)` | Text content of a body cell (row must be in view) |
| `getCellContent(int rowIndex, String headerText)` | Text content by row index and header text |
| `getCellContentLocator(int rowIndex, int colIndex)` | Locator for body cell content (for component renderers) |
| `getCellContentLocator(int rowIndex, String headerText)` | Locator by row index and header text |
| `resolveColumnIndex(String headerText)` | Resolve header text to its visible column index |

### Scroll Actions

| Method | Description |
|--------|-------------|
| `scrollToRow(int rowIndex)` | Scroll so the given row index becomes visible |
| `scrollToStart()` | Scroll to the very beginning |
| `scrollToEnd()` | Scroll to the very end |

### Selection Methods (HasGridSelectionElement)

| Method | Description |
|--------|-------------|
| `getSelectedItemCount()` | Number of currently selected items |
| `selectRow(int rowIndex)` | Click a row to select it (single-select) |
| `toggleRowSelection(int rowIndex)` | Toggle the selection checkbox (multi-select) |
| `toggleSelectAll()` | Toggle the select-all header checkbox |
| `deselectAll()` | Deselect all items programmatically |
| `isRowSelected(int rowIndex)` | Whether a row is currently selected |

### Sorting Methods (HasGridSortingElement)

| Method | Description |
|--------|-------------|
| `clickHeaderToSort(int colIndex)` | Click the sorter element to cycle sort direction |
| `clickHeaderToSort(String headerText)` | Click sorter by header text |
| `getSortDirection(int colIndex)` | Get sort direction: `"asc"`, `"desc"`, or `null` |
| `getSortDirection(String headerText)` | Get sort direction by header text |

### Row Details Methods (HasGridDetailsElement)

| Method | Description |
|--------|-------------|
| `openDetails(int rowIndex)` | Click a row to toggle its details panel |
| `isDetailsOpen(int rowIndex)` | Whether a row's details panel is open |

### Assertions

| Method | Description |
|--------|-------------|
| `assertRowCount(int expected)` | Assert total row count (auto-retry) |
| `assertEmpty()` | Assert zero rows |
| `assertColumnCount(int expected)` | Assert visible column count (auto-retry) |
| `assertRowInView(int rowIndex)` | Assert row is visible (auto-retry) |
| `assertRowNotInView(int rowIndex)` | Assert row is NOT visible (auto-retry) |
| `assertFirstVisibleRow(int expected)` | Assert first visible row index (auto-retry) |
| `assertLastVisibleRow(int expected)` | Assert last visible row index (auto-retry) |
| `assertCellContent(int row, int col, String expected)` | Assert cell text content |
| `assertCellContent(int row, String header, String expected)` | Assert cell text by header |
| `assertHeaderCellContent(int col, String expected)` | Assert header cell text |
| `assertAllRowsVisible()` | Assert allRowsVisible is enabled |
| `assertNotAllRowsVisible()` | Assert allRowsVisible is disabled |
| `assertSelectedItemCount(int expected)` | Assert selected item count |
| `assertRowSelected(int rowIndex)` | Assert row is selected |
| `assertRowNotSelected(int rowIndex)` | Assert row is NOT selected |
| `assertSortDirection(int colIndex, String direction)` | Assert sort direction |
| `assertSortDirection(String headerText, String direction)` | Assert sort direction by header |
| `assertNotSorted(int colIndex)` | Assert column is unsorted |
| `assertDetailsOpen(int rowIndex)` | Assert details panel is open |
| `assertDetailsClosed(int rowIndex)` | Assert details panel is closed |

## Usage Examples

### Basic Cell Access

```java
GridElement grid = GridElement.getById(page, "my-grid");

// Row and column counts
int rows = grid.getRowCount();
int cols = grid.getColumnCount();

// Headers
List<String> headers = grid.getHeaderCellContents();
String firstHeader = grid.getHeaderCellContent(0);

// Cell content by index
String value = grid.getCellContent(0, 0);

// Cell content by header text
String email = grid.getCellContent(0, "Email");
```

### Scrolling

```java
GridElement grid = GridElement.getById(page, "my-grid");

grid.scrollToRow(50);
grid.assertRowInView(50);

grid.scrollToStart();
grid.assertFirstVisibleRow(0);

grid.scrollToEnd();
grid.assertRowInView(grid.getRowCount() - 1);
```

### Single Selection

```java
GridElement grid = GridElement.getById(page, "single-select-grid");

grid.selectRow(0);
grid.assertRowSelected(0);

grid.selectRow(1);
grid.assertRowSelected(1);
grid.assertRowNotSelected(0);
```

### Multi Selection

```java
GridElement grid = GridElement.getById(page, "multi-select-grid");

grid.toggleRowSelection(0);
grid.toggleRowSelection(1);
grid.assertSelectedItemCount(2);

grid.toggleSelectAll();
grid.assertSelectedItemCount(totalRows);

grid.deselectAll();
grid.assertSelectedItemCount(0);
```

### Sorting

```java
GridElement grid = GridElement.getById(page, "sortable-grid");

grid.clickHeaderToSort("Name");
grid.assertSortDirection("Name", "asc");

grid.clickHeaderToSort("Name");
grid.assertSortDirection("Name", "desc");

grid.clickHeaderToSort("Name");
grid.assertNotSorted(0);
```

### Row Details

```java
GridElement grid = GridElement.getById(page, "details-grid");

grid.openDetails(0);
grid.assertDetailsOpen(0);

grid.openDetails(0);   // click again to close
grid.assertDetailsClosed(0);
```

### Component Renderer

```java
GridElement grid = GridElement.getById(page, "component-grid");

// Get the cell content locator for a cell with a component renderer
Locator cellContent = grid.getCellContentLocator(0, "Action");

// Find and interact with the button inside
Locator button = cellContent.locator("vaadin-button");
assertThat(button).hasText("Click First1");
button.click();
```

### LitRenderer

```java
GridElement grid = GridElement.getById(page, "lit-renderer-grid");

// Read text from a LitRenderer cell
String text = grid.getCellContent(0, "First Name");

// Locate elements inside a LitRenderer cell
Locator cellContent = grid.getCellContentLocator(0, "First Name");
Locator badge = cellContent.locator(".badge");
assertThat(badge).hasText("First1");

// Click a LitRenderer button
Locator actionCell = grid.getCellContentLocator(0, "Action");
actionCell.locator("vaadin-button").click();
```

### Lazy Loading

```java
GridElement grid = GridElement.getById(page, "lazy-grid");

grid.assertRowCount(10000);
grid.scrollToRow(9000);
grid.assertRowInView(9000);
String name = grid.getCellContent(9000, 0);
```

## Related Elements

- `VirtualListElement` - Virtualized scrollable list (simpler, no columns)
