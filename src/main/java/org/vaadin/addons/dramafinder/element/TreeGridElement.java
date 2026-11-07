package org.vaadin.addons.dramafinder.element;

import java.util.Optional;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for Vaadin Tree Grid.
 * <p>
 * Extends {@link GridElement} with tree-specific APIs for expanding and collapsing
 * rows, querying hierarchy levels, and performing bulk expand/collapse by level.
 * <p>
 * TreeGrid renders using the same {@code vaadin-grid} web component as Grid.
 * The hierarchy is expressed through {@code vaadin-grid-tree-toggle} elements
 * rendered inside the hierarchy column's cell content.
 * <p>
 * The {@code expanded} and {@code leaf} states are reflected as HTML attributes
 * on {@code vaadin-grid-tree-toggle} and are read via {@code getAttribute}.
 * The {@code level} value is a DOM property only (not a reflected attribute)
 * and must be read via JavaScript evaluation.
 */
@PlaywrightElement(TreeGridElement.FIELD_TAG_NAME)
public class TreeGridElement extends GridElement {

    // TreeGrid renders as the same vaadin-grid web component as Grid.
    public static final String FIELD_TAG_NAME = GridElement.FIELD_TAG_NAME;

    /**
     * Create a new {@code TreeGridElement}.
     *
     * @param locator the locator for the {@code <vaadin-grid>} element backed by a TreeGrid
     */
    public TreeGridElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code TreeGridElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code TreeGridElement}
     */
    public static TreeGridElement get(Page page) {
        return new TreeGridElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code TreeGridElement} within a parent locator.
     *
     * @param parent the parent locator to search within
     * @return the first matching {@code TreeGridElement}
     */
    public static TreeGridElement get(Locator parent) {
        return new TreeGridElement(parent.locator(FIELD_TAG_NAME).first());
    }

    // ── Private Helpers ────────────────────────────────────────────────

    /**
     * Get the {@code vaadin-grid-tree-toggle} locator for the row at the given index.
     * Searches all cells in the row to find the hierarchy column, so this works correctly
     * even when columns have been reordered or when the grid uses multi-select mode
     * (which inserts a checkbox column at index 0, pushing the hierarchy column to index 1).
     *
     * @param rowIndex 0-based row index
     * @return locator for the toggle element (may have {@code count() == 0} for leaf rows with no toggle)
     * @throws IllegalArgumentException if no row exists at the given index
     */
    private Locator getToggleForRow(int rowIndex) {
        var row = findRow(rowIndex);
        if (row.isEmpty()) {
            throw new IllegalArgumentException("No row found at index " + rowIndex);
        }
        var rowEl = row.get();
        int cellCount = rowEl.getRowLocator().locator("td").count();
        for (int i = 0; i < cellCount; i++) {
            var toggle = rowEl.getCell(i).getCellContentLocator()
                    .locator("vaadin-grid-tree-toggle");
            if (toggle.count() > 0) {
                return toggle.first();
            }
        }
        // No toggle found in any cell — return an empty locator; callers handle count() == 0
        return rowEl.getCell(0).getCellContentLocator()
                .locator("vaadin-grid-tree-toggle").first();
    }

    // ── Row-Level Query Methods ────────────────────────────────────────

    /**
     * Find the tree row at the given index, returning a {@link TreeRowElement}
     * that exposes tree-specific state and actions.
     *
     * @param rowIndex 0-based row index
     * @return optional tree row element; empty if the row does not exist
     */
    public Optional<TreeRowElement> findTreeRow(int rowIndex) {
        return findRow(rowIndex)
                .map(row -> new TreeRowElement(row.getRowLocator(), rowIndex));
    }

    /**
     * Whether the row at the given index is expanded.
     * Reads the {@code expanded} reflected attribute from the tree toggle.
     *
     * @param rowIndex 0-based row index
     * @return {@code true} if expanded
     */
    public boolean isRowExpanded(int rowIndex) {
        var toggle = getToggleForRow(rowIndex);
        if (toggle.count() == 0) {
            return false;
        }
        return toggle.getAttribute("expanded") != null;
    }

    /**
     * Whether the row at the given index is collapsed (has children but is not expanded).
     *
     * @param rowIndex 0-based row index
     * @return {@code true} if the row has children and is not expanded
     */
    public boolean isRowCollapsed(int rowIndex) {
        return !isRowExpanded(rowIndex) && !isRowLeaf(rowIndex);
    }

    /**
     * Whether the row at the given index is a leaf node (has no children).
     * Reads the {@code leaf} reflected attribute from the tree toggle.
     *
     * @param rowIndex 0-based row index
     * @return {@code true} if the row is a leaf
     */
    public boolean isRowLeaf(int rowIndex) {
        var toggle = getToggleForRow(rowIndex);
        if (toggle.count() == 0) {
            return true;
        }
        return toggle.getAttribute("leaf") != null;
    }

    /**
     * Get the hierarchy level of the row at the given index (0-based; root items are level 0).
     * Reads the {@code level} DOM property from the tree toggle via JavaScript evaluation,
     * because {@code level} is not reflected as an HTML attribute.
     *
     * @param rowIndex 0-based row index
     * @return hierarchy level
     */
    public int getRowLevel(int rowIndex) {
        var toggle = getToggleForRow(rowIndex);
        var level = toggle.evaluate("el => el.level");
        return level instanceof Number ? ((Number) level).intValue() : 0;
    }

    /**
     * Get the number of currently visible expanded rows.
     * Counts {@code vaadin-grid-tree-toggle} elements in the DOM that have
     * the {@code expanded} attribute present (it is a reflected boolean attribute).
     *
     * @return count of visible expanded rows
     */
    public int getExpandedRowCount() {
        return locator.locator("vaadin-grid-tree-toggle[expanded]").count();
    }

    // ── Row-Level Expand / Collapse ────────────────────────────────────

    /**
     * Expand the row at the given index.
     * Does nothing if the row is already expanded or is a leaf node.
     *
     * @param rowIndex 0-based row index
     */
    public void expandRow(int rowIndex) {
        if (!isRowLeaf(rowIndex) && !isRowExpanded(rowIndex)) {
            clickTreeToggle(getToggleForRow(rowIndex));
        }
    }

    /**
     * Collapse the row at the given index.
     * Does nothing if the row is already collapsed or is a leaf node.
     *
     * @param rowIndex 0-based row index
     */
    public void collapseRow(int rowIndex) {
        if (isRowExpanded(rowIndex)) {
            clickTreeToggle(getToggleForRow(rowIndex));
        }
    }

    /**
     * Toggle the expand/collapse state of the row at the given index.
     * Does nothing if the row is a leaf node.
     *
     * @param rowIndex 0-based row index
     */
    public void toggleRow(int rowIndex) {
        if (!isRowLeaf(rowIndex)) {
            clickTreeToggle(getToggleForRow(rowIndex));
        }
    }

    /**
     * Click a tree toggle and wait for the grid to finish loading.
     * Uses a JS synthetic click ({@code el.click()}) rather than Playwright's
     * coordinate-based click, because {@code vaadin-grid-cell-content} intercepts
     * pointer events and would otherwise block the click from reaching the toggle.
     * This mirrors the approach used by Vaadin's TestBench (Selenium {@code WebElement.click()}).
     *
     * @param toggleLocator locator for the {@code vaadin-grid-tree-toggle} to click
     */
    protected void clickTreeToggle(Locator toggleLocator) {
        toggleLocator.evaluate("el => el.click()");
        waitAfterAction();
        waitForGridToStopLoading();
    }

    // ── Inner Class: TreeRowElement ────────────────────────────────────

    /**
     * Represents a row in a TreeGrid, extending {@link GridElement.RowElement}
     * with tree-specific state queries and expand/collapse actions.
     */
    public class TreeRowElement extends GridElement.RowElement {

        /**
         * Create a new {@code TreeRowElement}.
         *
         * @param rowLocator the locator for the {@code <tr>} element
         * @param rowIndex   0-based row index
         */
        public TreeRowElement(Locator rowLocator, int rowIndex) {
            super(rowLocator, rowIndex);
        }

        /**
         * Create a {@code TreeRowElement} from an existing {@link GridElement.RowElement},
         * upgrading it with tree-specific state and action APIs.
         *
         * @param row the base row element to wrap
         */
        public TreeRowElement(GridElement.RowElement row) {
            super(row.getRowLocator(), row.getRowIndex());
        }

        /**
         * Get the locator for the {@code vaadin-grid-tree-toggle} element in this row.
         * Delegates to {@link TreeGridElement#getToggleForRow(int)} to handle column
         * reordering and multi-select grids (where the hierarchy column is not always index 0).
         *
         * @return locator for the tree toggle
         */
        public Locator getTreeToggleLocator() {
            return TreeGridElement.this.getToggleForRow(getRowIndex());
        }

        /**
         * Whether this row is expanded.
         * Reads the {@code expanded} reflected attribute from the tree toggle.
         *
         * @return {@code true} if expanded
         */
        public boolean isExpanded() {
            var toggle = getTreeToggleLocator();
            if (toggle.count() == 0) {
                return false;
            }
            return toggle.getAttribute("expanded") != null;
        }

        /**
         * Whether this row is a leaf node (has no children).
         * Reads the {@code leaf} reflected attribute from the tree toggle.
         *
         * @return {@code true} if the row is a leaf
         */
        public boolean isLeaf() {
            var toggle = getTreeToggleLocator();
            if (toggle.count() == 0) {
                return true;
            }
            return toggle.getAttribute("leaf") != null;
        }

        /**
         * Whether this row is collapsed (has children but is not expanded).
         *
         * @return {@code true} if collapsed
         */
        public boolean isCollapsed() {
            return !isExpanded() && !isLeaf();
        }

        /**
         * Get the hierarchy level of this row (0-based; root items are level 0).
         * Reads the {@code level} DOM property via JavaScript evaluation, because
         * {@code level} is not reflected as an HTML attribute.
         *
         * @return hierarchy level
         */
        public int getLevel() {
            var level = getTreeToggleLocator().evaluate("el => el.level");
            return level instanceof Number ? ((Number) level).intValue() : 0;
        }

        /**
         * Expand this row.
         * Does nothing if already expanded or a leaf node.
         */
        public void expand() {
            TreeGridElement.this.expandRow(getRowIndex());
        }

        /**
         * Collapse this row.
         * Does nothing if already collapsed or a leaf node.
         */
        public void collapse() {
            TreeGridElement.this.collapseRow(getRowIndex());
        }

        /**
         * Toggle the expand/collapse state of this row.
         * Does nothing if the row is a leaf node.
         */
        public void toggle() {
            TreeGridElement.this.toggleRow(getRowIndex());
        }
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TreeGridElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TreeGridElement}
     */
    public static TreeGridElement getById(Page page, String id) {
        return new TreeGridElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TreeGridElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TreeGridElement}
     */
    public static TreeGridElement getById(Locator locator, String id) {
        return new TreeGridElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TreeGridElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TreeGridElement}
     */
    public static TreeGridElement getByTestId(Page page, String testId) {
        return new TreeGridElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TreeGridElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TreeGridElement}
     */
    public static TreeGridElement getByTestId(Locator locator, String testId) {
        return new TreeGridElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
