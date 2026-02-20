# GridElement Specification

## Overview

`GridElement` is a Playwright element wrapper for the `<vaadin-grid>` web component. It provides helpers for querying rows and cells, accessing cell content by row/column index or header text, and interacting with the grid's selection, sorting, and row details features. Row and cell access methods **auto-scroll** the grid as needed to bring virtualized rows into view. 

Results are returned as `Optional` wrappers or inner-class elements (`RowElement`, `CellElement`, `HeaderCellElement`) so callers can interact with rows and cells directly rather than going back through the grid. Please note, that you should interact with these elements before searching for another row which may lead to scrolling and possibly hiding / removing element that is referenced. 

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
| `getTotalRowCount()` | Total number of data items |
| `getRenderedRowCount()` | Number of rows currently rendered in the DOM |
| `getColumnCount()` | Number of visible (non-hidden) columns |
| `isAllRowsVisible()` | Whether `allRowsVisible` is enabled (no vertical scroll) |
| `isMultiSort()` | Whether multi-sort is enabled |
| `isColumnReorderingAllowed()` | Whether column reordering is allowed |

### Header Access Methods

All `findHeaderCell*` methods return `Optional<HeaderCellElement>`.

| Method | Description |
|--------|-------------|
| `findHeaderCell(int colIndex)` | Header cell by column index in the first header row |
| `findHeaderCell(int headerRowIndex, int colIndex)` | Header cell by header row index and column index |
| `findHeaderCellByText(String text)` | Header cell by text content in the first header row |
| `findHeaderCellByText(int headerRowIndex, String text)` | Header cell by header row index and text content |
| `getHeaderCellContents()` | Text content of all visible header cells |

### Row Access Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `findRow(int rowIndex)` | `Optional<RowElement>` | Find row by index; **auto-scrolls** if the row is not yet in view |
| `findRowIndexesWithColumnText(int columnIndex, String text)` | `List<Integer>` | Row indexes where the given column cell matches the given text |

### Cell Access Methods

Both methods return `Optional<CellElement>` and **auto-scroll** the grid if the target row is not yet in view.

| Method | Description |
|--------|-------------|
| `findCell(int row, int column)` | Body cell by row and column index |
| `findCell(int row, String columnHeaderText)` | Body cell by row index and column header text |

### Selection Methods

| Method | Description |
|--------|-------------|
| `getSelectedItemCount()` | Number of currently selected items |
| `select(int rowIndex)` | Select row by index |
| `deselect(int rowIndex)` | Deselect row by index |
| `isSelectAllChecked()` | Whether the select-all checkbox is checked |
| `isSelectAllIndeterminate()` | Whether the select-all checkbox is indeterminate |
| `isSelectAllUnchecked()` | Whether the select-all checkbox is unchecked |
| `checkSelectAll()` | Check the select-all checkbox (no-op if already checked) |
| `uncheckSelectAll()` | Uncheck the select-all checkbox (no-op if already unchecked) |

### Utility Methods

| Method | Description |
|--------|-------------|
| `waitForGridToStopLoading()` | Wait for the grid to finish loading after a scroll or action that triggers row loading |

## Inner Classes

### CellElement

Wraps a single grid cell (`<td>`).

| Method | Description |
|--------|-------------|
| `getTableCell()` | Locator for the table cell element (`<td>`) |
| `getColumnIndex()` | 0-based column index; `-1` for details cells |
| `getCellContent()` | Locator for the `<vaadin-grid-cell-content>` slot element |
| `getContentSlotName()` | Name of the slot used for cell content |
| `click()` | Click the cell content |

### HeaderCellElement extends CellElement

Wraps a single grid header cell (`<th>`).

Extends `CellElement` with sorting support.

| Method | Description |
|--------|-------------|
| `isSortable()` | Whether the column supports sorting |
| `clickSort()` | Click the sorter to cycle sort direction |
| `isSortAscending()` | Whether the column is sorted ascending |
| `isSortDescending()` | Whether the column is sorted descending |
| `isNotSorted()` | Whether the column is not sorted |

### RowElement

Wraps a single grid row (`<tr>`).

