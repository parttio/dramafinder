# TreeGridElement Specification

## Overview

`TreeGridElement` is a Playwright element wrapper for `<vaadin-grid>` backed by a Vaadin `TreeGrid`. It extends `GridElement` with tree-specific APIs for querying hierarchy levels, checking expanded/collapsed/leaf state, and performing single-row or level-based bulk expand and collapse operations.

> **Note:** Vaadin's `TreeGrid` renders using the **same `<vaadin-grid>` web component** as the plain `Grid`. The hierarchy is expressed through `<vaadin-grid-tree-toggle>` elements that Vaadin renders inside each row's hierarchy column cell content. `TreeGridElement` therefore shares the same tag name constant as `GridElement` and inherits all of its row, cell, header, selection, and sorting APIs.

All row and cell access methods auto-scroll the grid as needed to bring virtualized rows into view.

## Tag Name

```
vaadin-grid
```

## Class Hierarchy

```
VaadinElement
    └── GridElement
            └── TreeGridElement
```

## Implemented Interfaces

Inherited from `GridElement`:

| Interface | Description |
|-----------|-------------|
| `FocusableElement` | Focus operations |
| `HasStyleElement` | CSS class support |
| `HasThemeElement` | Theme attribute support |
| `HasEnabledElement` | Enabled/disabled state |

## Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `FIELD_TAG_NAME` | `"vaadin-grid"` | Same tag as `GridElement` |

## API Methods

### Constructor

```java
TreeGridElement(Locator locator)
```

Creates a new `TreeGridElement` from a Playwright locator pointing to the `<vaadin-grid>` element backed by a `TreeGrid`.

### Static Factory Methods

#### get(Page page)

Get the first `TreeGridElement` on the page.

```java
TreeGridElement grid = TreeGridElement.get(page);
```

#### get(Locator parent)

Get the first `TreeGridElement` within a parent locator.

```java
TreeGridElement grid = TreeGridElement.get(page.locator("#container"));
```

#### getById(Page page, String id)

Get a `TreeGridElement` by its `id` attribute.

```java
TreeGridElement grid = TreeGridElement.getById(page, "my-tree-grid");
```

### Row-Level Query Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `findTreeRow(int rowIndex)` | `Optional<TreeRowElement>` | Like `findRow` but returns a `TreeRowElement` with tree-specific APIs; auto-scrolls if needed |
| `isRowExpanded(int rowIndex)` | `boolean` | `true` if the row's tree toggle has `expanded = true` |
| `isRowCollapsed(int rowIndex)` | `boolean` | `true` if the row has children but is not expanded |
| `isRowLeaf(int rowIndex)` | `boolean` | `true` if the row has no children (`leaf = true` on the toggle) |
| `getRowLevel(int rowIndex)` | `int` | 0-based hierarchy depth of the row (root items = 0) |
| `getExpandedRowCount()` | `int` | Number of currently visible expanded rows |

### Row-Level Expand / Collapse

| Method | Description |
|--------|-------------|
| `expandRow(int rowIndex)` | Expand the row. No-op if already expanded or a leaf node |
| `collapseRow(int rowIndex)` | Collapse the row. No-op if already collapsed or a leaf node |
| `toggleRow(int rowIndex)` | Toggle expanded/collapsed state. No-op if the row is a leaf |

### Inherited Grid Methods

All `GridElement` methods are inherited unchanged, including:

- `getTotalRowCount()`, `getRenderedRowCount()`, `getColumnCount()`
- `findRow(int rowIndex)`, `findCell(int row, int col)`, `findCell(int row, String header)`
- `findHeaderCell(...)`, `getHeaderCellContents()`
- `select(int)`, `deselect(int)`, `getSelectedItemCount()`
- `checkSelectAll()`, `uncheckSelectAll()`, `isSelectAllChecked()`, `isSelectAllIndeterminate()`, `isSelectAllUnchecked()`
- `waitForGridToStopLoading()`, `scrollToRow(int)`, `scrollToStart()`, `scrollToEnd()`

## Inner Classes

### TreeRowElement extends GridElement.RowElement

Represents a row in a `TreeGrid`, extending `RowElement` with tree-specific state queries and expand/collapse actions. Use `findTreeRow(int)` to obtain one, or construct from an existing `RowElement`.

#### Constructors

```java
// Obtain directly (preferred — use findTreeRow on the grid)
TreeRowElement row = grid.findTreeRow(0).get();

// Upgrade an existing RowElement
TreeRowElement row = new TreeRowElement(grid.findRow(0).get());
```

#### Tree-Specific Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTreeToggleLocator()` | `Locator` | Locator for the `<vaadin-grid-tree-toggle>` in this row's hierarchy cell |
| `isExpanded()` | `boolean` | `true` if this row is expanded |
| `isCollapsed()` | `boolean` | `true` if this row has children but is not expanded |
| `isLeaf()` | `boolean` | `true` if this row has no children |
| `getLevel()` | `int` | 0-based hierarchy depth of this row |
| `expand()` | `void` | Expand this row. No-op if already expanded or a leaf |
| `collapse()` | `void` | Collapse this row. No-op if already collapsed or a leaf |
| `toggle()` | `void` | Toggle expanded/collapsed state. No-op if a leaf |

#### Inherited RowElement Methods

All `RowElement` methods are available: `getRowLocator()`, `getRowIndex()`, `getCell(int)`, `getCell(String)`, `isSelected()`, `select()`, `deselect()`, `openDetails()`, `closeDetails()`, `isDetailsOpen()`.

