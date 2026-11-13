# Grid

`GridElement` wraps `<vaadin-grid>` (Flow: `Grid`). It reads and asserts rows,
cells, headers and footers, selects rows, sorts columns and opens row details.
Row and cell access **auto-scrolls** the grid, so a test reaches row 9 000 of a
lazy grid the same way it reaches row 0.

| | |
|---|---|
| Web component | `<vaadin-grid>` |
| Flow component | `com.vaadin.flow.component.grid.Grid` |
| Wrapper | `org.vaadin.addons.dramafinder.element.GridElement` |
| Typed query | `find(page, GridElement.class)` → `ComponentQuery` |
| API | [GridElement](../../skills/vaadin-playwright-test/api-reference.md#gridelement) |

Rows, cells and header cells come back as inner-class elements (`RowElement`,
`CellElement`, `HeaderCellElement`, `FooterCellElement`), usually wrapped in an
`Optional`, so a test works on a row or a cell directly instead of going back
through the grid each time.

!!! warning "Use a row or cell before looking up another one"

    The grid is virtualized: it renders only the rows in view and recycles their
    DOM elements while scrolling. Interact with a `RowElement` or `CellElement`
    before searching for another row, because finding a distant row scrolls the
    grid and may hide or reuse the element you still hold.

## Finding the component

A grid has no label, so the factories key on position, id or test id:

```java
GridElement grid = GridElement.getById(page, "basic-grid");          // id="basic-grid"
GridElement first = GridElement.get(page);                            // first grid on the page
GridElement scoped = GridElement.get(page.locator("#orders-panel"));  // first grid inside a container
GridElement byTestId = GridElement.getByTestId(page, "orders");
GridElement inPanel = GridElement.getById(page.locator("#orders-panel"), "orders-grid");
```

The typed query offers the filters every component has (id, test id, attribute,
class name, accessible name):

```java
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

GridElement grid = find(page, GridElement.class).withClassName("styled-grid").single();
GridElement inDialog = dialog.find(GridElement.class).first();
```

## Row and column counts

```java
// View
Grid<GridPerson> grid = new Grid<>();
grid.setId("basic-grid");
grid.setHeight("400px");
grid.addColumn(GridPerson::firstName).setHeader("First Name");
grid.addColumn(GridPerson::lastName).setHeader("Last Name");
grid.addColumn(GridPerson::email).setHeader("Email");
grid.setItems(generatePersons(100));
```

```java
// Test
GridElement grid = GridElement.getById(page, "basic-grid");
grid.assertRowCount(100);     // total data items, not rendered rows
grid.assertColumnCount(3);    // visible (non-hidden) columns

GridElement.getById(page, "empty-grid").assertEmpty();
```

A failed `assertRowCount` reports both numbers:
`Grid row count mismatch. Expected: <12>, actual: <100>`.

The getters are there when the number itself is needed:

| Getter | Returns |
|--------|---------|
| `getTotalRowCount()` | Total number of data items |
| `getRenderedRowCount()` | Number of rows currently rendered in the DOM (less than the total in a virtualized grid) |
| `getColumnCount()` | Number of visible (non-hidden) columns |

```java
int pageSize = grid.getRenderedRowCount(); // e.g. to scroll a page at a time
```

## Reading cells

Look a cell up by row and column index, or by row and header text. Both
auto-scroll the row into view. `assertCellContent` is the retrying one-liner:

```java
GridElement grid = GridElement.getById(page, "basic-grid");

grid.assertCellContent(0, 0, "First1");
grid.assertCellContent(0, "Email", "person1@example.com");
grid.assertCellPresent(0, 0);
grid.assertCellNotPresent(0, 3);   // only three columns
```

`findCell` returns an `Optional<CellElement>` to work with the cell itself:

```java
var cell = grid.findCell(0, "Last Name").orElseThrow();
cell.assertText("Last1");                    // retrying
String text = cell.getText();                // rendered text, when the value is needed
Locator content = cell.getCellContentLocator();  // <vaadin-grid-cell-content>
Locator td = cell.getTableCellLocator();         // the <td>/<th> in the shadow DOM
cell.click();                                    // clicks the cell content
```

| `CellElement` method | Description |
|--------|-------------|
| `getTableCellLocator()` | Locator for the table cell (`<td>` or `<th>`) |
| `getCellContentLocator()` | Locator for the `<vaadin-grid-cell-content>` slotted into the cell |
| `getText()` / `assertText(String)` | Rendered text of the cell content |
| `getColumnIndex()` | 0-based column index; `-1` for details cells |
| `getContentSlotName()` | Name of the slot the cell content is assigned to |
| `click()` | Click the cell content |

## Column indexes

Every `columnIndex` / `column` parameter counts the **visible** columns in
**display order**, starting from 0 on the left:

- hidden columns (`setVisible(false)`) are skipped;
- a selection column (multi-select) is a column and counts;
- after the user drags a column to a new place, the indexes follow the new
  order — the same order as TestBench's `getVisibleColumns()`.

This matches TestBench, whose `getCell(row, colIndex)` and `getHeaderCell(colIndex)`
count the selection column too. Flow's `Grid.getColumns()` and browserless tests
(`GridTester`) do **not** list the selection column, so in a multi-select grid a
Flow column index is one less than the `GridElement` index (the selection column
is first unless the columns were reordered). To stay independent of the selection
mode, look a column up by header text (`findCell(row, "Email")`) or by id
(`getColumnIndexById`) instead of hard-coding its index.

A column with no header text cannot be found with `findHeaderCellByText`; give it
an id in Flow (`Column.setId("email")`, which lands on the `<vaadin-grid-column>`)
and look it up by that id. `Column.setKey(..)` stays on the server and is not
visible to the browser.

```java
// View
grid.addColumn(GridPerson::firstName).setHeader("First Name").setId("first-name");
var lastName = grid.addColumn(GridPerson::lastName).setHeader("Last Name");
lastName.setId("last-name");
lastName.setVisible(false);
grid.addColumn(GridPerson::email).setId("email");               // no header text
grid.addColumn(p -> "key " + p.firstName()).setHeader("Key Only").setKey("key-only");
```

```java
// Test
GridElement grid = GridElement.getById(page, "columns-grid");

int email = grid.getColumnIndexById("email");          // 1: the hidden "last-name" is skipped
grid.assertCellContent(0, email, "person1@example.com");

grid.findColumnIndexById("last-name");   // OptionalInt.empty(): hidden
grid.findColumnIndexById("key-only");    // OptionalInt.empty(): a key is not an id
```

`findColumnIndexById` returns an empty `OptionalInt` when no visible column has
the id; `getColumnIndexById` throws `IllegalArgumentException` instead. Both match
the id exactly and reject an empty or `null` id.

## Headers

```java
GridElement grid = GridElement.getById(page, "basic-grid");

grid.assertHeaderCellContents("First Name", "Last Name", "Email");  // all, in order
grid.assertHeaderCell(0, "First Name");
grid.assertColumnPresent("Email");
grid.assertColumnNotPresent("Phone");
```

A failed `assertColumnPresent` lists the headers the grid does have, e.g.
`[First Name, Last Name, Email]`.

All `findHeaderCell*` methods return `Optional<HeaderCellElement>`:

| Method | Description |
|--------|-------------|
| `findHeaderCell(int columnIndex)` | Header cell by column index in the first header row |
| `findHeaderCell(int headerRowIndex, int columnIndex)` | Header cell by header row index and column index |
| `findHeaderCellByText(String text)` | Header cell by text content in the first header row |
| `findHeaderCellByText(int headerRowIndex, String text)` | Header cell by header row index and text content |
| `getHeaderCellContents()` | `List<String>`: text content of all visible header cells |
| `getHeaderCells(int columnIndex)` | `List<HeaderCellElement>`: the column's header cells in every header row, top row first |

`findHeaderCellByText` matches the whole header text exactly (the cell content's
`innerText`). Override the protected `matchesHeaderCell(CellElement, String)` to
match differently.

```java
var header = grid.findHeaderCellByText("Last Name").orElseThrow();
int column = header.getColumnIndex();   // 1
```

### Components in a header

A header set with `setHeader(Component)` holds a real component; scope a lookup
to the header cell content:

```java
// View
TextField filterField = new TextField();
filterField.setPlaceholder("Filter email...");
grid.addColumn(GridPerson::email).setHeader(filterField);
```

```java
// Test
GridElement grid = GridElement.getById(page, "component-header-grid");
Locator header = grid.findHeaderCell(2).orElseThrow().getCellContentLocator();
TextFieldElement filter = find(header, TextFieldElement.class)
        .withPlaceholder("Filter email...").single();
filter.setValue("test");
```

### Several header rows

`getHeaderCells` returns the cell of the column itself and those of the column
groups around it, which is how the web component renders Flow's extra
`HeaderRow`s: a joined cell above (returned for each column it spans) or a filter
field in an appended row below. It throws `IllegalArgumentException` for a column
index with no column.

```java
// View: joined "Name" row on top, sorters in the middle, filter fields below
Grid<GridPerson> grid = new Grid<>();
grid.setId("filter-grid");
grid.setSelectionMode(Grid.SelectionMode.MULTI);
var firstName = grid.addColumn(GridPerson::firstName).setHeader("First Name").setSortable(true);
var lastName = grid.addColumn(GridPerson::lastName).setHeader("Last Name").setSortable(true);
HeaderRow filterRow = grid.appendHeaderRow();
filterRow.getCell(lastName).setComponent(lastNameFilter);   // TextField with id "last-filter"
grid.prependHeaderRow().join(firstName, lastName).setText("Name");
```

```java
// Test: column 0 is the selection column, so "Last Name" is column 2
GridElement grid = GridElement.getById(page, "filter-grid");
var cells = grid.getHeaderCells(2);   // ["Name", "Last Name", <filter>]
cells.get(0).assertText("Name");
cells.get(1).assertText("Last Name");

TextFieldElement filter = TextFieldElement.getById(cells.get(2).getCellContentLocator(), "last-filter");
filter.setValue("Last1");
grid.assertRowCount(11);   // Last1 and Last10 to Last19
```

When the column has an id (`Column.setId("last-name")`),
`grid.getHeaderCells(grid.getColumnIndexById("last-name"))` keeps the test
independent of the selection mode.

## Footers

```java
// View
grid.addColumn(GridPerson::firstName).setHeader("First Name").setFooter("Total: " + persons.size());
grid.addColumn(GridPerson::lastName).setHeader("Last Name").setFooter("Last names");
grid.addColumn(GridPerson::email).setHeader("Email").setFooter("Emails");
```

```java
// Test
GridElement grid = GridElement.getById(page, "footer-grid");
grid.assertFooterCellContents("Total: 50", "Last names", "Emails");
grid.assertFooterCell(1, "Last names");
grid.assertFooterPresent("Emails");
grid.assertFooterNotPresent("Does not exist");

grid.findFooterCell(0).orElseThrow().assertText("Total: 50");
grid.findFooterCellByText("Emails").isPresent();             // true
grid.findFooterCellByText("Does not exist").isEmpty();       // true
```

All `findFooterCell*` methods return `Optional<FooterCellElement>`.
`FooterCellElement` adds no methods to `CellElement`; it is a distinct type
returned by the footer methods.

| Method | Description |
|--------|-------------|
| `findFooterCell(int columnIndex)` | Footer cell by column index in the first footer row |
| `findFooterCell(int footerRowIndex, int columnIndex)` | Footer cell by footer row index and column index |
| `findFooterCellByText(String text)` | Footer cell by text content in the first footer row |
| `findFooterCellByText(int footerRowIndex, String text)` | Footer cell by footer row index and text content |
| `getFooterCellContents()` | `List<String>`: text content of all visible footer cells |

## Rows

`findRow` returns an `Optional<RowElement>` and **auto-scrolls** to the row:

```java
GridElement grid = GridElement.getById(page, "basic-grid");
grid.assertRowPresent(99);       // auto-scrolls if necessary
grid.assertRowNotPresent(100);

var row = grid.findRow(0).orElseThrow();
row.getCell(0).assertText("First1");
row.getCell("Email").assertText("person1@example.com");
int index = row.getRowIndex();       // 0
Locator tr = row.getRowLocator();    // the <tr>
```

### Clicking and double-clicking a row

`click()` and `doubleClick()` fire the grid's item click and item double-click
listeners like a user would:

```java
// View
grid.addItemClickListener(e -> itemClicked.setText("Clicked: " + e.getItem().firstName()));
grid.addItemDoubleClickListener(e -> itemDoubleClicked.setText("Double-clicked: " + e.getItem().firstName()));
```

```java
// Test
GridElement grid = GridElement.getById(page, "click-grid");
grid.findRow(1).orElseThrow().click();
assertThat(page.locator("#item-clicked")).hasText("Clicked: First2");

grid.findRow(2).orElseThrow().doubleClick();
assertThat(page.locator("#item-double-clicked")).hasText("Double-clicked: First3");
```

They click the first data cell, skipping the selection column, at a spot free of
interactive content (usually the padding), so a button or link rendered in the
cell is not triggered; when interactive content covers a cell entirely they move
on to the next one, and throw an `IllegalStateException` when no cell has a free
spot.

## Searching across rows

```java
GridElement grid = GridElement.getById(page, "small-lazy-grid");

List<Integer> rows = grid.findRowIndexesWithColumnText(1, "Last250");   // [249]
grid.findRowIndexesWithColumnText(1, "Last2500");                       // []
grid.assertRowIndexesWithColumnText(1, "Last250", 249);                 // retrying

List<String> firstNames = grid.getColumnTexts(0);   // "First1" ... "First300", indexed by row
```

A cell matches when its rendered text **equals** the given text. To customise
how a cell matches (ignoring case, a part of the text), override the protected
`matchesCellText(String cellText, String text)`.

`getColumnTexts` (and `findRowIndexesWithColumnText`, built on it) reads a lazy
grid a **page** at a time: one script reads every rendered row, then the grid
jumps to the first unread row and waits for it to load. That is one scroll per
page instead of several round trips per row, so prefer it over looping on
`findCell(row, column)`. It returns an empty list for an empty grid and throws
`IllegalArgumentException` for a negative column index.

## Selection

### Single selection

```java
// View
grid.setSelectionMode(Grid.SelectionMode.SINGLE);
```

```java
// Test
GridElement grid = GridElement.getById(page, "single-select-grid");

grid.assertRowNotSelected(0);
grid.select(0);
grid.assertRowSelected(0);
grid.assertSelectedItemCount(1);

grid.select(1);                 // moves the selection
grid.assertRowNotSelected(0);
grid.assertRowSelected(1);
```

The same on a `RowElement`:

```java
var row = grid.findRow(0).orElseThrow();
row.assertNotSelected();
row.select();
row.assertSelected();
row.deselect();
row.assertNotSelected();
```

### Multi selection and select all

```java
// View
grid.setSelectionMode(Grid.SelectionMode.MULTI);
grid.setItems(GridBasicView.generatePersons(20));
```

```java
// Test
GridElement grid = GridElement.getById(page, "multi-select-grid");

grid.assertSelectAllUnchecked();
grid.select(0);
grid.select(1);
grid.assertSelectedItemCount(2);
grid.assertSelectAllIndeterminate();

grid.checkSelectAll();          // no-op if already checked
grid.assertSelectAllChecked();
grid.assertSelectedItemCount(20);

grid.uncheckSelectAll();        // no-op if already unchecked
grid.assertSelectAllUnchecked();
grid.assertSelectedItemCount(0);
```

Getters for the same state: `getSelectedItemCount()`, `isSelectAllChecked()`,
`isSelectAllIndeterminate()`, `isSelectAllUnchecked()` and `RowElement.isSelected()`.

### How a row gets selected

`select` and `deselect` click the row like a user would. In a multi-select grid
they click the selection checkbox. Otherwise they click the first cell that has a
spot where the click reaches the grid rather than a component rendered in the
cell, usually the cell padding: the grid ignores clicks on links, buttons and
other focusable content, so clicking the centre of a cell holding a link would
follow the link instead of selecting the row. A cell holding a link or a button
therefore still selects the row through its padding. Only when interactive content
covers every cell, padding included (for example a component with negative
margins, or a theme without cell padding), do they throw an
`IllegalStateException`; override the protected
`clickRowForSelection(RowElement)` or `clickCellForSelection(CellElement)` to
select such a row another way.

```java
// A grid whose first column renders a RouterLink: select() clicks the padding,
// so the row is selected and the page does not navigate
GridElement grid = GridElement.getById(page, "router-link-grid");
grid.select(0);
grid.assertRowSelected(0);
```

They also check the outcome. `select(int)` and `deselect(int)` throw an
`IllegalArgumentException` when there is no row at the index, and every select and
deselect fails with an `AssertionError` when the row's selected state didn't
change after the click: a grid with no selection (`SelectionMode.NONE`), a
single-select grid that doesn't allow deselecting
(`setDeselectAllowed(false)`), or a server that rejected the selection.

## Sorting

```java
// View
grid.setMultiSort(true);
grid.addColumn(GridPerson::firstName).setHeader("First Name").setSortable(true);
grid.addColumn(GridPerson::lastName).setHeader("Last Name").setSortable(true);
```

```java
// Test
GridElement grid = GridElement.getById(page, "sortable-grid");
grid.assertMultiSort();

var header = grid.findHeaderCellByText("First Name").orElseThrow();
header.assertSortable();
header.assertNotSorted();

header.clickSort();               // cycles none → ascending → descending → none
header.assertSortAscending();
header.clickSort();
header.assertSortDescending();
header.clickSort();
header.assertNotSorted();
```

`sort(SortDirection)` goes straight to a direction: it clicks the sorter (at most
twice) until it shows that direction, waiting for the grid to load after each
click, and does nothing if the column is already sorted that way:

```java
import org.vaadin.addons.dramafinder.element.GridElement.SortDirection;

header.sort(SortDirection.DESCENDING);
header.assertSortDirection(SortDirection.DESCENDING);
grid.assertCellContent(0, 0, "First9");   // "First9" sorts last as text

header.sort(SortDirection.NONE);
header.assertSortDirection(SortDirection.NONE);   // same as assertNotSorted()

SortDirection current = header.getSortDirection();  // NONE when unsorted
```

With multi-sort, sorting a second column keeps the first one sorted:

```java
var firstName = grid.findHeaderCell(0).orElseThrow();
var lastName = grid.findHeaderCell(1).orElseThrow();
firstName.clickSort();
lastName.clickSort();
firstName.assertSortAscending();
lastName.assertSortAscending();
```

| `HeaderCellElement` method | Description |
|--------|-------------|
| `isSortable()` / `assertSortable()` / `assertNotSortable()` | Whether the column has a sorter |
| `clickSort()` | Click the sorter to cycle the sort direction |
| `sort(SortDirection direction)` | Click the sorter until it shows `direction` (`ASCENDING`, `DESCENDING`, `NONE`) |
| `getSortDirection()` / `assertSortDirection(SortDirection)` | Current direction; `NONE` when unsorted |
| `isSortAscending()` / `assertSortAscending()` | Sorted ascending |
| `isSortDescending()` / `assertSortDescending()` | Sorted descending |
| `isNotSorted()` / `assertNotSorted()` | Not sorted |

`sort`, `getSortDirection` and `clickSort` throw `IllegalStateException` on a
header cell without a sorter, such as a joined group cell.

## Row details

```java
// View
grid.setItemDetailsRenderer(new ComponentRenderer<>(person -> {
    Span span = new Span("Details: " + person.email());
    span.addClassName("detail-content");
    return span;
}));
grid.setDetailsVisibleOnClick(true);   // the default
```

```java
// Test
GridElement grid = GridElement.getById(page, "details-grid");
grid.assertDetailsClosed(0);

var row = grid.findRow(0).orElseThrow();
row.openDetails();
row.assertDetailsOpen();
grid.assertDetailsOpen(0);
assertThat(row.getDetailsCell().getCellContentLocator()).containsText("Details: person1@example.com");

row.closeDetails();
row.assertDetailsClosed();
```

In the default single selection mode, opening the details of another row moves
both the selection and the details: row 0's details close.

`openDetails()` and `closeDetails()` click the row the same way as `click()`
(skipping the selection column and any button or link in a cell), and rely on the
grid toggling details on click, Flow's `Grid.setDetailsVisibleOnClick(true)` (the
default). They fail with an `AssertionError` naming that setting when the details
didn't open or close; override the protected `openDetails(RowElement)` /
`closeDetails(RowElement)` when the details are toggled another way.
`RowElement.isDetailsOpen()` is the getter.

