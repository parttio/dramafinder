package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TreeGridElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests covering TreeGrid row selection and deselection for both
 * single-select (default) and multi-select modes.
 * <p>
 * Single-select tree ({@code "two-level-tree-grid"}):
 * <pre>
 * Root 0  (index 0)
 * Root 1  (index 1)
 * Root 2  (index 2)
 * </pre>
 * Multi-select tree ({@code "multi-select-tree-grid"}): same data, selection
 * column at DOM index 0, hierarchy column at DOM index 1.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TreeGridSelectionViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "tree-grid";
    }

    // ── Single-Select: Initial State ───────────────────────────────────

    @Test
    public void testSingleSelectTreeGridHasNoRowSelectedInitially() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testSingleSelectTreeGridRootRowIsNotSelectedInitially() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertFalse(grid.findRow(0).get().isSelected());
    }

    // ── Single-Select: Select / Deselect ──────────────────────────────

    @Test
    public void testSelectRowInSingleSelectTreeGridMarksRowAsSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());
    }

    @Test
    public void testSelectRowInSingleSelectTreeGridSetsSelectedItemCountToOne() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testDeselectRowInSingleSelectTreeGridMarksRowAsNotSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());
        grid.deselect(0);
        assertFalse(grid.findRow(0).get().isSelected());
    }

    @Test
    public void testDeselectRowInSingleSelectTreeGridSetsSelectedItemCountToZero() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        grid.deselect(0);
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testSelectingDifferentRowInSingleSelectTreeGridDeselectedPreviousRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());

        grid.select(1);
        assertFalse(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    // ── Single-Select: Selection After Expand ─────────────────────────

    @Test
    public void testSelectionIsMaintainedInSingleSelectTreeGridAfterExpandingRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());

        grid.expandRow(0);
        // Root 0 is still selected after expansion
        assertTrue(grid.findTreeRow(0).get().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testChildRowCanBeSelectedInSingleSelectTreeGridAfterExpansion() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        // Row 1 is the first child of Root 0
        grid.select(1);
        assertTrue(grid.findTreeRow(1).get().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testSelectingChildRowInSingleSelectTreeGridDeselectsParentRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());

        grid.expandRow(0);
        grid.select(1); // first child of Root 0
        assertFalse(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    // ── Single-Select: TreeRowElement API ─────────────────────────────

    @Test
    public void testTreeRowElementSelectWorksInSingleSelectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        var row = grid.findTreeRow(0).get();
        assertFalse(row.isSelected());
        row.select();
        assertTrue(row.isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testTreeRowElementDeselectWorksInSingleSelectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        var row = grid.findTreeRow(0).get();
        row.select();
        assertTrue(row.isSelected());
        row.deselect();
        assertFalse(row.isSelected());
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testTreeRowElementIsSelectedReturnsTrueAfterSelectInSingleSelectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.select(1);
        assertTrue(grid.findTreeRow(1).get().isSelected());
    }

    // ── Multi-Select: Initial State ────────────────────────────────────

    @Test
    public void testMultiSelectTreeGridHasNoRowSelectedInitially() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testMultiSelectTreeGridSelectAllCheckboxIsUncheckedInitially() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        assertTrue(grid.isSelectAllUnchecked());
    }

    // ── Multi-Select: Select / Deselect ───────────────────────────────

    @Test
    public void testSelectRowInMultiSelectTreeGridMarksRowAsSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());
    }

    @Test
    public void testSelectRowInMultiSelectTreeGridSetsSelectedItemCountToOne() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testDeselectRowInMultiSelectTreeGridMarksRowAsNotSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());
        grid.deselect(0);
        assertFalse(grid.findRow(0).get().isSelected());
    }

    @Test
    public void testDeselectRowInMultiSelectTreeGridSetsSelectedItemCountToZero() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        grid.deselect(0);
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testSelectMultipleRowsInMultiSelectTreeGridIncreasesSelectedItemCount() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        grid.select(1);
        assertEquals(2, grid.getSelectedItemCount());
        assertTrue(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
    }

    @Test
    public void testSelectingSecondRowInMultiSelectTreeGridKeepsFirstRowSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        grid.select(1);
        assertTrue(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
    }

    @Test
    public void testDeselectOneOfMultipleSelectedRowsInMultiSelectTreeGridDecreasesCount() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        grid.select(1);
        assertEquals(2, grid.getSelectedItemCount());

        grid.deselect(0);
        assertFalse(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    // ── Multi-Select: Select-All Checkbox ─────────────────────────────

    @Test
    public void testSelectAllCheckboxIsIndeterminateWhenSomeButNotAllRowsAreSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        assertTrue(grid.isSelectAllIndeterminate());
    }

    @Test
    public void testCheckSelectAllSetsSelectAllCheckboxToCheckedState() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.checkSelectAll();
        assertTrue(grid.isSelectAllChecked());
    }

    @Test
    public void testUncheckSelectAllAfterCheckSelectAllSetsSelectedItemCountToZero() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.checkSelectAll();
        grid.uncheckSelectAll();
        assertEquals(0, grid.getSelectedItemCount());
        assertTrue(grid.isSelectAllUnchecked());
    }

    // ── Multi-Select: Selection After Expand ──────────────────────────

    @Test
    public void testSelectionIsMaintainedInMultiSelectTreeGridAfterExpandingRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        assertTrue(grid.findRow(0).get().isSelected());

        grid.expandRow(0);
        // Root 0 is still selected after expansion
        assertTrue(grid.findTreeRow(0).get().isSelected());
    }

    @Test
    public void testChildRowCanBeSelectedInMultiSelectTreeGridAfterExpansion() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.expandRow(0);
        // Row 1 is the first child of Root 0
        grid.select(1);
        assertTrue(grid.findTreeRow(1).get().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testSelectingChildRowInMultiSelectTreeGridKeepsParentRowSelected() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(0);
        grid.expandRow(0);
        grid.select(1); // first child of Root 0
        assertTrue(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
        assertEquals(2, grid.getSelectedItemCount());
    }

    // ── Multi-Select: TreeRowElement API ──────────────────────────────

    @Test
    public void testTreeRowElementSelectWorksInMultiSelectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        var row = grid.findTreeRow(0).get();
        assertFalse(row.isSelected());
        row.select();
        assertTrue(row.isSelected());
        assertEquals(1, grid.getSelectedItemCount());
    }

    @Test
    public void testTreeRowElementDeselectWorksInMultiSelectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        var row = grid.findTreeRow(0).get();
        row.select();
        assertTrue(row.isSelected());
        row.deselect();
        assertFalse(row.isSelected());
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testTreeRowElementSelectDoesNotDeselectOtherRowsInMultiSelectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "multi-select-tree-grid");
        grid.select(1);
        grid.findTreeRow(0).get().select();
        assertTrue(grid.findRow(0).get().isSelected());
        assertTrue(grid.findRow(1).get().isSelected());
        assertEquals(2, grid.getSelectedItemCount());
    }
}
