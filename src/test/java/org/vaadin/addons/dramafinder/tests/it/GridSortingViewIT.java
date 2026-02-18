package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridSortingViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-sorting";
    }

    @Test
    public void testInitiallyUnsorted() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        assertNull(grid.getSortDirection(0));
        grid.assertNotSorted(0);
    }

    @Test
    public void testClickHeaderToSortByIndex() {
        GridElement grid = GridElement.getById(page, "sortable-grid");

        grid.clickHeaderToSort(0);
        grid.assertSortDirection(0, "asc");

        grid.clickHeaderToSort(0);
        grid.assertSortDirection(0, "desc");

        grid.clickHeaderToSort(0);
        grid.assertNotSorted(0);
    }

    @Test
    public void testClickHeaderToSortByText() {
        GridElement grid = GridElement.getById(page, "sortable-grid");

        grid.clickHeaderToSort("Last Name");
        grid.assertSortDirection("Last Name", "asc");

        grid.clickHeaderToSort("Last Name");
        grid.assertSortDirection("Last Name", "desc");

        grid.clickHeaderToSort("Last Name");
        grid.assertNotSorted(1);
    }

    @Test
    public void testGetSortDirectionByText() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        assertNull(grid.getSortDirection("First Name"));

        grid.clickHeaderToSort("First Name");
        assertEquals("asc", grid.getSortDirection("First Name"));
    }

    @Test
    public void testMultiSort() {
        GridElement grid = GridElement.getById(page, "sortable-grid");

        grid.clickHeaderToSort(0);
        grid.assertSortDirection(0, "asc");

        grid.clickHeaderToSort(1);
        grid.assertSortDirection(1, "asc");
        // First column should still be sorted in multi-sort mode
        grid.assertSortDirection(0, "asc");
    }

    @Test
    public void testIsMultiSort() {
        GridElement grid = GridElement.getById(page, "sortable-grid");
        assertEquals(true, grid.isMultiSort());
    }
}
