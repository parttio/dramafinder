package org.vaadin.addons.dramafinder.tests.it;

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
    public void testHeaderCellContentsReadTheFirstHeaderRow() {
        GridElement grid = GridElement.getById(page, "filter-grid");
        // The selection column, then the joined "Name" cell once for each column it spans
        assertEquals(List.of("", "Name", "Name"), grid.getHeaderCellContents());
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
}