| Method | Description |
|--------|-------------|
| `getRow()` | Locator for the row element `<tr>` |
| `getRowIndex()` | 0-based row index |
| `getCell(int columnIndex)` | Cell for the given column index |
| `getCell(String columnHeaderText)` | Cell for the given header text |
| `getDetailsCell()` | Cell element for the details column |
| `isSelected()` | Whether the row is selected |
| `select()` | Select this row |
| `deselect()` | Deselect this row |
| `openDetails()` | Open this row's details panel |
| `closeDetails()` | Close this row's details panel |
| `isDetailsOpen()` | Whether this row's details panel is open |

## Usage Examples

### Basic Cell Access

```java
GridElement grid = GridElement.getById(page, "my-grid");

// Row and column counts
int rows = grid.getTotalRowCount();
int cols = grid.getColumnCount();

// Headers
List<String> headers = grid.getHeaderCellContents();

// Cell content by index (auto-scrolls if needed)
var valueCellO = grid.findCell(0, 0);
assertTrue(valueCellO.isPresent());
assertThat(valueCellO.get().getCellContent()).hasText("My name");

var emailCellO = grid.findCell(0, "Email");
assertTrue(emailCellO.isPresent());
assertThat(emailCellO.get().getCellContent()).hasText("my@email.com");
```

### Single Selection

```java
GridElement grid = GridElement.getById(page, "single-select-grid");

grid.select(0);
var row0 = grid.findRow(0);
assertTrue(row0.isPresent());
assertTrue(row0.get().isSelected());

grid.select(1);
assertFalse(row0.get().isSelected());
```

### Multi Selection

```java
GridElement grid = GridElement.getById(page, "multi-select-grid");

grid.select(0);
grid.select(1);
assertEquals(2, grid.getSelectedItemCount());

grid.checkSelectAll();
assertTrue(grid.isSelectAllChecked());

grid.uncheckSelectAll();
assertEquals(0, grid.getSelectedItemCount());
```

### Sorting

```java
GridElement grid = GridElement.getById(page, "sortable-grid");

var headerCell = grid.findHeaderCellByText("Name");
assertTrue(headerCell.isPresent());
headerCell.get().clickSort();
assertTrue(headerCell.get().isSortAscending());

headerCell.get().clickSort();
assertTrue(headerCell.get().isSortDescending());

headerCell.get().clickSort();
assertTrue(headerCell.get().isNotSorted());
```

### Row Details

```java
GridElement grid = GridElement.getById(page, "details-grid");

var row = grid.findRow(0);
assertTrue(row.isPresent());
row.get().openDetails();
assertTrue(row.get().isDetailsOpen());

row.get().closeDetails();
assertFalse(row.get().isDetailsOpen());
```

### Component Renderer

```java
GridElement grid = GridElement.getById(page, "component-grid");

var actionCell = grid.findCell(0, "Action");
assertTrue(actionCell.isPresent());
Locator button = actionCell.get().getCellContent().locator("vaadin-button");
assertThat(button).hasText("Click First1");
button.click();
```

### LitRenderer

```java
GridElement grid = GridElement.getById(page, "lit-renderer-grid");

// Read text from a LitRenderer cell (auto-scrolls if needed)
var firstNameCell = grid.findCell(0, "First Name");
assertTrue(firstNameCell.isPresent());
assertThat(firstNameCell.get().getCellContent().locator(".badge")).hasText("First1");

// Click a LitRenderer button
var actionCell = grid.findCell(0, "Action");
assertTrue(actionCell.isPresent());
actionCell.get().getCellContent().locator("vaadin-button").click();
```

### Lazy Loading

```java
GridElement grid = GridElement.getById(page, "lazy-grid");

assertEquals(10000, grid.getTotalRowCount());

// findRow auto-scrolls to row 9000 before returning
var row = grid.findRow(9000);
assertTrue(row.isPresent());
assertThat(row.get().getCell(0).getCellContent()).hasText("First9001");
```

### Searching Across Rows

```java
GridElement grid = GridElement.getById(page, "my-grid");

// Find all row indexes where the "Status" column contains "Active"
List<Integer> activeRows = grid.findRowIndexesWithColumnText(2, "Active");
```

## Related Elements

- `VirtualListElement` - Virtualized scrollable list (simpler, no columns)