## Renderers

### Component renderer

Scope a lookup to the cell content to reach the component a
`ComponentRenderer` put there:

```java
// View
grid.addColumn(new ComponentRenderer<>(person -> {
    Button button = new Button("Click " + person.firstName());
    button.addClickListener(e -> clickOutput.setText("Clicked: " + person.firstName()));
    return button;
})).setHeader("Action");
```

```java
// Test
GridElement grid = GridElement.getById(page, "component-grid");
Locator content = grid.findCell(0, "Action").orElseThrow().getCellContentLocator();
ButtonElement button = ButtonElement.getByText(content, "Click First1");
button.click();
assertThat(page.locator("#click-output")).hasText("Clicked: First1");
```

### LitRenderer

A `LitRenderer` renders plain DOM into the cell content, so use Playwright
locators on it:

```java
// View
grid.addColumn(LitRenderer.<GridPerson>of("<span class='badge'>${item.firstName}</span>")
        .withProperty("firstName", GridPerson::firstName)).setHeader("First Name");
grid.addColumn(LitRenderer.<GridPerson>of(
                "<vaadin-button @click='${handleClick}'>Action ${item.firstName}</vaadin-button>")
        .withProperty("firstName", GridPerson::firstName)
        .withFunction("handleClick", p -> clickOutput.setText("LitClicked: " + p.firstName())))
        .setHeader("Action");
```

