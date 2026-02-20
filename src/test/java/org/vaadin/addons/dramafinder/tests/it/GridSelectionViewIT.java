package org.vaadin.addons.dramafinder.tests.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

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
        var row = grid.findRow(0).get();
        assertFalse(row.isSelected());
        row.select();
        assertTrue(row.isSelected());

        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testSingleSelectChangeSelection() {
        GridElement grid = GridElement.getById(page, "single-select-grid");

        var row = grid.findRow(0).get();
        assertFalse(row.isSelected());
        row.select();
        assertTrue(row.isSelected());

        var row2 = grid.findRow(1).get();
        row2.select();
        assertTrue(row2.isSelected());
        assertFalse(row.isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    // ── Multi Selection ────────────────────────────────────────────────

    @Test
    public void testMultiSelectSelectAll() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        var row1 = grid.findRow(0).get();
        var row2 = grid.findRow(1).get();
        var row3 = grid.findRow(2).get();
        row1.select();
        assertTrue(row1.isSelected());
        assertFalse(row2.isSelected());
        assertFalse(row3.isSelected());
        assertEquals(1, grid.getSelectedItemCount());

        grid.checkSelectAll();

        assertTrue(row1.isSelected());
        assertTrue(row2.isSelected());
        assertTrue(row3.isSelected());
        assertEquals(20, grid.getSelectedItemCount());
    }

    @Test
    public void testMultiSelectDeselectAll() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        var row1 = grid.findRow(0).get();
        var row2 = grid.findRow(1).get();
        var row3 = grid.findRow(2).get();
        row1.select();
        assertTrue(row1.isSelected());
        assertFalse(row2.isSelected());
        assertFalse(row3.isSelected());
        assertEquals(1, grid.getSelectedItemCount());

        grid.checkSelectAll();
        grid.uncheckSelectAll();

        assertFalse(row1.isSelected());
        assertFalse(row2.isSelected());
        assertFalse(row3.isSelected());
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testIsRowSelected() {
        GridElement grid = GridElement.getById(page, "multi-select-grid");

        var row1 = grid.findRow(0).get();
        var row2 = grid.findRow(1).get();
        var row3 = grid.findRow(2).get();

        row1.select();
        row2.select();
        
        assertTrue(row1.isSelected());
        assertTrue(row2.isSelected());
        assertFalse(row3.isSelected());
    }
}
