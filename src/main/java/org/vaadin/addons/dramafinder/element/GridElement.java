package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-grid>}.
 * <p>
 * Provides helpers for scrolling, querying visible rows, accessing cell
 * content, and interacting with the grid's header, body and selection.
 * Cell access uses JavaScript evaluation on the grid's internal APIs
 * since body cells live in shadow DOM and are virtualized.
 */
@PlaywrightElement(GridElement.FIELD_TAG_NAME)
public class GridElement extends VaadinElement
        implements FocusableElement, HasStyleElement, HasThemeElement, HasEnabledElement {

    public static final String FIELD_TAG_NAME = "vaadin-grid";

    /**
     * Create a new {@code GridElement}.
     *
     * @param locator the locator for the {@code <vaadin-grid>} element
     */
    public GridElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code GridElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code GridElement}
     */
    public static GridElement get(Page page) {
        return new GridElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code GridElement} within a parent locator.
     *
     * @param parent the parent locator to search within
     * @return the first matching {@code GridElement}
     */
    public static GridElement get(Locator parent) {
        return new GridElement(parent.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get a {@code GridElement} by its {@code id} attribute.
     *
     * @param page the Playwright page
     * @param id   the element id
     * @return the matching {@code GridElement}
     */
    public static GridElement getById(Page page, String id) {
        return new GridElement(page.locator("#" + id));
    }

    // ── Properties / Getters ───────────────────────────────────────────

    /**
     * Get the number of rows currently rendered in the DOM.
     * This may be less than the total row count due to virtualization.
     *
     * @return count of rows currently rendered in the DOM
     */
    public int getRenderedRowCount() {
        return (Integer) locator.elementHandle().evaluate("e => e._getRenderedRows().length");
    }

    /**
     * Get the total number of rows (data items) in the grid.
     *
     * @return total item count
     */
    public int getTotalRowCount() {
        return (int) locator.elementHandle().evaluate("e => e._flatSize");
    }

    /**
     * Get the number of visible (non-hidden) columns.
     *
     * @return visible column count
     */
    public int getColumnCount() {
        return ((Number) locator.evaluate(
                "el => el._getColumns().filter(c => !c.hidden).length")).intValue();
    }

    /**
     * Whether the grid has {@code allRowsVisible} enabled.
     *
     * @return {@code true} if all rows are visible (no vertical scroll)
     */
    public boolean isAllRowsVisible() {
        return Boolean.TRUE.equals(getProperty("allRowsVisible"));
    }

    /**
     * Whether the grid has multi-sort enabled.
     *
     * @return {@code true} if multi-sort is enabled
     */
    public boolean isMultiSort() {
        return Boolean.TRUE.equals(getProperty("multiSort"));
    }

    /**
     * Whether column reordering is allowed.
     *
     * @return {@code true} if column reordering is allowed
     */
    public boolean isColumnReorderingAllowed() {
        return Boolean.TRUE.equals(getProperty("columnReorderingAllowed"));
    }

    // ── Header Access ──────────────────────────────────────────────────


    /**
     * Get the number of header rows. Usually 1, but can be more if there are column groups.
     *
     * @return header row count
     */
    protected int getHeaderRowCount() {
        int rowCount = 0;
        var lastHeaderRow = locator.locator("thead tr.last-header-row").first();
        if (lastHeaderRow.count() > 0) {
            var headerRowIndex = lastHeaderRow.getAttribute("aria-rowIndex");
            if (headerRowIndex != null) {
                rowCount = Integer.parseInt(headerRowIndex);
            }
        }
        return rowCount;
    }

    /**
     * Find a header cell by column index.
     * Uses first header row.
     *
     * @param columnIndex 0-based visible column index
     * @return optional header cell element. Empty if no header cell exists at the given column index.
     */
    public Optional<HeaderCellElement> findHeaderCell(int columnIndex) {
        return findHeaderCell(0, columnIndex);
    }

    /**
     * Find a header cell by header row index and column index.
     *
     * @param headerRowIndex 0-based header row index. Use 0 for the first header row, 1 for the second, etc.
     * @param columnIndex    0-based visible column index.
     * @return optional header cell element. Empty if no header cell exists at the given header row and column index.
     */
    public Optional<HeaderCellElement> findHeaderCell(int headerRowIndex, int columnIndex) {
        if (columnIndex < 0) {
            throw new IllegalArgumentException("Column index must be non-negative");
        } else if (headerRowIndex < 0) {
            throw new IllegalArgumentException("Header row index must be non-negative");
        }

        var lastHeaderRow = locator.locator("thead tr").nth(headerRowIndex);
        if (lastHeaderRow.count() == 0) {
            return Optional.empty();
        }

        var headerCell = lastHeaderRow.locator("th").nth(columnIndex);
        if (headerCell.count() > 0) {
            headerCell.scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
            return Optional.of(new HeaderCellElement(headerCell, columnIndex));
        }

        return Optional.empty();
    }

    /**
     * Find a header cell by its text content.
     * Uses first header row.
     *
     * @param text the header text to find
     * @return optional header cell element. Empty if no header cell with the given text is found.
     */
    public Optional<HeaderCellElement> findHeaderCellByText(String text) {
        return findHeaderCellByText(0, text);
    }

    /**
     * Find a header cell by header row index and text content.
     *
     * @param headerRowIndex 0-based header row index. Use 0 for the first header row, 1 for the second, etc.
     * @param text           the header text to find
     * @return optional header cell element. Empty if no header cell with the given text is found in the given header row.
     */
    public Optional<HeaderCellElement> findHeaderCellByText(int headerRowIndex, String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text must not be null or empty");
        }
        var lastHeaderRow = locator.locator("thead tr").nth(headerRowIndex);
        if (lastHeaderRow.count() == 0) {
            return Optional.empty();
        }

        var allHeaderCells = lastHeaderRow.locator("th").all();
        for (int i = 0; i < allHeaderCells.size(); i++) {
            var cell = new HeaderCellElement(allHeaderCells.get(i), i);
            cell.getTableCellLocator().scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
            if (matchesHeaderCell(cell, text)) {
                return Optional.of(cell);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the text content of all visible header cells.
     *
     * @return list of header cell text contents
     */
    public List<String> getHeaderCellContents() {
        int count = getColumnCount();
        List<String> headers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            var headerCell = findHeaderCell(i);
            if (!headerCell.isPresent()) {
                throw new IllegalStateException("No header cell found at column index " + i);
            }
            headers.add(headerCell.get().getCellContentLocator().innerText());
        }
        return headers;
    }

    /**
     * Determine if a header cell matches the given text. By default, compares the trimmed innerText of the cell content.
     * Can be overridden for custom matching logic (e.g. ignoring case, or matching only a part of the text).
     *
     * @param cell the header cell to check
     * @param text the header text to match
     * @return {@code true} if the cell matches the text, {@code false} otherwise
     */
    protected boolean matchesHeaderCell(CellElement cell, String text) {
        return Objects.equals(cell.getCellContentLocator().innerText(), text);
    }

    // ── Footer Access ──────────────────────────────────────────────────

    /**
     * Get the number of footer rows. Usually 0 (no footer) or 1, but can be more if there are column groups.
     *
     * @return footer row count
     */
    protected int getFooterRowCount() {
        return locator.locator("tfoot tr").count();
    }

    /**
     * Find a footer cell by column index.
     * Uses the first footer row.
     *
     * @param columnIndex 0-based visible column index
     * @return optional footer cell element. Empty if no footer cell exists at the given column index.
     */
    public Optional<FooterCellElement> findFooterCell(int columnIndex) {
        return findFooterCell(0, columnIndex);
    }

    /**
     * Find a footer cell by footer row index and column index.
     *
     * @param footerRowIndex 0-based footer row index. Use 0 for the first footer row, 1 for the second, etc.
     * @param columnIndex    0-based visible column index.
     * @return optional footer cell element. Empty if no footer cell exists at the given footer row and column index.
     */
    public Optional<FooterCellElement> findFooterCell(int footerRowIndex, int columnIndex) {
        if (columnIndex < 0) {
            throw new IllegalArgumentException("Column index must be non-negative");
        } else if (footerRowIndex < 0) {
            throw new IllegalArgumentException("Footer row index must be non-negative");
        }

        var footerRow = locator.locator("tfoot tr").nth(footerRowIndex);
        if (footerRow.count() == 0) {
            return Optional.empty();
        }

        var footerCell = footerRow.locator("td").nth(columnIndex);
        if (footerCell.count() > 0) {
            footerCell.scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
            return Optional.of(new FooterCellElement(footerCell, columnIndex));
        }

        return Optional.empty();
    }

    /**
     * Find a footer cell by its text content.
     * Uses the first footer row.
     *
     * @param text the footer text to find
     * @return optional footer cell element. Empty if no footer cell with the given text is found.
     */
    public Optional<FooterCellElement> findFooterCellByText(String text) {
        return findFooterCellByText(0, text);
    }

    /**
     * Find a footer cell by footer row index and text content.
     *
     * @param footerRowIndex 0-based footer row index. Use 0 for the first footer row, 1 for the second, etc.
     * @param text           the footer text to find
     * @return optional footer cell element. Empty if no footer cell with the given text is found in the given footer row.
     */
    public Optional<FooterCellElement> findFooterCellByText(int footerRowIndex, String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text must not be null or empty");
        }
        var footerRow = locator.locator("tfoot tr").nth(footerRowIndex);
        if (footerRow.count() == 0) {
            return Optional.empty();
        }

        var allFooterCells = footerRow.locator("td").all();
        for (int i = 0; i < allFooterCells.size(); i++) {
            var cell = new FooterCellElement(allFooterCells.get(i), i);
            cell.getTableCellLocator().scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
            if (matchesFooterCell(cell, text)) {
                return Optional.of(cell);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the text content of all visible footer cells.
     *
     * @return list of footer cell text contents
     */
    public List<String> getFooterCellContents() {
        int count = getColumnCount();
        List<String> footers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            var footerCell = findFooterCell(i);
            if (!footerCell.isPresent()) {
                throw new IllegalStateException("No footer cell found at column index " + i);
            }
            footers.add(footerCell.get().getCellContentLocator().innerText());
        }
        return footers;
    }

    /**
     * Determine if a footer cell matches the given text. By default, compares the trimmed innerText of the cell content.
     * Can be overridden for custom matching logic (e.g. ignoring case, or matching only a part of the text).
     *
     * @param cell the footer cell to check
     * @param text the footer text to match
     * @return {@code true} if the cell matches the text, {@code false} otherwise
     */
    protected boolean matchesFooterCell(CellElement cell, String text) {
        return Objects.equals(cell.getCellContentLocator().innerText(), text);
    }

    // ── Cell Content Access ────────────────────────────────────────────

    /**
     * Find a body cell by row index and column index.
     *
     * @param row    row index, starting from 0.
     * @param column column index, starting from 0.
     * @return optional cell element. Empty if no cell exists at the given row and column index.
     */
    public Optional<CellElement> findCell(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Row and column must be non-negative");
        }

        var rowLocatorOptional = findRow(row);
        if (!rowLocatorOptional.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(rowLocatorOptional.get().getCell(column));
    }

    /**
     * Find a body cell by row index and column header text.
     *
     * @param row              row index, starting from 0.
     * @param columnHeaderText the header text of the column to find.
     * @return optional cell element. Empty if no cell exists at the given row and column header text.
     */
    public Optional<CellElement> findCell(int row, String columnHeaderText) {
        if (row < 0) {
            throw new IllegalArgumentException("Row and column must be non-negative");
        }
        var foundHeaderCell = findHeaderCellByText(columnHeaderText);
        if (!foundHeaderCell.isPresent()) {
            return Optional.empty();
        }

        var rowLocatorOptional = findRow(row);
        if (!rowLocatorOptional.isPresent()) {
            return Optional.empty();
        }

        return findCell(row, foundHeaderCell.get().getColumnIndex());
    }

    /**
     * Find row indexes where the cell in the given column has the given text.
     *
     * @param columnIndex the column index to check the cell content in, starting from 0.
     * @param text        the text to match in the cell content.
     * @return list of row indexes where the cell in the given column matches the given text. Empty list if no match is found.
     */
    public List<Integer> findRowIndexesWithColumnText(int columnIndex, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        var headerRowCount = getHeaderRowCount();
        var matchingRows = new ArrayList<Integer>();
        int totalRowCount = getTotalRowCount();
        for (int i = 0; i < totalRowCount; i++) {
            var rowOptional = findRow(i, headerRowCount);

            rowOptional.ifPresent(row -> {
                var cell = row.getCell(columnIndex);
                if (matchesCellContent(cell, text)) {
                    matchingRows.add(row.getRowIndex());
                }
            });
        }

        return matchingRows;
    }

    /**
     * Find a row by its index.
     * Scrolls the grid if necessary to find the row.
     *
     * @param rowIndex row index, starting from 0.
     * @return optional row element. Empty if no row exists at the given index.
     */
    public Optional<RowElement> findRow(int rowIndex) {
        if (rowIndex < 0) {
            throw new IllegalArgumentException("Row index must be non-negative");
        }

        return findRow(rowIndex, getHeaderRowCount());
    }

    /**
     * Find a row by its index, given the number of header rows.
     *
     * @param rowIndex       row index, starting from 0.
     * @param headerRowCount number of header rows in the grid, used to calculate the aria-rowIndex for finding the row.
     * @return optional row element. Empty if no row exists at the given index.
     */
    protected Optional<RowElement> findRow(int rowIndex, int headerRowCount) {
        var ariaRowIndex = rowIndex + 1 + headerRowCount;
        // Attempt to find the row directly
        var foundRow = locator.locator("tbody tr[aria-rowIndex=\"" + ariaRowIndex + "\"]").first();
        if (foundRow.count() == 0) {
            // Row not found, try scrolling to it
            var foundRowByScrolling = findRowByScrolling(locator, ariaRowIndex);
            if (foundRowByScrolling.isEmpty()) {
                return Optional.empty();
            } else {
                foundRow = foundRowByScrolling.get();
            }
        } else {
            foundRow.scrollIntoViewIfNeeded();
            waitForGridToStopLoading();
        }

        return Optional.of(new RowElement(foundRow, rowIndex));
    }

    private Optional<Locator> findRowByScrolling(Locator grid, int ariaRowIndex) {
        return findRowByScrolling(grid, null, ariaRowIndex);
    }

    private Optional<Locator> findRowByScrolling(Locator grid, RowRangeData previousRowRangeData, int ariaRowIndex) {
        var visibleRows = grid.locator("tbody tr").all();
        if (visibleRows.isEmpty()) {
            return Optional.empty();
        }

        var rowRangeData = new RowRangeData(visibleRows);

        if (!areNewRowsLoaded(previousRowRangeData, rowRangeData, ariaRowIndex)) {
            return Optional.empty();
        }

        if (ariaRowIndex < rowRangeData.getMin()) {
            // Scroll up
            rowRangeData.getMinRowLocator().evaluate("el => el.scrollIntoView({ block: 'end', inline: 'nearest' })");
        } else {
            // Scroll down
            rowRangeData.getMaxRowLocator().evaluate("el => el.scrollIntoView({ block: 'start', inline: 'nearest' })");
        }

        waitForGridToStopLoading();

        // Attempt to find the required row after scrolling
        var foundRow = grid.locator("tbody tr[aria-rowIndex=\"" + ariaRowIndex + "\"]").first();
        if (foundRow.count() == 0) {
            // Keep scrolling
            return findRowByScrolling(grid, rowRangeData, ariaRowIndex);
        } else {
            foundRow.scrollIntoViewIfNeeded();
            waitForGridToStopLoading();
        }

        return Optional.of(foundRow);
    }

    private static boolean areNewRowsLoaded(RowRangeData previousRowRangeData, RowRangeData currentRowRangeData, int targetAriaRowIndex) {
        if (previousRowRangeData == null) {
            return true;
        }

        // Check if the current row range has expanded in the direction of the target row index
        if (targetAriaRowIndex < previousRowRangeData.getMin()) {
            return currentRowRangeData.getMin() < previousRowRangeData.getMin();
        } else {
            return currentRowRangeData.getMax() > previousRowRangeData.getMax();
        }
    }

    /**
     * Determine if a body cell matches the given text. By default, compares the trimmed innerText of the cell content.
     * Can be overridden for custom matching logic (e.g. ignoring case, or matching only a part of the text).
     *
     * @param cell the body cell to check
     * @param text the text to match in the cell content
     * @return {@code true} if the cell matches the text, {@code false} otherwise
     */
    protected boolean matchesCellContent(CellElement cell, String text) {
        cell.getCellContentLocator().scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
        return Objects.equals(cell.getCellContentLocator().innerText(), text);
    }

    // ── Scroll Actions ─────────────────────────────────────────────────

    /**
     * Scroll the grid so that the given row index becomes visible.
     * Waits for the row to be rendered in the DOM before returning.
     *
     * @param rowIndex the 0-based row index to scroll to
     */
    public void scrollToRow(int rowIndex) {
        var row = findRow(rowIndex);
        if (row.isPresent()) {
            row.get().getRowLocator().scrollIntoViewIfNeeded();
            waitForGridToStopLoading();
        }
    }

    /**
     * Scroll to the very beginning of the grid.
     */
    public void scrollToStart() {
        scrollToRow(0);
    }

    /**
     * Scroll to the very end of the grid.
     */
    public void scrollToEnd() {
        var row = findRow(getTotalRowCount() - 1);
        if (row.isPresent()) {
            row.get().getRowLocator().scrollIntoViewIfNeeded();
            waitForGridToStopLoading();
        }
    }

    /**
     * Select a row by id.
     *
     * @param rowIndex index of the row to select, starting from 0.
     */
    public void select(int rowIndex) {
        var rowOptional = findRow(rowIndex);
        rowOptional.ifPresent(this::select);
    }

    /**
     * Select the row.
     * You can override this method to implement custom selection logic,
     * for example if you want to select by clicking some other cell than the first one,
     * or if you want to use some modifier keys for selection.
     *
     * @param row row to select.
     */
    protected void select(RowElement row) {
        if (!row.isSelected()) {
            clickCellForSelection(row.getCell(0));
        }
    }

    /**
     * Deselect a row by id.
     *
     * @param rowIndex index of the row to deselect, starting from 0.
     */
    public void deselect(int rowIndex) {
        var rowOptional = findRow(rowIndex);
        rowOptional.ifPresent(this::deselect);
    }

    /**
     * Deselect the row.
     * You can override this method to implement custom selection logic,
     * for example if you want to deselect by clicking some other cell than the first one,
     * or if you want to use some modifier keys for deselection.
     *
     * @param row row to deselect.
     */
    protected void deselect(RowElement row) {
        if (row.isSelected()) {
            clickCellForSelection(row.getCell(0));
        }
    }

    /**
     * You can override this method to implement custom selection logic for given cell.
     */
    protected void clickCellForSelection(CellElement cell) {
        var checkbox = cell.getCellContentLocator().locator("vaadin-checkbox");
        if (checkbox.count() > 0) {
            checkbox.click();
        } else {
            cell.click();
        }
        waitForGridToStopLoading();
    }

    /**
     * Get the number of currently selected items.
     *
     * @return selected item count
     */
    public int getSelectedItemCount() {
        return ((Number) getLocator().evaluate(
                "el => el.selectedItems ? el.selectedItems.length : 0")).intValue();
    }

    /**
     * Check if the select-all checkbox is checked.
     *
     * @return {@code true} if the select-all checkbox is checked (and not indeterminate), {@code false} otherwise
     */
    public boolean isSelectAllChecked() {
        var checkbox = getSelectAllCheckboxLocator();
        return checkbox.getAttribute("checked") != null
                && checkbox.getAttribute("indeterminate") == null;
    }

    /**
     * Check if the select-all checkbox is indeterminate.
     *
     * @return {@code true} if the select-all checkbox is indeterminate, {@code false} otherwise
     */
    public boolean isSelectAllIndeterminate() {
        return getSelectAllCheckboxLocator().getAttribute("indeterminate") != null;
    }

    /**
     * Check if the select-all checkbox is unchecked.
     *
     * @return {@code true} if the select-all checkbox is unchecked, {@code false} otherwise
     */
    public boolean isSelectAllUnchecked() {
        return getSelectAllCheckboxLocator().getAttribute("checked") == null;
    }

    /**
     * Check the select-all checkbox.
     * Does nothing if the select-all checkbox is already checked.
     */
    public void checkSelectAll() {
        if (isSelectAllChecked()) {
            return;
        }
        getSelectAllCheckboxLocator().click();
        waitForGridToStopLoading();
    }

    /**
     * Uncheck the select-all checkbox.
     * Does nothing if the select-all checkbox is already unchecked.
     */
    public void uncheckSelectAll() {
        if (isSelectAllUnchecked()) {
            return;
        }
        getSelectAllCheckboxLocator().click();
        waitForGridToStopLoading();
    }

    /**
     * Get the select-all checkbox element.
     *
     * @return the select-all checkbox element
     */
    private Locator getSelectAllCheckboxLocator() {
        var checkboxLocator = getLocator().locator("vaadin-checkbox.vaadin-grid-select-all-checkbox");
        if (checkboxLocator.count() == 0) {
            throw new IllegalStateException("Select-all checkbox not found in the grid header");
        }
        return checkboxLocator.first();
    }


    /**
     * Open details for a row by id.
     * Override this method if you need to control how the details are opened,
     * for example by clicking some other cell than the first one.
     *
     * @param row row for which to open details
     */
    protected void openDetails(RowElement row) {
        if (row.isDetailsOpen()) {
            return;
        }

        row.getCell(1).click();
        waitForGridToStopLoading();
    }

    /**
     * Close details for a row by id.
     * Override this method if you need to control how the details are closed,
     * for example by clicking some other cell than the first one.
     *
     * @param row row for which to close details
     */
    protected void closeDetails(RowElement row) {
        if (!row.isDetailsOpen()) {
            return;
        }

        row.getCell(1).click();
        waitForGridToStopLoading();
    }

    /**
     * Wait for the grid to finish loading after a scroll or other action that triggers loading of new rows.
     */
    public void waitForGridToStopLoading() {
        locator.page().waitForFunction("g => !g.hasAttribute('loading')", locator.elementHandle());
        locator.evaluate("g => g.updateComplete.then(() => new Promise(r => requestAnimationFrame(() => requestAnimationFrame(r))))");
    }


    /**
     * Whether the row with the given index is currently scrolled into view
     * (at least partially visible between the header and footer), without
     * triggering any scrolling.
     *
     * @param rowIndex row index, starting from 0.
     * @return {@code true} if the row is rendered and at least partially visible, {@code false} otherwise
     */
    public boolean isRowInView(int rowIndex) {
        if (rowIndex < 0) {
            throw new IllegalArgumentException("Row index must be non-negative");
        }
        var ariaRowIndex = rowIndex + 1 + getHeaderRowCount();
        var row = locator.locator("tbody tr[aria-rowIndex=\"" + ariaRowIndex + "\"]").first();
        if (row.count() == 0) {
            return false;
        }
        return (boolean) row.evaluate(
                "tr => { const grid = tr.getRootNode().host;"
                        + " const r = tr.getBoundingClientRect();"
                        + " const top = grid.$.header.getBoundingClientRect().bottom;"
                        + " const bottom = grid.$.footer.getBoundingClientRect().top;"
                        + " return r.bottom > top && r.top < bottom; }");
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert that the grid has the given total number of rows (data items).
     *
     * @param expected the expected total row count
     */
    public void assertRowCount(int expected) {
        locator.page().waitForCondition(() -> getTotalRowCount() == expected);
    }

    /**
     * Assert that the grid has no rows.
     */
    public void assertEmpty() {
        assertRowCount(0);
    }

    /**
     * Assert that the grid has the given number of visible (non-hidden) columns.
     *
     * @param expected the expected visible column count
     */
    public void assertColumnCount(int expected) {
        locator.page().waitForCondition(() -> getColumnCount() == expected);
    }

    /**
     * Assert that {@code allRowsVisible} is enabled.
     */
    public void assertAllRowsVisible() {
        locator.page().waitForCondition(this::isAllRowsVisible);
    }

    /**
     * Assert that {@code allRowsVisible} is not enabled.
     */
    public void assertNotAllRowsVisible() {
        locator.page().waitForCondition(() -> !isAllRowsVisible());
    }

    /**
     * Assert that multi-sort is enabled.
     */
    public void assertMultiSort() {
        locator.page().waitForCondition(this::isMultiSort);
    }

    /**
     * Assert that multi-sort is not enabled.
     */
    public void assertNotMultiSort() {
        locator.page().waitForCondition(() -> !isMultiSort());
    }

    /**
     * Assert that column reordering is allowed.
     */
    public void assertColumnReorderingAllowed() {
        locator.page().waitForCondition(this::isColumnReorderingAllowed);
    }

    /**
     * Assert that column reordering is not allowed.
     */
    public void assertColumnReorderingNotAllowed() {
        locator.page().waitForCondition(() -> !isColumnReorderingAllowed());
    }

    /**
     * Assert that the body cell at the given row and column has the given text content.
     * Auto-scrolls the grid if necessary to bring the row into view.
     *
     * @param row      row index, starting from 0.
     * @param column   column index, starting from 0.
     * @param expected the expected cell text content
     */
    public void assertCellContent(int row, int column, String expected) {
        var cell = findCell(row, column);
        if (!cell.isPresent()) {
            throw new AssertionError("No cell found at row " + row + ", column " + column);
        }
        assertThat(cell.get().getCellContentLocator()).hasText(expected);
    }

    /**
     * Assert that the body cell at the given row and column header has the given text content.
     * Auto-scrolls the grid if necessary to bring the row into view.
     *
     * @param row              row index, starting from 0.
     * @param columnHeaderText the header text of the column.
     * @param expected         the expected cell text content
     */
    public void assertCellContent(int row, String columnHeaderText, String expected) {
        var cell = findCell(row, columnHeaderText);
        if (!cell.isPresent()) {
            throw new AssertionError("No cell found at row " + row + ", column '" + columnHeaderText + "'");
        }
        assertThat(cell.get().getCellContentLocator()).hasText(expected);
    }

    /**
     * Assert that a cell exists at the given row and column.
     *
     * @param row    row index, starting from 0.
     * @param column column index, starting from 0.
     */
    public void assertCellPresent(int row, int column) {
        locator.page().waitForCondition(() -> cellExists(row, column));
    }

    /**
     * Assert that no cell exists at the given row and column.
     *
     * @param row    row index, starting from 0.
     * @param column column index, starting from 0.
     */
    public void assertCellNotPresent(int row, int column) {
        locator.page().waitForCondition(() -> !cellExists(row, column));
    }

    /**
     * Whether a cell exists at the given row and column. Unlike {@link #findCell(int, int)},
     * this treats an out-of-range column (which {@code findCell} reports by throwing) as "not present".
     */
    private boolean cellExists(int row, int column) {
        try {
            return findCell(row, column).isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Assert that the visible header cells have exactly the given text contents, in order.
     *
     * @param expected the expected header cell text contents
     */
    public void assertHeaderCellContents(String... expected) {
        locator.page().waitForCondition(() -> getHeaderCellContents().equals(List.of(expected)));
    }

    /**
     * Assert that the header cell at the given column has the given text content.
     *
     * @param column   column index, starting from 0.
     * @param expected the expected header cell text content
     */
    public void assertHeaderCell(int column, String expected) {
        var headerCell = findHeaderCell(column);
        if (!headerCell.isPresent()) {
            throw new AssertionError("No header cell found at column " + column);
        }
        assertThat(headerCell.get().getCellContentLocator()).hasText(expected);
    }

    /**
     * Assert that a column with the given header text exists.
     *
     * @param headerText the header text to look for
     */
    public void assertColumnPresent(String headerText) {
        locator.page().waitForCondition(() -> findHeaderCellByText(headerText).isPresent());
    }

    /**
     * Assert that no column with the given header text exists.
     *
     * @param headerText the header text to look for
     */
    public void assertColumnNotPresent(String headerText) {
        locator.page().waitForCondition(() -> !findHeaderCellByText(headerText).isPresent());
    }

    /**
     * Assert that the visible footer cells have exactly the given text contents, in order.
     *
     * @param expected the expected footer cell text contents
     */
    public void assertFooterCellContents(String... expected) {
        locator.page().waitForCondition(() -> getFooterCellContents().equals(List.of(expected)));
    }

    /**
     * Assert that the footer cell at the given column has the given text content.
     *
     * @param column   column index, starting from 0.
     * @param expected the expected footer cell text content
     */
    public void assertFooterCell(int column, String expected) {
        var footerCell = findFooterCell(column);
        if (!footerCell.isPresent()) {
            throw new AssertionError("No footer cell found at column " + column);
        }
        assertThat(footerCell.get().getCellContentLocator()).hasText(expected);
    }

    /**
     * Assert that a footer cell with the given text exists.
     *
     * @param footerText the footer text to look for
     */
    public void assertFooterPresent(String footerText) {
        locator.page().waitForCondition(() -> findFooterCellByText(footerText).isPresent());
    }

    /**
     * Assert that no footer cell with the given text exists.
     *
     * @param footerText the footer text to look for
     */
    public void assertFooterNotPresent(String footerText) {
        locator.page().waitForCondition(() -> !findFooterCellByText(footerText).isPresent());
    }

    /**
     * Assert that a row exists at the given index (auto-scrolling if necessary).
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowPresent(int rowIndex) {
        locator.page().waitForCondition(() -> findRow(rowIndex).isPresent());
    }

    /**
     * Assert that no row exists at the given index.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowNotPresent(int rowIndex) {
        locator.page().waitForCondition(() -> !findRow(rowIndex).isPresent());
    }

    /**
     * Assert that the row with the given index is currently scrolled into view.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowInView(int rowIndex) {
        locator.page().waitForCondition(() -> isRowInView(rowIndex));
    }

    /**
     * Assert that the row with the given index is not currently scrolled into view.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowNotInView(int rowIndex) {
        locator.page().waitForCondition(() -> !isRowInView(rowIndex));
    }

    /**
     * Assert that the cells in the given column with the given text appear at exactly the given row indexes.
     *
     * @param column   column index, starting from 0.
     * @param text     the text to match in the cell content.
     * @param expected the expected row indexes (in order)
     */
    public void assertRowIndexesWithColumnText(int column, String text, Integer... expected) {
        locator.page().waitForCondition(
                () -> findRowIndexesWithColumnText(column, text).equals(List.of(expected)));
    }

    /**
     * Assert that the given number of items are currently selected.
     *
     * @param expected the expected selected item count
     */
    public void assertSelectedItemCount(int expected) {
        locator.page().waitForCondition(() -> getSelectedItemCount() == expected);
    }

    /**
     * Assert that the row at the given index is selected.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowSelected(int rowIndex) {
        locator.page().waitForCondition(
                () -> findRow(rowIndex).map(RowElement::isSelected).orElse(false));
    }

    /**
     * Assert that the row at the given index is not selected.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowNotSelected(int rowIndex) {
        locator.page().waitForCondition(
                () -> findRow(rowIndex).map(row -> !row.isSelected()).orElse(false));
    }

    /**
     * Assert that the select-all checkbox is checked.
     */
    public void assertSelectAllChecked() {
        locator.page().waitForCondition(this::isSelectAllChecked);
    }

    /**
     * Assert that the select-all checkbox is unchecked.
     */
    public void assertSelectAllUnchecked() {
        locator.page().waitForCondition(this::isSelectAllUnchecked);
    }

    /**
     * Assert that the select-all checkbox is indeterminate.
     */
    public void assertSelectAllIndeterminate() {
        locator.page().waitForCondition(this::isSelectAllIndeterminate);
    }

    /**
     * Assert that the details panel of the row at the given index is open.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertDetailsOpen(int rowIndex) {
        locator.page().waitForCondition(
                () -> findRow(rowIndex).map(RowElement::isDetailsOpen).orElse(false));
    }

    /**
     * Assert that the details panel of the row at the given index is closed.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertDetailsClosed(int rowIndex) {
        locator.page().waitForCondition(
                () -> findRow(rowIndex).map(row -> !row.isDetailsOpen()).orElse(false));
    }

    private class RowRangeData {
        Integer min;
        Locator minRow;
        Integer max;
        Locator maxRow;

        public RowRangeData(List<Locator> rows) {
            for (var row : rows) {
                var rowIndexStr = row.getAttribute("aria-rowIndex");
                if (rowIndexStr != null && !rowIndexStr.isEmpty()) {
                    int rowIndex = Integer.parseInt(rowIndexStr);
                    if (min == null || rowIndex < min) {
                        min = rowIndex;
                        minRow = row;
                    }
                    if (max == null || rowIndex > max) {
                        max = rowIndex;
                        maxRow = row;
                    }
                }
            }
        }

        public Integer getMin() {
            return min;
        }

        public Integer getMax() {
            return max;
        }

        public Locator getMinRowLocator() {
            return minRow;
        }

        public Locator getMaxRowLocator() {
            return maxRow;
        }
    }

    /**
     * Represents a cell in the grid, providing access to the table cell (td or th),
     * the cell content (vaadin-grid-cell-content) and the column index.
     */
    public class CellElement {
        private final Locator tableCell;
        private final int columnIndex;
        private final Locator cellContent;
        private final String contentSlotName;

        public CellElement(Locator tableCell, int columnIndex) {
            this.tableCell = tableCell;
            this.columnIndex = columnIndex;

            if (tableCell.count() == 0) {
                throw new IllegalArgumentException("Table cell locator is empty");
            }

            this.contentSlotName = tableCell.locator("slot").first().getAttribute("name");
            this.cellContent = locator.locator("vaadin-grid-cell-content[slot=\"" + contentSlotName + "\"]").first();
        }

        /**
         * Get the locator for the table cell (td or th).
         *
         * @return the locator for the table cell
         */
        public Locator getTableCellLocator() {
            return tableCell;
        }

        /**
         * Get the column index (0-based) of this cell.
         * Returns -1 for details cells.
         *
         * @return the column index
         */
        public int getColumnIndex() {
            return columnIndex;
        }

        /**
         * Get the locator for the cell content (vaadin-grid-cell-content) assigned to this cell.
         *
         * @return the locator for the cell content
         */
        public Locator getCellContentLocator() {
            return cellContent;
        }

        /**
         * Get the name of the slot used for the cell content. This is used for accessing the cell content in the grid's shadow DOM.
         *
         * @return the slot name
         */
        public String getContentSlotName() {
            return contentSlotName;
        }

        /**
         * Click the cell content.
         */
        public void click() {
            cellContent.click();
        }
    }

    /**
     * Represents a header cell in the grid, providing access to the table cell (th),
     * the cell content and sorting.
     */
    public class HeaderCellElement extends CellElement {
        public HeaderCellElement(Locator tableCell, int columnIndex) {
            super(tableCell, columnIndex);
        }

        /**
         * Whether the header cell supports sorting.
         *
         * @return {@code true} if the header cell supports sorting, {@code false} otherwise
         */
        public boolean isSortable() {
            var sorterLocator = getCellContentLocator().locator("vaadin-grid-sorter");
            return sorterLocator.count() > 0;
        }

        /**
         * Click the header cell sorter to sort the column.
         * If the column is not currently sorted, it will be sorted in ascending order.
         */
        public void clickSort() {
            var sorterLocator = getSorterLocator();
            if (sorterLocator.count() == 0) {
                throw new IllegalStateException("Header cell at column index " + getColumnIndex() + " is not sortable");
            }
            sorterLocator.first().click();
            waitForGridToStopLoading();
        }

        /**
         * Whether the column is currently sorted in ascending order.
         *
         * @return {@code true} if the column is sorted in ascending order, {@code false} otherwise
         */
        public boolean isSortAscending() {
            return "asc".equals(getSortDirection());
        }

        /**
         * Whether the column is currently sorted in descending order.
         *
         * @return {@code true} if the column is sorted in descending order, {@code false} otherwise
         */
        public boolean isSortDescending() {
            return "desc".equals(getSortDirection());
        }

        /**
         * Whether the column is currently not sorted.
         *
         * @return {@code true} if the column is not sorted, {@code false} otherwise
         */
        public boolean isNotSorted() {
            var sortDirection = getSortDirection();
            return sortDirection == null || sortDirection.isEmpty();
        }

        /**
         * Assert that the column is sorted in ascending order.
         */
        public void assertSortAscending() {
            locator.page().waitForCondition(this::isSortAscending);
        }

        /**
         * Assert that the column is sorted in descending order.
         */
        public void assertSortDescending() {
            locator.page().waitForCondition(this::isSortDescending);
        }

        /**
         * Assert that the column is not sorted.
         */
        public void assertNotSorted() {
            locator.page().waitForCondition(this::isNotSorted);
        }

        /**
         * Assert that the header cell supports sorting.
         */
        public void assertSortable() {
            locator.page().waitForCondition(this::isSortable);
        }

        /**
         * Assert that the header cell does not support sorting.
         */
        public void assertNotSortable() {
            locator.page().waitForCondition(() -> !isSortable());
        }

        /**
         * Get the current sort direction of the column.
         * Returns "asc" for ascending, "desc" for descending, and null or empty string for not sorted.
         *
         * @return the sort direction, or null/empty if not sorted
         */
        private String getSortDirection() {
            var sorterLocator = getSorterLocator();
            if (sorterLocator.count() == 0) {
                throw new IllegalStateException("Header cell at column index " + getColumnIndex() + " is not sortable");
            }
            return sorterLocator.first().getAttribute("direction");
        }

        /**
         * Get the locator for the vaadin-grid-sorter element in this header cell, if it exists.
         *
         * @return the locator for the vaadin-grid-sorter element, or an empty locator if it doesn't exist
         */
        private Locator getSorterLocator() {
            return getCellContentLocator().locator("vaadin-grid-sorter");
        }
    }

    /**
     * Represents a footer cell in the grid, providing access to the table cell (td)
     * and the cell content.
     */
    public class FooterCellElement extends CellElement {
        public FooterCellElement(Locator tableCell, int columnIndex) {
            super(tableCell, columnIndex);
        }
    }

    /**
     * Represents a row in the grid, providing access to the row locator,
     * row index, and methods for accessing cells and interacting with the row (selection, details).
     */
    public class RowElement {
        private final Locator row;
        private final int rowIndex;

        public RowElement(Locator rowLocator, int rowIndex) {
            if (rowIndex < 0) {
                throw new IllegalArgumentException("Row index must be non-negative");
            }

            this.row = rowLocator;
            this.rowIndex = rowIndex;
        }

        /**
         * Get the locator for the row (tr).
         *
         * @return the locator for the row
         */
        public Locator getRowLocator() {
            return row;
        }

        /**
         * Get the row index (0-based) of this row.
         *
         * @return the row index
         */
        public int getRowIndex() {
            return rowIndex;
        }

        /**
         * Get the cell element for the given column index in this row.
         *
         * @param columnIndex the column index (0-based) of the cell to get
         * @return the cell element for the given column index
         */
        public CellElement getCell(int columnIndex) {
            if (columnIndex < 0) {
                throw new IllegalArgumentException("Column index must be non-negative");
            }

            var column = row.locator("td").nth(columnIndex);
            if (column.count() == 0) {
                throw new IllegalArgumentException("Column with index " + columnIndex + " does not exist in the row " + rowIndex);
            }

            column.scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
            return new CellElement(column, columnIndex);
        }

        /**
         * Get the cell element for the given column header text in this row.
         *
         * @param columnHeaderText the text of the column header
         * @return the cell element for the given column header text
         */
        public CellElement getCell(String columnHeaderText) {
            if (columnHeaderText == null || columnHeaderText.isEmpty()) {
                throw new IllegalArgumentException("Column header text must not be null or empty");
            }
            var foundHeaderCell = findHeaderCellByText(columnHeaderText);
            if (!foundHeaderCell.isPresent()) {
                throw new IllegalArgumentException("Column with header text '" + columnHeaderText + "' does not exist");
            }

            return getCell(foundHeaderCell.get().getColumnIndex());
        }

        /**
         * Get the cell element for the details column in this row.
         *
         * @return the cell element for the details column.
         */
        public CellElement getDetailsCell() {
            var detailsCell = row.locator("td.details-cell").first();
            if (detailsCell.count() == 0) {
                throw new IllegalArgumentException("Details cell does not exist in the row " + rowIndex);
            }

            detailsCell.scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
            return new CellElement(detailsCell, -1);
        }

        /**
         * Whether this row is selected.
         *
         * @return true if the row is selected, false otherwise
         */
        public boolean isSelected() {
            return row.getAttribute("selected") != null;
        }

        /**
         * Select this row.
         */
        public void select() {
            GridElement.this.select(this);
        }

        /**
         * Deselect this row.
         */
        public void deselect() {
            GridElement.this.deselect(this);
        }

        /**
         * Whether the details for this row are open.
         * If you need to use a custom way to open the details,
         * override the {@link GridElement#openDetails(GridElement.Row)} method.
         */
        public void openDetails() {
            GridElement.this.openDetails(this);
        }

        /**
         * Close the details for this row.
         * If you need to use a custom way to close the details,
         * override the {@link GridElement#closeDetails(GridElement.Row)} method.
         */
        public void closeDetails() {
            GridElement.this.closeDetails(this);
        }

        /**
         * Whether the details for this row are open.
         *
         * @return true if the details are open, false otherwise
         */
        public boolean isDetailsOpen() {
            return getRowLocator().getAttribute("details-opened") != null;
        }

        /**
         * Assert that this row is selected.
         */
        public void assertSelected() {
            locator.page().waitForCondition(this::isSelected);
        }

        /**
         * Assert that this row is not selected.
         */
        public void assertNotSelected() {
            locator.page().waitForCondition(() -> !isSelected());
        }

        /**
         * Assert that this row's details panel is open.
         */
        public void assertDetailsOpen() {
            locator.page().waitForCondition(this::isDetailsOpen);
        }

        /**
         * Assert that this row's details panel is closed.
         */
        public void assertDetailsClosed() {
            locator.page().waitForCondition(() -> !isDetailsOpen());
        }
    }
}
