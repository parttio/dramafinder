package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TreeGridElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests verifying that TreeGrid expand/collapse operations work
 * correctly when the grid uses multi-select mode.
 * <p>
 * In multi-select mode Vaadin Grid inserts a checkbox selection column at DOM
 * index 0, pushing the hierarchy column to index 1.  These tests confirm that
 * {@code TreeGridElement} finds the {@code vaadin-grid-tree-toggle} by searching
 * all cells rather than assuming the toggle is always in cell 0.
 * <p>
 * Multi-select two-level tree ({@code "multi-select-tree-grid"}):
 * <pre>
 * Root 0            (level 0) — 2 children
 * Root 1            (level 0) — 3 children
 * Root 2            (level 0) — 1 child
 * </pre>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TreeGridMultiSelectViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "tree-grid";
    }

    // ── Single-Row Expand ──────────────────────────────────────────────

    @Test
    public void testExpandRowInMultiSelectGridIncreasesTotalRowCount() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertEquals(3, grid.getTotalRowCount());
        grid.expandRow(0);
        // Root 0's 2 children now visible: 3 roots + 2 children = 5
        assertEquals(5, grid.getTotalRowCount());
    }

    @Test
    public void testExpandRowInMultiSelectGridSetsRowExpandedStateToTrue() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.isRowExpanded(0));
    }

    @Test
    public void testExpandRowInMultiSelectGridMakesChildRowsAccessibleByIndex() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        var cell = grid.findCell(1, "Name");
        assertTrue(cell.isPresent());
        assertEquals("Child 0-0", cell.get().getCellContentLocator().innerText());
    }

    // ── Single-Row Collapse ────────────────────────────────────────────

    @Test
    public void testCollapseRowInMultiSelectGridRestoresRowCount() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertEquals(5, grid.getTotalRowCount());
        grid.collapseRow(0);
        assertEquals(3, grid.getTotalRowCount());
    }

    @Test
    public void testCollapseRowInMultiSelectGridSetsRowExpandedStateToFalse() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.isRowExpanded(0));
        grid.collapseRow(0);
        assertFalse(grid.isRowExpanded(0));
    }

    // ── Toggle ─────────────────────────────────────────────────────────

    @Test
    public void testToggleRowInMultiSelectGridExpandsCollapsedRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertFalse(grid.isRowExpanded(0));
        grid.toggleRow(0);
        assertTrue(grid.isRowExpanded(0));
    }

    @Test
    public void testToggleRowInMultiSelectGridCollapsesAlreadyExpandedRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.toggleRow(0);
        assertTrue(grid.isRowExpanded(0));
        grid.toggleRow(0);
        assertFalse(grid.isRowExpanded(0));
    }

    // ── Row State Queries ──────────────────────────────────────────────

    @Test
    public void testChildRowLevelIsOneAfterExpansionInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertEquals(1, grid.getRowLevel(1));
    }

    @Test
    public void testChildRowIsLeafAfterExpansionInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.isRowLeaf(1));
    }

    @Test
    public void testRootRowIsNotLeafInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertFalse(grid.isRowLeaf(0));
    }

    @Test
    public void testRootRowIsInitiallyCollapsedInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertFalse(grid.isRowExpanded(0));
        assertTrue(grid.isRowCollapsed(0));
    }

    // ── Expanded Row Count ─────────────────────────────────────────────

    @Test
    public void testExpandedRowCountReflectsExpandedItemsInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertEquals(0, grid.getExpandedRowCount());
        grid.expandRow(0);
        assertEquals(1, grid.getExpandedRowCount());
        // After expanding Root 0 (2 children), Root 1 is at flat index 3
        grid.expandRow(3);
        assertEquals(2, grid.getExpandedRowCount());
    }

    // ── TreeRowElement API ─────────────────────────────────────────────

    @Test
    public void testTreeRowElementExpandWorksInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        var row = grid.findTreeRow(0).get();
        assertFalse(row.isExpanded());
        row.expand();
        assertTrue(grid.isRowExpanded(0));
        assertEquals(5, grid.getTotalRowCount());
    }

    @Test
    public void testTreeRowElementCollapseWorksInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        var row = grid.findTreeRow(0).get();
        assertTrue(row.isExpanded());
        row.collapse();
        assertFalse(grid.isRowExpanded(0));
        assertEquals(3, grid.getTotalRowCount());
    }

    @Test
    public void testTreeRowElementGetLevelReturnsZeroForRootRowInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertEquals(0, grid.findTreeRow(0).get().getLevel());
    }

    @Test
    public void testTreeRowElementGetLevelReturnsOneForChildRowInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertEquals(1, grid.findTreeRow(1).get().getLevel());
    }

    @Test
    public void testTreeRowElementIsLeafReturnsTrueForChildRowInMultiSelectGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.findTreeRow(1).get().isLeaf());
    }
}
