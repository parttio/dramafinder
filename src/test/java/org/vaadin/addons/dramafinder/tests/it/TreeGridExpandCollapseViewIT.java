package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TreeGridElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests covering TreeGrid expand and collapse operations,
 * including single-row toggling and interaction via the
 * {@link TreeGridElement.TreeRowElement} API.
 * <p>
 * Two-level tree ({@code "two-level-tree-grid"}):
 * <pre>
 * Root 0            (level 0) — 2 children
 * Root 1            (level 0) — 3 children
 * Root 2            (level 0) — 1 child
 * </pre>
 * Three-level tree ({@code "three-level-tree-grid"}):
 * <pre>
 * Division A        (level 0) — 2 dept children
 *   Dept A1         (level 1) — 2 employee children
 *   Dept A2         (level 1) — 1 employee child
 * Division B        (level 0) — 1 dept child
 *   Dept B1         (level 1) — 1 employee child
 * </pre>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TreeGridExpandCollapseViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "tree-grid";
    }

    // ── Single-Row Expand ──────────────────────────────────────────────

    @Test
    public void testExpandRowIncreasesTotalRowCount() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(3, grid.getTotalRowCount());
        grid.expandRow(0);
        // Root 0's 2 children are now visible: 3 roots + 2 children = 5
        assertEquals(5, grid.getTotalRowCount());
    }

    @Test
    public void testExpandRowSetsRowExpandedStateToTrue() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.isRowExpanded(0));
    }

    @Test
    public void testExpandRowMakesChildRowsAccessibleByIndex() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        var cell = grid.findCell(1, "Name");
        assertTrue(cell.isPresent());
        assertEquals("Child 0-0", cell.get().getCellContentLocator().innerText());
    }

    @Test
    public void testChildRowAfterExpansionHasLevelOne() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        // Row 1 is the first child of Root 0
        assertEquals(1, grid.getRowLevel(1));
    }

    @Test
    public void testChildRowAfterExpansionIsALeafNode() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.isRowLeaf(1));
    }

    // ── Single-Row Collapse ────────────────────────────────────────────

    @Test
    public void testCollapseRowRestoresRowCountToRootOnlyCount() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        assertEquals(5, grid.getTotalRowCount());
        grid.collapseRow(0);
        assertEquals(3, grid.getTotalRowCount());
    }

    @Test
    public void testCollapseRowSetsRowExpandedStateToFalse() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.isRowExpanded(0));
        grid.collapseRow(0);
        assertFalse(grid.isRowExpanded(0));
    }

    // ── Toggle ─────────────────────────────────────────────────────────

    @Test
    public void testToggleRowExpandsCollapsedRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertFalse(grid.isRowExpanded(0));
        grid.toggleRow(0);
        assertTrue(grid.isRowExpanded(0));
    }

    @Test
    public void testToggleRowCollapsesAlreadyExpandedRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.toggleRow(0);
        assertTrue(grid.isRowExpanded(0));
        grid.toggleRow(0);
        assertFalse(grid.isRowExpanded(0));
    }

    // ── Expanded Row Count ─────────────────────────────────────────────

    @Test
    public void testExpandedRowCountReflectsNumberOfExpandedItems() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(0, grid.getExpandedRowCount());

        grid.expandRow(0);
        assertEquals(1, grid.getExpandedRowCount());

        // After expanding Root 0 (2 children), Root 1 is at flat index 3
        grid.expandRow(3);
        assertEquals(2, grid.getExpandedRowCount());
    }

    // ── TreeRowElement API ─────────────────────────────────────────────

    @Test
    public void testTreeRowElementExpandMethodExpandsRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        var row = grid.findTreeRow(0).get();
        assertFalse(row.isExpanded());
        row.expand();
        assertTrue(grid.isRowExpanded(0));
        assertEquals(5, grid.getTotalRowCount());
    }

    @Test
    public void testTreeRowElementCollapseMethodCollapsesExpandedRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        var row = grid.findTreeRow(0).get();
        assertTrue(row.isExpanded());
        row.collapse();
        assertFalse(grid.isRowExpanded(0));
        assertEquals(3, grid.getTotalRowCount());
    }

    @Test
    public void testTreeRowElementIsExpandedReturnsTrueAfterExpand() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        assertTrue(grid.findTreeRow(0).get().isExpanded());
    }

    @Test
    public void testTreeRowElementIsLeafReturnsTrueForChildRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        // Row 1 is the first child of Root 0 — a leaf in a two-level tree
        assertTrue(grid.findTreeRow(1).get().isLeaf());
    }

    @Test
    public void testTreeRowElementGetLevelReturnsOneForChildRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.expandRow(0);
        assertEquals(1, grid.findTreeRow(1).get().getLevel());
    }

    @Test
    public void testTreeRowElementGetLevelReturnsTwoForGrandchildRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "three-level-tree-grid");
        // Expand Division A, then Dept A1 to reveal grandchildren
        grid.expandRow(0);  // Division A
        grid.expandRow(1);  // Dept A1 (first child of Division A)
        // Row 2 is Employee A1a — level 2
        assertEquals(2, grid.findTreeRow(2).get().getLevel());
    }
}
