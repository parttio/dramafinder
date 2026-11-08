package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.Position;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.AssertionWaits.assertEventually;

/**
 * PlaywrightElement for {@code <vaadin-grid>}.
 * <p>
 * Provides helpers for scrolling, querying visible rows, accessing cell
 * content, and interacting with the grid's header, body and selection.
 * Cell access uses JavaScript evaluation on the grid's internal APIs
 * since body cells live in shadow DOM and are virtualized.
 * <p>
 * A column index counts the visible (non-hidden) columns in display order,
 * from 0 on the left: hidden columns are skipped, a selection column counts,
 * and after the user drags a column to a new place the indexes follow the
 * new order. A column without header text can be found by its id
 * ({@code Column.setId(..)}) with {@link #getColumnIndexById(String)}.
 */
@PlaywrightElement(GridElement.FIELD_TAG_NAME)
public class GridElement extends VaadinElement
        implements FocusableElement, HasStyleElement, HasThemeElement, HasEnabledElement,
        Findable<ComponentQuery<GridElement>> {

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
     * Find the index of the visible column with the given id, the one set with
     * {@code Column.setId(..)} in Flow. The index counts visible columns in display
     * order (see the class description), so it can be passed to
     * {@link #findCell(int, int)} or {@link #findHeaderCell(int)}. A column key
     * ({@code Column.setKey(..)}) stays on the server and cannot be found this way.
     *
     * @param id the column id, matched exactly
     * @return the 0-based column index; empty if no visible column has that id
     *         (the column does not exist or is hidden)
     */
    public OptionalInt findColumnIndexById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Column id must not be null or empty");
        }
        int index = ((Number) locator.evaluate(
                "(g, id) => g._getColumns().filter(c => !c.hidden)"
                        + ".sort((a, b) => a._order - b._order).findIndex(c => c.id === id)",
                id)).intValue();
        return index < 0 ? OptionalInt.empty() : OptionalInt.of(index);
    }

    /**
     * Get the index of the visible column with the given id, for a column that must
     * exist. {@link #findColumnIndexById(String)} is the variant that may find none.
     *
     * @param id the column id, matched exactly
     * @return the 0-based column index, in display order
     * @throws IllegalArgumentException if no visible column has that id
     */
    public int getColumnIndexById(String id) {
        return findColumnIndexById(id).orElseThrow(() ->
                new IllegalArgumentException("No visible column with id '" + id + "'"));
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
     * Reads the column a rendered page of rows at a time (see {@link #getColumnTexts(int)}), so a
     * lazy grid is scrolled once per page rather than once per row; each cell is compared with
     * {@link #matchesCellText(String, String)}.
     *
     * @param columnIndex the column index to check the cell content in, starting from 0.
     * @param text        the text to match in the cell content.
     * @return list of row indexes where the cell in the given column matches the given text. Empty list if no match is found.
     */
    public List<Integer> findRowIndexesWithColumnText(int columnIndex, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        var texts = getColumnTexts(columnIndex);
        var matchingRows = new ArrayList<Integer>();
        for (int i = 0; i < texts.size(); i++) {
            var cellText = texts.get(i);
            if (cellText != null && matchesCellText(cellText, text)) {
                matchingRows.add(i);
            }
        }

        return matchingRows;
    }

    /**
     * Read the rendered text of every body cell in the given column, from the first row to the
     * last. The grid is read a page at a time: one script reads all rows rendered at once, then
     * the grid jumps to the first row not read yet and waits for it to load. A lazy grid therefore
     * costs one scroll and one round trip per page instead of several per row. A row that is still
     * not rendered after the jump is looked up on its own, as {@link #findRow(int)} does. Leaves
     * the grid scrolled to wherever the last page was.
     *
     * @param columnIndex the column index, starting from 0.
     * @return the cell texts (the {@code innerText} of the cell content), indexed by row; an
     *         entry is {@code null} when that row could not be rendered. Empty for an empty grid.
     * @throws IllegalArgumentException if the column index is negative, or the grid has rows and the
     *                                  column does not exist
     */
    public List<String> getColumnTexts(int columnIndex) {
        if (columnIndex < 0) {
            throw new IllegalArgumentException("Column index must be non-negative");
        }
        int totalRowCount = getTotalRowCount();
        var texts = new ArrayList<String>(Collections.nCopies(totalRowCount, null));
        var read = new boolean[totalRowCount];
        var headerRowCount = getHeaderRowCount();

        readRenderedColumnTexts(columnIndex, headerRowCount, texts, read);
        for (int i = 0; i < totalRowCount; i++) {
            if (read[i]) {
                continue;
            }
            if (scrollToFlatIndex(i)) {
                waitForGridToStopLoading();
                readRenderedColumnTexts(columnIndex, headerRowCount, texts, read);
            }
            if (!read[i]) {
                // Still not rendered (or its cell has no content yet): fall back to the row path
                var row = findRow(i, headerRowCount);
                if (row.isPresent()) {
                    texts.set(i, row.get().getCell(columnIndex).getText());
                }
                read[i] = true;
            }
        }
        return texts;
    }

    /**
     * Read, in one script, the text of the given column in every body row currently rendered
     * (not hidden, not loading), and store it under the row's 0-based index.
     */
    @SuppressWarnings("unchecked")
    private void readRenderedColumnTexts(int columnIndex, int headerRowCount, List<String> texts, boolean[] read) {
        var pairs = (List<List<Object>>) locator.evaluate(
                "(g, [column, headerRows]) => [...g.shadowRoot.querySelectorAll('tbody tr[aria-rowindex]')]"
                        + " .filter(tr => !tr.hidden && !tr.hasAttribute('loading'))"
                        + " .map(tr => {"
                        + "   const cell = tr.querySelectorAll('td')[column];"
                        + "   const slot = cell && cell.querySelector('slot');"
                        + "   const content = slot && g.querySelector(`vaadin-grid-cell-content[slot=\"${slot.name}\"]`);"
                        + "   return [parseInt(tr.getAttribute('aria-rowindex')) - 1 - headerRows,"
                        + "     content ? content.innerText : null]; })"
                        + " .filter(pair => pair[1] !== null)",
                List.of(columnIndex, headerRowCount));
        for (var pair : pairs) {
            int rowIndex = ((Number) pair.get(0)).intValue();
            if (rowIndex >= 0 && rowIndex < read.length) {
                texts.set(rowIndex, (String) pair.get(1));
                read[rowIndex] = true;
            }
        }
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
        var foundRow = locator.locator("tbody tr[aria-rowIndex=\"" + ariaRowIndex + "\"]").first();
        if (foundRow.count() == 0) {
            // Row not rendered: jump straight to it instead of paging through the
            // rows in between, which could give up on a slow render
            if (!scrollToFlatIndex(rowIndex)) {
                return Optional.empty();
            }
            waitForGridToStopLoading();
            if (foundRow.count() == 0) {
                return Optional.empty();
            }
        }
        foundRow.scrollIntoViewIfNeeded();
        waitForGridToStopLoading();

        return Optional.of(new RowElement(foundRow, rowIndex));
    }

    /**
     * Scroll the grid so that the row at the given flat index gets rendered.
     *
     * @param flatIndex 0-based index among all rows, counting expanded children in a tree grid
     * @return {@code false} if the index is beyond the last row, {@code true} otherwise
     */
    private boolean scrollToFlatIndex(int flatIndex) {
        return (boolean) locator.evaluate(
                "(g, i) => { if (i >= g._flatSize) return false; g._scrollToFlatIndex(i); return true; }",
                flatIndex);
    }

    /**
     * Determine if a body cell matches the given text, by comparing its rendered text with
     * {@link #matchesCellText(String, String)}.
     *
     * @param cell the body cell to check
     * @param text the text to match in the cell content
     * @return {@code true} if the cell matches the text, {@code false} otherwise
     * @deprecated {@link #findRowIndexesWithColumnText(int, String)} reads a whole column at once
     *             and no longer calls this method; override {@link #matchesCellText(String, String)}
     *             to customise matching.
     */
    @Deprecated(since = "2.0.1", forRemoval = true)
    protected boolean matchesCellContent(CellElement cell, String text) {
        cell.getCellContentLocator().scrollIntoViewIfNeeded(); // Scroll into view, to make sure its rendered
        return matchesCellText(cell.getCellContentLocator().innerText(), text);
    }

    /**
     * Determine if a body cell's rendered text matches the given text. By default, the two must
     * be equal. Override for custom matching logic (e.g. ignoring case, or matching only a part of
     * the text).
     *
     * @param cellText the {@code innerText} of the cell content, never {@code null}
     * @param text     the text to match
     * @return {@code true} if the cell text matches, {@code false} otherwise
     */
    protected boolean matchesCellText(String cellText, String text) {
        return Objects.equals(cellText, text);
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
     * Select a row by index, clicking it like a user would.
     * Does nothing if the row is already selected.
     *
     * @param rowIndex index of the row to select, starting from 0.
     * @throws IllegalArgumentException if no row exists at the given index
     * @throws AssertionError           if the row is not selected after the click, e.g. because the
     *                                  grid has no selection or the server rejected it
     */
    public void select(int rowIndex) {
        select(requireRow(rowIndex));
    }

    /**
     * Select the row.
     * You can override this method to implement custom selection logic,
     * for example if you want to select by clicking some other cell than the first one,
     * or if you want to use some modifier keys for selection.
     *
     * @param row row to select.
     * @throws AssertionError if the row is not selected after the click
     */
    protected void select(RowElement row) {
        if (!row.isSelected()) {
            clickRowForSelection(row);
            assertEventually(locator.page(), "grid row " + row.getRowIndex()
                    + " to be selected after clicking it", row::isSelected);
        }
    }

    /**
     * Deselect a row by index, clicking it like a user would.
     * Does nothing if the row is not selected.
     *
     * @param rowIndex index of the row to deselect, starting from 0.
     * @throws IllegalArgumentException if no row exists at the given index
     * @throws AssertionError           if the row is still selected after the click, e.g. because
     *                                  the grid does not allow deselecting
     */
    public void deselect(int rowIndex) {
        deselect(requireRow(rowIndex));
    }

    private RowElement requireRow(int rowIndex) {
        return findRow(rowIndex).orElseThrow(() -> new IllegalArgumentException(
                "No row at index " + rowIndex + ": the grid has " + getTotalRowCount() + " rows"));
    }

    /**
     * Deselect the row.
     * You can override this method to implement custom selection logic,
     * for example if you want to deselect by clicking some other cell than the first one,
     * or if you want to use some modifier keys for deselection.
     *
     * @param row row to deselect.
     * @throws AssertionError if the row is still selected after the click
     */
    protected void deselect(RowElement row) {
        if (row.isSelected()) {
            clickRowForSelection(row);
            assertEventually(locator.page(), "grid row " + row.getRowIndex()
                    + " not to be selected after clicking it", () -> !row.isSelected());
        }
    }

    /**
     * Toggle the selection of a row by clicking it like a user would.
     * Clicks the selection checkbox of the first cell when there is one. Otherwise clicks the first
     * cell that has a spot where a click reaches the grid rather than a component rendered in the
     * cell, usually its padding, so a cell holding a link or a button still selects the row: the grid
     * ignores clicks on links, buttons and other focusable content, which would only trigger that
     * component.
     * You can override this method to choose the cell to click yourself.
     *
     * @param row row to select or deselect
     * @throws IllegalStateException if interactive content covers every cell of the row, padding included
     */
    protected void clickRowForSelection(RowElement row) {
        var firstCell = row.getCell(0);
        if (firstCell.getCellContentLocator().locator("vaadin-checkbox").count() > 0) {
            clickCellForSelection(firstCell);
            return;
        }
        var cellCount = row.getRowLocator().locator("td:not(.details-cell)").count();
        for (int i = 0; i < cellCount; i++) {
            var cell = i == 0 ? firstCell : row.getCell(i);
            if (findFreeClickPosition(cell).isPresent()) {
                clickCellForSelection(cell);
                return;
            }
        }
        throw new IllegalStateException("Cannot select row " + row.getRowIndex() + ": interactive content "
                + "covers every cell, padding included, so any click would hit a component rendered in it; "
                + "override clickRowForSelection or clickCellForSelection to select it another way");
    }

    /**
     * Click the given cell to toggle the selection of its row.
     * Clicks the selection checkbox when the cell holds one, otherwise a spot of the cell that is
     * not covered by interactive content, typically its padding.
     * You can override this method to implement custom selection logic for given cell.
     *
     * @param cell the body cell to click
     * @throws IllegalStateException if interactive content covers the whole cell, padding included
     */
    protected void clickCellForSelection(CellElement cell) {
        var checkbox = cell.getCellContentLocator().locator("vaadin-checkbox");
        if (checkbox.count() > 0) {
            checkbox.click();
            waitAfterAction();
        } else {
            var position = findFreeClickPosition(cell).orElseThrow(() -> new IllegalStateException(
                    "Cannot click cell " + cell.getColumnIndex() + " for selection: interactive content "
                            + "covers the whole cell, padding included"));
            cell.getCellContentLocator().click(new Locator.ClickOptions().setPosition(position));
            waitAfterAction();
        }
        waitForGridToStopLoading();
    }

    /**
     * Find a spot of the cell content where a click reaches the grid rather than a component rendered
     * in the cell: one that hits the cell content itself or non-interactive content in it. Tries the
     * middle of the left padding first, then the top left corner, then the centre.
     *
     * @param cell the body cell to check
     * @return the position relative to the cell content, empty if every spot hits interactive content
     */
    private Optional<Position> findFreeClickPosition(CellElement cell) {
        var point = cell.getCellContentLocator().evaluate("""
                c => {
                    const r = c.getBoundingClientRect();
                    if (!r.width || !r.height) return null;
                    const padding = parseFloat(getComputedStyle(c).paddingLeft) || 0;
                    const points = [[Math.max(1, padding / 2), r.height / 2], [1, 1], [r.width / 2, r.height / 2]];
                    const interactive = 'a[href], button, input, select, textarea, label, [tabindex], [contenteditable]';
                    for (const [x, y] of points) {
                        const hit = document.elementFromPoint(r.left + x, r.top + y);
                        if (!hit || !c.contains(hit)) continue;
                        let safe = true;
                        for (let e = hit; e !== c; e = e.parentElement) {
                            // Custom elements may be interactive inside their shadow root
                            if (e.matches(interactive) || e.localName.includes('-')) {
                                safe = false;
                                break;
                            }
                        }
                        if (safe) return { x, y };
                    }
                    return null;
                }
                """);
        if (point instanceof Map<?, ?> map) {
            return Optional.of(new Position(((Number) map.get("x")).doubleValue(),
                    ((Number) map.get("y")).doubleValue()));
        }
        return Optional.empty();
    }

    /**
     * Find where to click a row so the grid sees a click on the row: a free spot of the first data
     * cell, skipping the selection column and cells that interactive content covers entirely.
     *
     * @param row    the row to click
     * @param action what the click is for, used in the failure message
     * @return the cell content to click and the position within it
     * @throws IllegalStateException if no data cell of the row has a free spot
     */
    private RowClickTarget findRowClickTarget(RowElement row, String action) {
        var cellCount = row.getRowLocator().locator("td:not(.details-cell)").count();
        for (int i = 0; i < cellCount; i++) {
            var cell = row.getCell(i);
            if (isSelectionColumnCell(cell)) {
                continue;
            }
            var position = findFreeClickPosition(cell);
            if (position.isPresent()) {
                return new RowClickTarget(cell.getCellContentLocator(), position.get());
            }
        }
        throw new IllegalStateException("Cannot " + action + " row " + row.getRowIndex()
                + ": the row has no data cell with a spot free of interactive content, padding included");
    }

    private static boolean isSelectionColumnCell(CellElement cell) {
        return (boolean) cell.getTableCellLocator().evaluate(
                "td => !!td._column && td._column.localName.endsWith('selection-column')");
    }

    private record RowClickTarget(Locator cellContent, Position position) {
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
        waitAfterAction();
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
        waitAfterAction();
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
     * Open the details of a row by clicking it (see {@link RowElement#click()}), which relies on the
     * grid toggling details on click: Flow's {@code Grid.setDetailsVisibleOnClick(true)}, the default.
     * Does nothing if the details are already open. In a single-select grid the click also selects
     * the row.
     * Override this method if the details are opened another way, for example by a button.
     *
     * @param row row for which to open details
     * @throws AssertionError if the details are not open after the click
     */
    protected void openDetails(RowElement row) {
        if (row.isDetailsOpen()) {
            return;
        }

        row.click();
        assertEventually(locator.page(), "grid row " + row.getRowIndex() + " to have its details open"
                + " after clicking it (details open on click only with setDetailsVisibleOnClick(true);"
                + " override openDetails to open them another way)", row::isDetailsOpen);
    }

    /**
     * Close the details of a row by clicking it (see {@link RowElement#click()}), which relies on the
     * grid toggling details on click: Flow's {@code Grid.setDetailsVisibleOnClick(true)}, the default.
     * Does nothing if the details are already closed.
     * Override this method if the details are closed another way, for example by a button.
     *
     * @param row row for which to close details
     * @throws AssertionError if the details are still open after the click
     */
    protected void closeDetails(RowElement row) {
        if (!row.isDetailsOpen()) {
            return;
        }

        row.click();
        assertEventually(locator.page(), "grid row " + row.getRowIndex() + " to have its details closed"
                + " after clicking it (details toggle on click only with setDetailsVisibleOnClick(true);"
                + " override closeDetails to close them another way)", () -> !row.isDetailsOpen());
    }

    /**
     * Wait for the grid to finish loading after a scroll or other action that triggers loading of new rows.
     * Returns without waiting when the grid is no longer in the DOM, e.g. because a click on a cell
     * navigated to another view.
     */
    public void waitForGridToStopLoading() {
        if (locator.count() == 0) {
            return;
        }
        try {
            var grid = locator.elementHandle();
            locator.page().waitForFunction("g => !g.isConnected || !g.hasAttribute('loading')", grid);
            grid.evaluate("g => g.isConnected && g.updateComplete.then(() => new Promise(r => requestAnimationFrame(() => requestAnimationFrame(r))))");
        } catch (PlaywrightException e) {
            // The grid was removed, or the page navigated, while waiting
            if (locator.count() > 0) {
                throw e;
            }
        }
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
        assertEventually(locator.page(), "Grid row count", this::getTotalRowCount, expected);
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
        assertEventually(locator.page(), "Grid column count", this::getColumnCount, expected);
    }

    /**
     * Assert that {@code allRowsVisible} is enabled.
     */
    public void assertAllRowsVisible() {
        assertEventually(locator.page(), "grid to have all rows visible", this::isAllRowsVisible);
    }

    /**
     * Assert that {@code allRowsVisible} is not enabled.
     */
    public void assertNotAllRowsVisible() {
        assertEventually(locator.page(), "grid not to have all rows visible", () -> !isAllRowsVisible());
    }

    /**
     * Assert that multi-sort is enabled.
     */
    public void assertMultiSort() {
        assertEventually(locator.page(), "grid to be multi-sort", this::isMultiSort);
    }

    /**
     * Assert that multi-sort is not enabled.
     */
    public void assertNotMultiSort() {
        assertEventually(locator.page(), "grid not to be multi-sort", () -> !isMultiSort());
    }

    /**
     * Assert that column reordering is allowed.
     */
    public void assertColumnReorderingAllowed() {
        assertEventually(locator.page(), "grid to allow column reordering", this::isColumnReorderingAllowed);
    }

    /**
     * Assert that column reordering is not allowed.
     */
    public void assertColumnReorderingNotAllowed() {
        assertEventually(locator.page(), "grid not to allow column reordering", () -> !isColumnReorderingAllowed());
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
        assertEventually(locator.page(), "grid cell at row " + row + ", column " + column + " to be present",
                () -> cellExists(row, column));
    }

    /**
     * Assert that no cell exists at the given row and column.
     *
     * @param row    row index, starting from 0.
     * @param column column index, starting from 0.
     */
    public void assertCellNotPresent(int row, int column) {
        assertEventually(locator.page(), "grid cell at row " + row + ", column " + column + " not to be present",
                () -> !cellExists(row, column));
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
        assertEventually(locator.page(), "Grid header cell contents", this::getHeaderCellContents,
                List.of(expected));
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
        assertEventually(locator.page(), "Grid header texts", this::getHeaderCellContents,
                contents -> findHeaderCellByText(headerText).isPresent(),
                "a header cell with text \"" + headerText + "\"");
    }

    /**
     * Assert that no column with the given header text exists.
     *
     * @param headerText the header text to look for
     */
    public void assertColumnNotPresent(String headerText) {
        assertEventually(locator.page(), "Grid header texts", this::getHeaderCellContents,
                contents -> findHeaderCellByText(headerText).isEmpty(),
                "no header cell with text \"" + headerText + "\"");
    }

    /**
     * Assert that the visible footer cells have exactly the given text contents, in order.
     *
     * @param expected the expected footer cell text contents
     */
    public void assertFooterCellContents(String... expected) {
        assertEventually(locator.page(), "Grid footer cell contents", this::getFooterCellContents,
                List.of(expected));
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
        assertEventually(locator.page(), "Grid footer texts", this::getFooterCellContents,
                contents -> findFooterCellByText(footerText).isPresent(),
                "a footer cell with text \"" + footerText + "\"");
    }

    /**
     * Assert that no footer cell with the given text exists.
     *
     * @param footerText the footer text to look for
     */
    public void assertFooterNotPresent(String footerText) {
        assertEventually(locator.page(), "Grid footer texts", this::getFooterCellContents,
                contents -> findFooterCellByText(footerText).isEmpty(),
                "no footer cell with text \"" + footerText + "\"");
    }

    /**
     * Assert that a row exists at the given index (auto-scrolling if necessary).
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowPresent(int rowIndex) {
        assertEventually(locator.page(), "Grid row count", this::getTotalRowCount,
                count -> findRow(rowIndex).isPresent(), "a row at index " + rowIndex);
    }

    /**
     * Assert that no row exists at the given index.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowNotPresent(int rowIndex) {
        assertEventually(locator.page(), "Grid row count", this::getTotalRowCount,
                count -> findRow(rowIndex).isEmpty(), "no row at index " + rowIndex);
    }

    /**
     * Assert that the row with the given index is currently scrolled into view.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowInView(int rowIndex) {
        assertEventually(locator.page(), "grid row " + rowIndex + " to be in view", () -> isRowInView(rowIndex));
    }

    /**
     * Assert that the row with the given index is not currently scrolled into view.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowNotInView(int rowIndex) {
        assertEventually(locator.page(), "grid row " + rowIndex + " not to be in view", () -> !isRowInView(rowIndex));
    }

    /**
     * Assert that the cells in the given column with the given text appear at exactly the given row indexes.
     *
     * @param column   column index, starting from 0.
     * @param text     the text to match in the cell content.
     * @param expected the expected row indexes (in order)
     */
    public void assertRowIndexesWithColumnText(int column, String text, Integer... expected) {
        assertEventually(locator.page(),
                "Grid row indexes with text \"" + text + "\" in column " + column,
                () -> findRowIndexesWithColumnText(column, text), List.of(expected));
    }

    /**
     * Assert that the given number of items are currently selected.
     *
     * @param expected the expected selected item count
     */
    public void assertSelectedItemCount(int expected) {
        assertEventually(locator.page(), "Grid selected item count", this::getSelectedItemCount, expected);
    }

    /**
     * Assert that the row at the given index is selected.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowSelected(int rowIndex) {
        assertEventually(locator.page(), "grid row " + rowIndex + " to be selected",
                () -> findRow(rowIndex).map(RowElement::isSelected).orElse(false));
    }

    /**
     * Assert that the row at the given index is not selected.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertRowNotSelected(int rowIndex) {
        assertEventually(locator.page(), "grid row " + rowIndex + " to be present and not selected",
                () -> findRow(rowIndex).map(row -> !row.isSelected()).orElse(false));
    }

    /**
     * Assert that the select-all checkbox is checked.
     */
    public void assertSelectAllChecked() {
        assertEventually(locator.page(), "grid select-all checkbox to be checked", this::isSelectAllChecked);
    }

    /**
     * Assert that the select-all checkbox is unchecked.
     */
    public void assertSelectAllUnchecked() {
        assertEventually(locator.page(), "grid select-all checkbox to be unchecked", this::isSelectAllUnchecked);
    }

    /**
     * Assert that the select-all checkbox is indeterminate.
     */
    public void assertSelectAllIndeterminate() {
        assertEventually(locator.page(), "grid select-all checkbox to be indeterminate", this::isSelectAllIndeterminate);
    }

    /**
     * Assert that the details panel of the row at the given index is open.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertDetailsOpen(int rowIndex) {
        assertEventually(locator.page(), "grid row " + rowIndex + " to have its details open",
                () -> findRow(rowIndex).map(RowElement::isDetailsOpen).orElse(false));
    }

    /**
     * Assert that the details panel of the row at the given index is closed.
     *
     * @param rowIndex row index, starting from 0.
     */
    public void assertDetailsClosed(int rowIndex) {
        assertEventually(locator.page(), "grid row " + rowIndex + " to be present with its details closed",
                () -> findRow(rowIndex).map(row -> !row.isDetailsOpen()).orElse(false));
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
         * Get the rendered text of the cell content.
         *
         * @return the text of the cell content
         */
        public String getText() {
            return cellContent.innerText();
        }

        /**
         * Assert that the cell content has the given text.
         *
         * @param expected the expected cell text content
         */
        public void assertText(String expected) {
            assertThat(cellContent).hasText(expected);
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
            waitAfterAction();
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
            waitAfterAction();
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
            assertEventually(locator.page(), "Sort direction of column " + getColumnIndex(),
                    this::sortDirectionForMessage, "asc"::equals, "asc");
        }

        /**
         * Assert that the column is sorted in descending order.
         */
        public void assertSortDescending() {
            assertEventually(locator.page(), "Sort direction of column " + getColumnIndex(),
                    this::sortDirectionForMessage, "desc"::equals, "desc");
        }

        /**
         * Assert that the column is not sorted.
         */
        public void assertNotSorted() {
            assertEventually(locator.page(), "Sort direction of column " + getColumnIndex(),
                    this::sortDirectionForMessage, "none"::equals, "none");
        }

        /**
         * Assert that the header cell supports sorting.
         */
        public void assertSortable() {
            assertEventually(locator.page(), "column " + getColumnIndex() + " to be sortable", this::isSortable);
        }

        /**
         * Assert that the header cell does not support sorting.
         */
        public void assertNotSortable() {
            assertEventually(locator.page(), "column " + getColumnIndex() + " not to be sortable",
                    () -> !isSortable());
        }

        /**
         * Get the current sort direction of the column.
         * Returns "asc" for ascending, "desc" for descending, and null or empty string for not sorted.
         *
         * @return the sort direction, or null/empty if not sorted
         */
        // The sort direction as shown in an assertion message: "none" when unsorted.
        private String sortDirectionForMessage() {
            return isNotSorted() ? "none" : getSortDirection();
        }

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
         * Click this row like a user would, firing the grid's item click.
         * Clicks the first data cell, skipping the selection column, at a spot free of interactive
         * content (typically its padding), so a button or link rendered in the cell is not
         * triggered; falls back to the next data cell when interactive content covers a cell
         * entirely. As for a user, the click also toggles the row's selection in a single-select
         * grid.
         *
         * @throws IllegalStateException if interactive content covers every data cell of the row
         */
        public void click() {
            var target = findRowClickTarget(this, "click");
            target.cellContent().click(new Locator.ClickOptions().setPosition(target.position()));
            waitAfterAction();
            waitForGridToStopLoading();
        }

        /**
         * Double-click this row like a user would, firing the grid's item double click.
         * Picks the spot to click the same way as {@link #click()}.
         *
         * @throws IllegalStateException if interactive content covers every data cell of the row
         */
        public void doubleClick() {
            var target = findRowClickTarget(this, "double-click");
            target.cellContent().dblclick(new Locator.DblclickOptions().setPosition(target.position()));
            waitAfterAction();
            waitForGridToStopLoading();
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
         * Select this row by clicking it like a user would; see {@link GridElement#select(int)}.
         *
         * @throws AssertionError if the row is not selected after the click
         */
        public void select() {
            GridElement.this.select(this);
        }

        /**
         * Deselect this row by clicking it like a user would; see {@link GridElement#deselect(int)}.
         *
         * @throws AssertionError if the row is still selected after the click
         */
        public void deselect() {
            GridElement.this.deselect(this);
        }

        /**
         * Open the details for this row by clicking it.
         * If you need to use a custom way to open the details,
         * override the {@link GridElement#openDetails(RowElement)} method.
         *
         * @throws AssertionError if the details are not open after the click
         */
        public void openDetails() {
            GridElement.this.openDetails(this);
        }

        /**
         * Close the details for this row by clicking it.
         * If you need to use a custom way to close the details,
         * override the {@link GridElement#closeDetails(RowElement)} method.
         *
         * @throws AssertionError if the details are still open after the click
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
            assertEventually(locator.page(), "grid row " + rowIndex + " to be selected", this::isSelected);
        }

        /**
         * Assert that this row is not selected.
         */
        public void assertNotSelected() {
            assertEventually(locator.page(), "grid row " + rowIndex + " not to be selected", () -> !isSelected());
        }

        /**
         * Assert that this row's details panel is open.
         */
        public void assertDetailsOpen() {
            assertEventually(locator.page(), "grid row " + rowIndex + " to have its details open", this::isDetailsOpen);
        }

        /**
         * Assert that this row's details panel is closed.
         */
        public void assertDetailsClosed() {
            assertEventually(locator.page(), "grid row " + rowIndex + " to have its details closed", () -> !isDetailsOpen());
        }
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code GridElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code GridElement}
     */
    public static GridElement getById(Page page, String id) {
        return new GridElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code GridElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code GridElement}
     */
    public static GridElement getById(Locator locator, String id) {
        return new GridElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code GridElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code GridElement}
     */
    public static GridElement getByTestId(Page page, String testId) {
        return new GridElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code GridElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code GridElement}
     */
    public static GridElement getByTestId(Locator locator, String testId) {
        return new GridElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