```java
// Test
GridElement grid = GridElement.getById(page, "lit-renderer-grid");

Locator firstName = grid.findCell(0, "First Name").orElseThrow().getCellContentLocator();
assertThat(firstName.locator(".badge")).hasText("First1");

Locator action = grid.findCell(0, "Action").orElseThrow().getCellContentLocator();
ButtonElement button = new ButtonElement(action.locator("vaadin-button"));
assertThat(button.getLocator()).hasText("Action First1");
button.click();
assertThat(page.locator("#lit-click-output")).hasText("LitClicked: First1");
```

## Lazy loading and scrolling

```java
// View
grid.setDataProvider(DataProvider.fromCallbacks(
        query -> IntStream.rangeClosed(query.getOffset() + 1, query.getOffset() + query.getLimit())
                .mapToObj(i -> new GridPerson("First" + i, "Last" + i, "person" + i + "@example.com")),
        query -> 10000));
```

```java
// Test
GridElement grid = GridElement.getById(page, "lazy-grid");
grid.assertRowCount(10000);

// findRow auto-scrolls to row 9000 and waits for it to load
var row = grid.findRow(9000).orElseThrow();
row.getCell(0).assertText("First9001");
```

To check what the user sees, read the viewport without scrolling:

```java
GridElement grid = GridElement.getById(page, "basic-grid");
grid.assertRowInView(0);
grid.assertRowNotInView(99);
grid.assertFirstVisibleRow(0);

grid.scrollToEnd();
grid.assertLastVisibleRow(99);
grid.scrollToStart();
grid.assertFirstVisibleRow(0);

grid.scrollToRow(50);                  // waits for the row to be rendered
grid.assertRowInView(50);
```

