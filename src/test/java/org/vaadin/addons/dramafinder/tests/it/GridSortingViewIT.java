package org.vaadin.addons.dramafinder.tests.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

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
}
