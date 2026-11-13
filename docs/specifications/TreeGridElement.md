# Tree Grid

`TreeGridElement` wraps the `<vaadin-grid>` of a Flow `TreeGrid`. It adds
expanding, collapsing and hierarchy queries to everything
[`GridElement`](GridElement.md) does: cells, headers, selection, sorting, details.

| | |
|---|---|
| Web component | `<vaadin-grid>` (with `<vaadin-grid-tree-toggle>` in the hierarchy column) |
| Flow component | `com.vaadin.flow.component.treegrid.TreeGrid` |
| Wrapper | `org.vaadin.addons.dramafinder.element.TreeGridElement` (extends `GridElement`) |
| Typed query | `find(page, TreeGridElement.class)` → `ComponentQuery`, typed as `GridElement` |
| API | [TreeGridElement](../../skills/vaadin-playwright-test/api-reference.md#treegridelement) |

Vaadin's `TreeGrid` renders with the **same `<vaadin-grid>` web component** as the
plain `Grid`. The hierarchy is expressed through `<vaadin-grid-tree-toggle>`
elements that Vaadin renders inside each row's hierarchy column cell content, so
`TreeGridElement` shares `GridElement`'s tag name constant
(`FIELD_TAG_NAME = "vaadin-grid"`) and inherits all of its APIs. Row indexes are
**flat**: they count every visible row, expanded children included, from the top.

## Finding the component

```java
TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
TreeGridElement first = TreeGridElement.get(page);                         // first grid on the page
TreeGridElement scoped = TreeGridElement.get(page.locator("#org-chart"));  // first grid in a container
TreeGridElement byTestId = TreeGridElement.getByTestId(page, "org-tree");
```

`TreeGridElement` inherits `GridElement`'s query binding (Java forbids two
parameterizations of one interface), so `find(page, TreeGridElement.class)`
returns `TreeGridElement` instances whose static type is `GridElement`. Cast to
get the tree API:

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

TreeGridElement grid = (TreeGridElement) find(page, TreeGridElement.class)
        .withId("two-level-tree-grid").single();
```

## The example tree

The examples below use this view:

```java
// View
TreeData<String> data = new TreeData<>();
data.addRootItems("Root 0", "Root 1", "Root 2");
data.addItems("Root 0", "Child 0-0", "Child 0-1");
data.addItems("Root 1", "Child 1-0", "Child 1-1", "Child 1-2");
data.addItems("Root 2", "Child 2-0");

TreeGrid<String> grid = new TreeGrid<>(new TreeDataProvider<>(data));
grid.setId("two-level-tree-grid");
grid.addHierarchyColumn(s -> s).setHeader("Name");
grid.addColumn(s -> s + " desc").setHeader("Description");
```

Initially only the three root items are visible.

## Expanding and collapsing rows

```java
// Test
TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
grid.assertRowCount(3);                    // only the roots

grid.expandRow(0);                         // Root 0
grid.assertRowCount(5);                    // 3 roots + 2 children
grid.assertCellContent(1, "Name", "Child 0-0");

grid.collapseRow(0);
grid.assertRowCount(3);

grid.toggleRow(0);                         // expands a collapsed row ...
grid.toggleRow(0);                         // ... and collapses an expanded one
```

`expandRow` does nothing on a row that is already expanded or a leaf,
`collapseRow` on a row that is already collapsed or a leaf, and `toggleRow` on a
leaf. Each one waits for the grid to load the new rows.

Because indexes are flat, expanding a row shifts the rows below it:

```java
grid.expandRow(0);      // Root 0 now has its 2 children at indexes 1 and 2
grid.expandRow(3);      // so Root 1 is at index 3
```

## Reading the hierarchy state

```java
grid.expandRow(0);

grid.isRowExpanded(0);       // Root 0 -> true
grid.isRowCollapsed(3);      // Root 1: has children, not expanded -> true
grid.isRowLeaf(1);           // Child 0-0 -> true
grid.getRowLevel(0);         // 0: root items are level 0
grid.getRowLevel(1);         // 1
grid.getExpandedRowCount();  // 1: expanded rows currently rendered
```

These are getters and do not retry. Right after `expandRow` / `collapseRow` they
are safe, since those wait for the grid. For a retrying check after something
else changed the tree, assert on the toggle locator:

```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

var row = grid.findTreeRow(0).orElseThrow();
assertThat(row.getTreeToggleLocator()).hasAttribute("expanded", "");
assertThat(row.getTreeToggleLocator()).not().hasAttribute("expanded", "");   // collapsed
```

## Working with a tree row

`findTreeRow` returns an `Optional<TreeRowElement>`: a `GridElement.RowElement`
with the tree state and actions. It auto-scrolls the grid like `findRow`.

```java
TreeGridElement.TreeRowElement row = grid.findTreeRow(0).orElseThrow();

row.getLevel();        // 0
row.isLeaf();          // false
row.isCollapsed();     // true
row.expand();          // no-op if already expanded or a leaf
row.isExpanded();      // true
row.collapse();        // no-op if already collapsed or a leaf
row.toggle();          // no-op on a leaf

