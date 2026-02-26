package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TreeGridElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests covering basic TreeGrid features: factory methods,
 * row and column counts, header and cell content access, and initial
 * tree-specific state (level, leaf, expanded) before any interaction.
 * <p>
 * All tests use {@code "two-level-tree-grid"} which starts with 3 collapsed
 * root items visible.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TreeGridBasicViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "tree-grid";
    }

    // ── Factory Methods ────────────────────────────────────────────────

    @Test
    public void testGetByPageReturnsFirstTreeGridOnPage() {
        TreeGridElement grid = TreeGridElement.get(page);
        grid.assertVisible();
    }

    @Test
    public void testGetByIdReturnsCorrectTreeGrid() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        grid.assertVisible();
    }

    @Test
    public void testGetByLocatorReturnsTreeGridWithinParent() {
        TreeGridElement grid = TreeGridElement.get(
                page.locator("#two-level-tree-grid").locator(".."));
        grid.assertVisible();
    }

    // ── Row Count ──────────────────────────────────────────────────────

    @Test
    public void testInitialTotalRowCountShowsOnlyRootItems() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(3, grid.getTotalRowCount());
    }

    // ── Column Count ───────────────────────────────────────────────────

    @Test
    public void testColumnCountMatchesConfiguredColumns() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(2, grid.getColumnCount());
    }

    // ── Headers ────────────────────────────────────────────────────────

    @Test
    public void testHeaderCellContentsMatchConfiguredHeaders() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        List<String> headers = grid.getHeaderCellContents();
        assertEquals(List.of("Name", "Description"), headers);
    }

    // ── Cell Content ───────────────────────────────────────────────────

    @Test
    public void testCellContentAtFirstRowAndFirstColumnIsAccessible() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        var cell = grid.findCell(0, 0);
        assertTrue(cell.isPresent());
        assertEquals("Root 0", cell.get().getCellContentLocator().innerText());
    }

    @Test
    public void testCellContentByColumnHeaderTextIsAccessible() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        var cell = grid.findCell(0, "Name");
        assertTrue(cell.isPresent());
        assertEquals("Root 0", cell.get().getCellContentLocator().innerText());
    }

    // ── Tree State ─────────────────────────────────────────────────────

    @Test
    public void testRootRowHasHierarchyLevelZero() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(0, grid.getRowLevel(0));
    }

    @Test
    public void testRootRowIsNotALeafNode() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertFalse(grid.isRowLeaf(0));
    }

    @Test
    public void testRootRowIsInitiallyInCollapsedState() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertFalse(grid.isRowExpanded(0));
        assertTrue(grid.isRowCollapsed(0));
    }

    @Test
    public void testInitiallyZeroRowsAreExpanded() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(0, grid.getExpandedRowCount());
    }

    // ── TreeRowElement ─────────────────────────────────────────────────

    @Test
    public void testFindTreeRowReturnsPresentOptional() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertTrue(grid.findTreeRow(0).isPresent());
    }

    @Test
    public void testTreeRowElementReportsCorrectLevelForRootRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertEquals(0, grid.findTreeRow(0).get().getLevel());
    }

    @Test
    public void testTreeRowElementReportsNotLeafForRootRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertFalse(grid.findTreeRow(0).get().isLeaf());
    }

    @Test
    public void testTreeRowElementReportsNotExpandedForInitialRootRow() {
        TreeGridElement grid = TreeGridElement.getById(page, "two-level-tree-grid");
        assertFalse(grid.findTreeRow(0).get().isExpanded());
    }
}
