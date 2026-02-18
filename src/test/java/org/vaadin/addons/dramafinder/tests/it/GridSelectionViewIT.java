package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridSelectionViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-selection";
    }

    // ── Single Selection ───────────────────────────────────────────────

    @Test
    public void testSingleSelectRow() {
        GridElement grid = GridElement.getById(page, "single-select-grid");
        grid.assertRowNotSelected(0);

        grid.selectRow(0);

        grid.assertRowSelected(0);
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testSingleSelectChangeSelection() {
        GridElement grid = GridElement.getById(page, "single-select-grid");

        grid.selectRow(0);
        grid.assertRowSelected(0);

        grid.selectRow(1);
        grid.assertRowSelected(1);
        grid.assertRowNotSelected(0);
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testSingleSelectDeselectAll() {
        GridElement grid = GridElement.getById(page, "single-select-grid");

        grid.selectRow(0);
        grid.assertRowSelected(0);

        grid.deselectAll();
        grid.assertRowNotSelected(0);
        grid.assertSelectedItemCount(0);
    }

    // ── Multi Selection ────────────────────────────────────────────────

    @Test
    public void testMultiSelectToggleRow() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        grid.toggleRowSelection(0);
        grid.assertRowSelected(0);
        grid.assertSelectedItemCount(1);

        grid.toggleRowSelection(1);
        grid.assertRowSelected(1);
        grid.assertSelectedItemCount(2);
    }

    @Test
    public void testMultiSelectDeselectRow() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        grid.toggleRowSelection(0);
        grid.assertRowSelected(0);

        grid.toggleRowSelection(0);
        grid.assertRowNotSelected(0);
        grid.assertSelectedItemCount(0);
    }

    @Test
    public void testMultiSelectSelectAll() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        grid.toggleSelectAll();
        grid.assertSelectedItemCount(20);
        assertTrue(grid.isRowSelected(0));
        assertTrue(grid.isRowSelected(1));
    }

    @Test
    public void testMultiSelectDeselectAll() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        grid.toggleSelectAll();
        grid.assertSelectedItemCount(20);

        grid.deselectAll();
        grid.assertSelectedItemCount(0);
        assertFalse(grid.isRowSelected(0));
    }

    @Test
    public void testIsRowSelected() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");
        assertFalse(grid.isRowSelected(0));

        grid.toggleRowSelection(0);
        assertTrue(grid.isRowSelected(0));
        assertFalse(grid.isRowSelected(1));
    }
}