Locator toggle = row.getTreeToggleLocator();   // <vaadin-grid-tree-toggle>
```

Every `RowElement` method is there too: `getCell(int)`, `getCell(String)`,
`select()`, `assertSelected()`, `click()`, `doubleClick()`, `openDetails()`,
`getRowLocator()`, `getRowIndex()`...

```java
row.getCell("Name").assertText("Root 0");
row.select();
row.assertSelected();
```

A `RowElement` obtained from a generic grid helper can be upgraded. `TreeRowElement`
is an inner class of the grid, so create it on the grid instance:

```java
GridElement.RowElement plainRow = grid.findRow(0).orElseThrow();
TreeGridElement.TreeRowElement treeRow = grid.new TreeRowElement(plainRow);
treeRow.getLevel();   // 0
```

## Three levels

```java
// View
data.addRootItems("Division A", "Division B");
data.addItems("Division A", "Dept A1", "Dept A2");
data.addItems("Division B", "Dept B1");
data.addItems("Dept A1", "Employee A1a", "Employee A1b");
data.addItems("Dept A2", "Employee A2a");
data.addItems("Dept B1", "Employee B1a");
```

```java
// Test
TreeGridElement grid = TreeGridElement.getById(page, "three-level-tree-grid");
grid.assertRowCount(2);                 // only the divisions

grid.expandRow(0);                      // Division A
grid.assertRowCount(4);                 // + Dept A1, Dept A2

grid.expandRow(1);                      // Dept A1
grid.assertRowCount(6);                 // + Employee A1a, Employee A1b
grid.assertCellContent(2, "Name", "Employee A1a");

var employee = grid.findTreeRow(2).orElseThrow();
employee.getLevel();                    // 2
employee.isLeaf();                      // true

grid.collapseRow(0);                    // hides all of Division A's descendants
grid.assertRowCount(2);
```

## Selection

Selection is inherited from `GridElement` and uses flat indexes:

```java
TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
grid.select(0);
grid.expandRow(0);
grid.assertRowSelected(0);          // still selected after expanding

grid.select(1);                     // Child 0-0: single select moves the selection
grid.assertRowNotSelected(0);
grid.assertRowSelected(1);
grid.assertSelectedItemCount(1);
```

In multi-select mode Vaadin inserts the checkbox selection column first, pushing
the hierarchy column to index 1. `TreeGridElement` finds the toggle anyway (see
below), and the grid's select-all helpers work as usual:

```java
// View
grid.setSelectionMode(Grid.SelectionMode.MULTI);
```

```java
// Test
TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
grid.expandRow(0);                  // works although the toggle is in column 1
grid.select(0);
grid.select(1);                     // first child of Root 0
grid.assertSelectedItemCount(2);

grid.checkSelectAll();
grid.assertSelectAllChecked();
grid.uncheckSelectAll();
grid.assertSelectedItemCount(0);
```

## How it works

### How tree state is read

`<vaadin-grid-tree-toggle>` exposes three key values, but they are surfaced
differently:

| Value | Reflected as HTML attribute? | How it is read |
|-------|------------------------------|----------------|
| `expanded` | **Yes** | `toggle.getAttribute("expanded") != null` |
| `leaf` | **Yes** | `toggle.getAttribute("leaf") != null` |
| `level` | **No** (DOM property only) | `toggle.evaluate("el => el.level")` |

Because `expanded` and `leaf` are reflected boolean attributes, their presence
means `true` and their absence `false`, and CSS attribute selectors work on them —
`getExpandedRowCount()` simply counts `vaadin-grid-tree-toggle[expanded]` in the
DOM, so it only sees rendered rows. `level` is not reflected, so it is read with
JavaScript.

### How the hierarchy column is located

`TreeGridElement` searches **all cells** of the row for the one whose cell
content holds a `<vaadin-grid-tree-toggle>`, rather than assuming the toggle is
always in cell 0. This handles:

- **Multi-select mode:** Vaadin inserts a checkbox selection column at index 0,
  pushing the hierarchy column to index 1.
- **Column reordering:** the hierarchy column may be at any position.

### Why expand/collapse uses a synthetic click

`<vaadin-grid-cell-content>` intercepts pointer events, so Playwright's
coordinate-based `.click()` never reaches the `<vaadin-grid-tree-toggle>` inside
it. The toggle is clicked with `toggle.evaluate("el => el.click()")`, the same
way Vaadin's TestBench does it with Selenium's `WebElement.click()`. Override the
protected `clickTreeToggle(Locator)` to click it differently.

## Pitfalls

- **Flat indexes.** Row indexes count visible rows; expanding or collapsing a row
  moves every row below it. Re-read indexes after changing the tree.
- **No retrying tree assertions.** `isRowExpanded`, `isRowLeaf`, `getRowLevel`
  and friends read the state once; assert on `getTreeToggleLocator()` with
  Playwright when the state changes asynchronously.
- **`getExpandedRowCount()` counts rendered rows only**, not expanded items that
  are scrolled out of the rendered range.
- **`find` is typed as `GridElement`.** Cast the result to `TreeGridElement`
  for the tree API.
- **`getTotalRowCount()` / `assertRowCount` count visible rows**: children of a
  collapsed row are not counted.

## See also

- [Grid](GridElement.md) — every inherited feature: cells, headers, sorting, details, renderers
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- [Core concepts](../guide/core-concepts.md)
- Full examples: [TreeGridBasicViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TreeGridBasicViewIT.java),
  [TreeGridExpandCollapseViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TreeGridExpandCollapseViewIT.java),
  [TreeGridSelectionViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TreeGridSelectionViewIT.java),
  [TreeGridMultiSelectViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/TreeGridMultiSelectViewIT.java)