`isRowInView`, `getFirstVisibleRowIndex` and `getLastVisibleRowIndex` are the
getters; a row is "in view" when it is at least partially visible between the
header and the footer. The index getters return `-1` when no row is in view (an
empty grid), and `assertFirstVisibleRow(-1)` / `assertLastVisibleRow(-1)` assert
that.

`waitForGridToStopLoading()` waits for the grid to finish loading after a scroll
or another action that loads rows. The grid's own actions already call it; use it
after driving the grid by hand (a mouse drag, a script). It returns at once when
the grid is no longer in the DOM, e.g. because a click on a cell navigated to
another view.

## Grid settings

```java
GridElement grid = GridElement.getById(page, "all-rows-visible-grid");
grid.assertAllRowsVisible();               // setAllRowsVisible(true): no vertical scroll
grid.assertColumnReorderingAllowed();      // setColumnReorderingAllowed(true)

GridElement other = GridElement.getById(page, "basic-grid");
other.assertNotAllRowsVisible();
other.assertNotMultiSort();
other.assertColumnReorderingNotAllowed();
```

Getters: `isAllRowsVisible()`, `isMultiSort()`, `isColumnReorderingAllowed()`.

## Styling and theme variants

```java
// View
grid.addClassName("styled-grid");
grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
```