## Implementation Notes

### How tree state is read

`<vaadin-grid-tree-toggle>` exposes three key values, but they are surfaced differently:

| Value | Reflected as HTML attribute? | How it is read |
|-------|------------------------------|----------------|
| `expanded` | **Yes** | `toggle.getAttribute("expanded") != null` |
| `leaf` | **Yes** | `toggle.getAttribute("leaf") != null` |
| `level` | **No** (DOM property only) | `toggle.evaluate("el => el.level")` |

Because `expanded` and `leaf` are reflected boolean attributes, their presence in the DOM means `true` and their absence means `false`. This also means CSS attribute selectors work for them:

```java
// Count all visible expanded toggles using a CSS selector — no JS needed
locator.locator("vaadin-grid-tree-toggle[expanded]").count();
```

`level` is not reflected, so it must be read via JavaScript:

```javascript
toggle.evaluate("el => el.level")  // returns a Number
```

### How the hierarchy column is located

`TreeGridElement` searches **all cells** in the row for the one whose cell content contains a `<vaadin-grid-tree-toggle>`, rather than assuming the toggle is always in cell 0. This correctly handles:

- **Multi-select mode:** Vaadin inserts a checkbox selection column at DOM index 0, pushing the hierarchy column to index 1.
- **Column reordering:** The hierarchy column may be at any position.

### Why expand/collapse uses a JS synthetic click

`<vaadin-grid-cell-content>` intercepts pointer events, which causes Playwright's coordinate-based `.click()` to never reach the `<vaadin-grid-tree-toggle>` inside it. Toggle clicks are therefore performed via:

```javascript
toggle.evaluate("el => el.click()")
```

This matches how Vaadin's own TestBench performs the same operation via Selenium's `WebElement.click()`.

## Usage Examples

### Basic Tree State Queries

```java
TreeGridElement grid = TreeGridElement.getById(page, "my-tree-grid");

// Row counts
int total = grid.getTotalRowCount();       // visible rows (collapsed children not counted)
int expanded = grid.getExpandedRowCount(); // number of currently expanded rows

// Hierarchy state of row at index 0
boolean isLeaf = grid.isRowLeaf(0);
boolean isExpanded = grid.isRowExpanded(0);
boolean isCollapsed = grid.isRowCollapsed(0);
int level = grid.getRowLevel(0);           // 0 = root
```

### Single-Row Expand / Collapse

```java
TreeGridElement grid = TreeGridElement.getById(page, "my-tree-grid");

// Expand root row 0 (no-op if already expanded or a leaf)
grid.expandRow(0);
assertTrue(grid.isRowExpanded(0));

// Access a child row that is now visible at flat index 1
var child = grid.findCell(1, "Name");
assertEquals("Child 0-0", child.get().getCellContentLocator().innerText());

// Collapse it again
grid.collapseRow(0);
assertEquals(3, grid.getTotalRowCount());
```

### TreeRowElement API

```java
TreeGridElement grid = TreeGridElement.getById(page, "my-tree-grid");

// Obtain a tree-aware row object
TreeGridElement.TreeRowElement row = grid.findTreeRow(0).get();

System.out.println(row.getLevel());    // 0
System.out.println(row.isLeaf());      // false
System.out.println(row.isExpanded());  // false

row.expand();
assertTrue(row.isExpanded());

row.collapse();
assertFalse(row.isExpanded());
```

### Upgrading a RowElement to TreeRowElement

```java
// If you already have a RowElement from a generic helper, wrap it:
GridElement.RowElement plainRow = grid.findRow(0).get();
TreeGridElement.TreeRowElement treeRow = new TreeGridElement.TreeRowElement(plainRow);

assertEquals(0, treeRow.getLevel());
assertFalse(treeRow.isLeaf());
```

### Selection in a Multi-Select TreeGrid

```java
// Multi-select mode adds a checkbox column at index 0.
// TreeGridElement handles this automatically.
TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");

grid.select(0);
grid.select(1);
assertEquals(2, grid.getSelectedItemCount());

// Expand, then select a child row too
grid.expandRow(0);
grid.select(1); // first child of row 0
assertTrue(grid.findRow(1).get().isSelected());

// Select-all checkbox
grid.checkSelectAll();
assertTrue(grid.isSelectAllChecked());

grid.uncheckSelectAll();
assertEquals(0, grid.getSelectedItemCount());
```

### Multi-Level Tree (Three Levels)

```java
TreeGridElement grid = TreeGridElement.getById(page, "three-level-tree-grid");
assertEquals(2, grid.getTotalRowCount()); // only root divisions visible

// Expand Division A (index 0) to reveal its departments
grid.expandRow(0);
assertEquals(4, grid.getTotalRowCount()); // 2 divisions + Dept A1, Dept A2

// Expand Dept A1 (now at index 1) to reveal its employees
grid.expandRow(1);
assertEquals(6, grid.getTotalRowCount()); // + Employee A1a, Employee A1b

// Verify grandchild level and leaf state
assertEquals(2, grid.findTreeRow(2).get().getLevel()); // Employee A1a is level 2
assertTrue(grid.findTreeRow(2).get().isLeaf());

// Collapse Division A — hides all its descendants
grid.collapseRow(0);
assertEquals(2, grid.getTotalRowCount());
```
