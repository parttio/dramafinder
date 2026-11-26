package org.vaadin.addons.dramafinder.tests.it;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.element.GridElement.SortDirection;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridSortingViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-sorting";
    }

    @Test
    public void testSortableHeaderCellIsNamedByItsSorter() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        assertEquals("Sort by First Name", grid.findHeaderCell(0).orElseThrow().getAccessibleName());
    }

    @Test
    public void testInitiallyUnsorted() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var headerCell = grid.findHeaderCell(0);
        
        assertFalse(headerCell.isEmpty());
        assertTrue(headerCell.get().isNotSorted());
    }

    @Test
    public void testClickHeaderToSortByIndex() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var headerCellO = grid.findHeaderCell(0);

        assertFalse(headerCellO.isEmpty());
        var headerCell = headerCellO.get();

        headerCell.clickSort();
        assertTrue(headerCell.isSortAscending());
        assertFalse(headerCell.isSortDescending());
        assertFalse(headerCell.isNotSorted());

        headerCell.clickSort();
        assertTrue(headerCell.isSortDescending());
        assertFalse(headerCell.isSortAscending());
        assertFalse(headerCell.isNotSorted());

        headerCell.clickSort();
        assertTrue(headerCell.isNotSorted());
        assertFalse(headerCell.isSortDescending());
        assertFalse(headerCell.isSortAscending());
    }

    @Test
    public void testMultiSort() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var headerCellO1 = grid.findHeaderCell(0);
        var headerCellO2 = grid.findHeaderCell(1);

        assertFalse(headerCellO1.isEmpty());
        var headerCell1 = headerCellO1.get();

        assertFalse(headerCellO2.isEmpty());
        var headerCell2 = headerCellO2.get();


        headerCell1.clickSort();
        headerCell2.clickSort();

        assertTrue(headerCell1.isSortAscending());
        assertTrue(headerCell2.isSortAscending());
    }

    @Test
    public void testIsMultiSort() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        assertEquals(true, grid.isMultiSort());
    }

    // ── Assertions ─────────────────────────────────────────────────────

    @Test
    public void testAssertSortDirections() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var headerCell = grid.findHeaderCell(0).get();

        headerCell.assertSortable();
        headerCell.assertNotSorted();

        headerCell.clickSort();
        headerCell.assertSortAscending();

        headerCell.clickSort();
        headerCell.assertSortDescending();

        headerCell.clickSort();
        headerCell.assertNotSorted();
    }

    @Test
    public void testAssertMultiSort() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        grid.assertMultiSort();
    }

    // ── Sort to a direction ────────────────────────────────────────────

    @Test
    public void testSortToEachDirection() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var headerCell = grid.findHeaderCell(0).orElseThrow();
        assertEquals(SortDirection.NONE, headerCell.getSortDirection());

        headerCell.sort(SortDirection.DESCENDING);
        headerCell.assertSortDirection(SortDirection.DESCENDING);
        grid.assertCellContent(0, 0, "First9");

        headerCell.sort(SortDirection.ASCENDING);
        assertEquals(SortDirection.ASCENDING, headerCell.getSortDirection());
        grid.assertCellContent(0, 0, "First1");

        headerCell.sort(SortDirection.NONE);
        headerCell.assertSortDirection(SortDirection.NONE);
        headerCell.assertNotSorted();
    }

    @Test
    public void testSortToCurrentDirectionDoesNothing() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var headerCell = grid.findHeaderCell(0).orElseThrow();
        headerCell.sort(SortDirection.ASCENDING);
        headerCell.sort(SortDirection.ASCENDING);
        headerCell.assertSortAscending();
    }

    @Test
    public void testSortOnUnsortableHeaderFails() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // The joined "Name" cell on top has no sorter
        var nameCell = grid.getHeaderCells(1).get(0);
        assertThrows(IllegalStateException.class, () -> nameCell.sort(SortDirection.ASCENDING));
        assertThrows(IllegalStateException.class, nameCell::getSortDirection);
    }

    // ── Header cells across header rows ────────────────────────────────

    @Test
    public void testHeaderCellsOfColumnAcrossHeaderRows() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // Column 0 is the selection column
        var cells = grid.getHeaderCells(1);
        assertEquals(3, cells.size());
        assertEquals("Name", cells.get(0).getText());
        assertEquals("First Name", cells.get(1).getText());
        assertTrue(cells.get(1).isSortable());
        assertTrue(cells.stream().allMatch(c -> c.getColumnIndex() == 1));

        var lastNameCells = grid.getHeaderCells(2);
        assertEquals(3, lastNameCells.size());
        assertEquals("Name", lastNameCells.get(0).getText());
        assertEquals("Last Name", lastNameCells.get(1).getText());
    }

    @Test
    public void testFilterFieldInHeaderCell() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        var filterCell = grid.getHeaderCells(2).get(2);
        var filter = TextFieldElement.getById(filterCell.getCellContentLocator(), "last-filter");
        filter.setValue("Last1");
        // Last1 and Last10 to Last19
        grid.assertRowCount(11);
    }

    @Test
    public void testHeaderContentLocatorCoversEveryHeaderRow() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // "Name" group, "Last Name" and the filter row
        assertThat(grid.getHeaderContentLocator(2)).hasCount(3);
        TextFieldElement filter = TextFieldElement.getById(grid.getHeaderContentLocator(2), "last-filter");
        filter.setValue("Last1");
        grid.assertRowCount(11);
        // the selection column has its select-all checkbox only
        assertThat(grid.getHeaderContentLocator(0).locator("vaadin-text-field")).hasCount(0);
        // no footer: a locator that matches nothing
        assertThat(grid.getFooterContentLocator(1)).hasCount(0);
        assertThrows(IllegalArgumentException.class, () -> grid.getHeaderContentLocator(9));
    }

    @Test
    public void testColumnsListTheSelectionColumn() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        var columns = grid.getColumns();
        assertEquals(List.of(new GridElement.ColumnInfo(0, null, true),
                new GridElement.ColumnInfo(1, null, false),
                new GridElement.ColumnInfo(2, "last-name-column", false)), columns);
        assertTrue(grid.isSelectionColumn(0));
        assertFalse(grid.isSelectionColumn(2));
        assertThrows(IllegalArgumentException.class, () -> grid.isSelectionColumn(3));
        // Flow's index 1 (no selection column) is this class's index 2
        assertEquals(2, columns.stream().filter(c -> !c.selectionColumn()).toList().get(1).index());

        assertFalse(GridElement.getById(page, "sortable-grid").getColumns().get(0).selectionColumn());
    }

    @Test
    public void testHeaderCellContentsReadTheFirstHeaderRow() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // The selection column, then the joined "Name" cell once for each column it spans
        assertEquals(List.of("", "Name", "Name"), grid.getHeaderCellContents());
    }

    @Test
    public void testRowsUnderSeveralHeaderRows() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // Three header rows: rows are found by index, not by an aria-rowindex offset
        assertEquals("First1", grid.findRow(0).orElseThrow().getCell(1).getText());
        assertEquals("Last20", grid.findRow(19).orElseThrow().getCell(2).getText());
        assertTrue(grid.isRowInView(19));
        assertFalse(grid.isRowInView(0));
        assertTrue(grid.findRow(20).isEmpty());
    }

    @Test
    public void testHeaderCellsOfSingleRowHeader() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        var cells = grid.getHeaderCells(2);
        assertEquals(1, cells.size());
        assertEquals("Email", cells.get(0).getText());
    }

    @Test
    public void testHeaderCellsOfMissingColumnFails() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        assertThrows(IllegalArgumentException.class, () -> grid.getHeaderCells(3));
    }

    // ── Header group row above the captions (issue #299) ───────────────

    @Test
    public void testColumnCaptionsUnderGroupRow() {
        GridElement grid = GridElement.getById(page, "grouped-grid");
        assertEquals(List.of("Id", "First Name", "Last Name", "Email", "City"), grid.getColumnCaptions());
    }

    @Test
    public void testColumnCaptionsSkipFilterRowAndSelectionColumn() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // The selection column has no caption; the filter row below the captions is blank
        assertEquals(List.of("", "First Name", "Last Name"), grid.getColumnCaptions());
    }

    @Test
    public void testFindHeaderCellAfterJoinedCell() {
        GridElement grid = GridElement.getById(page, "grouped-grid");
        // Without a row: the column's caption cell
        assertEquals("City", grid.findHeaderCell(4).orElseThrow().getText());
        assertEquals("Last Name", grid.findHeaderCell(2).orElseThrow().getText());
        // The group row has four cells for five columns
        var nameGroup = grid.findHeaderCell(0, 2).orElseThrow();
        assertEquals("Name", nameGroup.getText());
        assertEquals(2, nameGroup.getColumnIndex());
        assertEquals("", grid.findHeaderCell(0, 4).orElseThrow().getText());
        assertEquals("Email", grid.findHeaderCell(1, 3).orElseThrow().getText());
        assertTrue(grid.findHeaderCell(2, 3).isEmpty());
        assertTrue(grid.findHeaderCell(5).isEmpty());
    }

    @Test
    public void testSortColumnAfterJoinedCell() {
        GridElement grid = GridElement.getById(page, "grouped-grid");
        var city = grid.findHeaderCell(4).orElseThrow();
        city.sort(SortDirection.DESCENDING);
        city.assertSortDirection(SortDirection.DESCENDING);
        assertEquals("City9", grid.findCell(0, 4).orElseThrow().getText());
    }

    @Test
    public void testAssertHeaderCellReadsCaptions() {
        GridElement grid = GridElement.getById(page, "grouped-grid");
        grid.assertHeaderCell(1, "First Name");
        grid.assertHeaderCell(4, "City");
    }

    @Test
    public void testFindHeaderCellByTextReportsColumnIndex() {
        GridElement grid = GridElement.getById(page, "grouped-grid");
        // Without a row: matched against the captions
        assertEquals(4, grid.findHeaderCellByText("City").orElseThrow().getColumnIndex());
        assertTrue(grid.findHeaderCellByText("Name").isEmpty());
        // In the group row: the first column the joined cell spans
        assertEquals(1, grid.findHeaderCellByText(0, "Name").orElseThrow().getColumnIndex());
        assertEquals(3, grid.findHeaderCellByText(1, "Email").orElseThrow().getColumnIndex());
        assertTrue(grid.findHeaderCellByText(0, "Email").isEmpty());
    }

    @Test
    public void testLookupsByCaptionUnderGroupRow() {
        GridElement grid = GridElement.getById(page, "grouped-grid");
        assertEquals("City1", grid.findCell(0, "City").orElseThrow().getText());
        assertEquals("Last1", grid.findRow(0).orElseThrow().getCell("Last Name").getText());
        grid.assertCellContent(0, "Email", "person1@example.com");
        grid.assertColumnPresent("City");
        grid.assertColumnNotPresent("Name");
        var failure = assertThrows(AssertionError.class, () -> grid.assertColumnPresent("Country"));
        assertTrue(failure.getMessage().contains("City"), failure.getMessage());
    }
}