```java
// Test
grid.assertCssClass("styled-grid");
grid.assertHasThemeVariant("row-stripes");   // one variant among several
grid.assertTheme("compact row-stripes");     // the whole attribute, in any order
```

`GridElement` also implements `FocusableElement` (`focus()`, `assertIsFocused()`)
and `HasEnabledElement` (`assertEnabled()`, `assertDisabled()`), both read on the
`<vaadin-grid>` host.

## Pitfalls

- **Stale rows.** Use a `RowElement` or `CellElement` before looking up another
  row: the lookup may scroll the virtualized grid and recycle the element you
  hold.
- **Selection column counts.** In a multi-select grid column 0 is the checkbox
  column, unlike Flow's `Grid.getColumns()`. Look columns up by header text or id.
- **A key is not an id.** `Column.setKey(..)` never reaches the browser; use
  `Column.setId(..)` to find a column without header text.
- **Exact text.** `findHeaderCellByText`, `findFooterCellByText`,
  `findRowIndexesWithColumnText` and the `assert*` text helpers compare the whole
  text. Override `matchesHeaderCell`, `matchesFooterCell` or `matchesCellText` for
  other matching.
- **Select/deselect check the outcome.** They fail with an `AssertionError` when
  the selected state does not change (no selection mode, deselect not allowed,
  server rejected it), with `IllegalArgumentException` when the row does not
  exist, and with `IllegalStateException` when interactive content covers every
  cell.
- **Details need `setDetailsVisibleOnClick(true)`.** Otherwise `openDetails()` /
  `closeDetails()` fail; override `openDetails(RowElement)` /
  `closeDetails(RowElement)`.
- **Sorting a group cell.** `sort`, `clickSort` and `getSortDirection` throw
  `IllegalStateException` on a header cell without a sorter.

## See also

- [Tree Grid](TreeGridElement.md) — the same grid with a hierarchy column
- [Virtual List](VirtualListElement.md) — a virtualized list without columns
- [Assertions](../guide/assertions.md)
- [Finding components](../guide/finding-components.md)
- [Actions and waiting](../guide/actions-and-waiting.md)
- [Core concepts](../guide/core-concepts.md)
- Full examples: [GridBasicViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridBasicViewIT.java),
  [GridSelectionViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridSelectionViewIT.java),
  [GridSortingViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridSortingViewIT.java),
  [GridDetailsViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridDetailsViewIT.java),
  [GridColumnsViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridColumnsViewIT.java),
  [GridFooterViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridFooterViewIT.java),
  [GridComponentRendererViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridComponentRendererViewIT.java),
  [GridLitRendererViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridLitRendererViewIT.java),
  [GridNavigationViewIT](../../src/test/java/org/vaadin/addons/dramafinder/tests/it/GridNavigationViewIT.java)
