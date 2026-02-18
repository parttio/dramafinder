package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasGridDetailsElement;
import org.vaadin.addons.dramafinder.element.shared.HasGridSelectionElement;
import org.vaadin.addons.dramafinder.element.shared.HasGridSortingElement;
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
        implements FocusableElement, HasStyleElement, HasThemeElement, HasEnabledElement,
        HasGridSelectionElement, HasGridSortingElement, HasGridDetailsElement {

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
     * Get the total number of rows (data items) in the grid.
     *
     * @return total item count
     */
    public int getRowCount() {
        Object value = getProperty("size");
        return value == null ? 0 : ((Number) value).intValue();
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
     * Get the index of the first row that is at least partially visible.
     *
     * @return 0-based index of the first visible row
     */
    public int getFirstVisibleRowIndex() {
        return ((Number) locator.evaluate(
                "el => el._firstVisibleIndex")).intValue();
    }

    /**
     * Get the index of the last row that is at least partially visible.
     *
     * @return 0-based index of the last visible row
     */
    public int getLastVisibleRowIndex() {
        return ((Number) locator.evaluate(
                "el => el._lastVisibleIndex")).intValue();
    }

    /**
     * Get the number of currently visible rows.
     *
     * @return count of rows visible in the viewport
     */
    public int getVisibleRowCount() {
        return ((Number) locator.evaluate(
                "el => el._lastVisibleIndex - el._firstVisibleIndex + 1")).intValue();
    }

    /**
     * Whether the given row index is currently visible in the viewport.
     *
     * @param rowIndex the 0-based row index to check
     * @return {@code true} if the row is between first and last visible index
     */
    public boolean isRowInView(int rowIndex) {
        return (boolean) locator.evaluate(
                "(el, idx) => el._firstVisibleIndex <= idx && idx <= el._lastVisibleIndex",
                rowIndex);
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
     * Get the text content of a header cell by column index.
     *
     * @param colIndex 0-based visible column index
     * @return the header cell text content
     */
    public String getHeaderCellContent(int colIndex) {
        return getHeaderCellLocator(colIndex).textContent().trim();
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
            headers.add(getHeaderCellContent(i));
        }
        return headers;
    }

    /**
     * Get a Playwright locator for the header cell content at the given column index.
     *
     * @param colIndex 0-based visible column index
     * @return locator for the header cell content element
     */
    public Locator getHeaderCellLocator(int colIndex) {
        String slot = (String) locator.evaluate(
                "(el, colIdx) => {"
                        + "  const cols = el._getColumns().filter(c => !c.hidden);"
                        + "  return cols[colIdx]._headerCell._content.getAttribute('slot');"
                        + "}", colIndex);
        return locator.locator("vaadin-grid-cell-content[slot='" + slot + "']");
    }

    /**
     * Get a Playwright locator for the header cell content matching the given header text.
     *
     * @param headerText the header text to match
     * @return locator for the header cell content element
     */
    public Locator getHeaderCellLocator(String headerText) {
        return getHeaderCellLocator(resolveColumnIndex(headerText));
    }

    // ── Cell Content Access ────────────────────────────────────────────

    /**
     * Get the text content of a body cell by row and column index.
     * The row must be in view (scrolled to).
     *
     * @param rowIndex 0-based data row index
     * @param colIndex 0-based visible column index
     * @return the cell text content
     */
    public String getCellContent(int rowIndex, int colIndex) {
        return (String) locator.evaluate(
                "(el, args) => {"
                        + "  const rowIdx = args[0];"
                        + "  const colIdx = args[1];"
                        + "  const cols = el._getColumns().filter(c => !c.hidden);"
                        + "  const col = cols[colIdx];"
                        + "  const rows = el.$.items.children;"
                        + "  for (let i = 0; i < rows.length; i++) {"
                        + "    const row = rows[i];"
                        + "    if (row.index === rowIdx) {"
                        + "      const cells = [...row.children];"
                        + "      for (const cell of cells) {"
                        + "        if (cell._column === col) {"
                        + "          return cell._content.textContent.trim();"
                        + "        }"
                        + "      }"
                        + "    }"
                        + "  }"
                        + "  return null;"
                        + "}", List.of(rowIndex, colIndex));
    }

    /**
     * Get the text content of a body cell by row index and header text.
     * The row must be in view (scrolled to).
     *
     * @param rowIndex   0-based data row index
     * @param headerText the header text to match
     * @return the cell text content
     */
    public String getCellContent(int rowIndex, String headerText) {
        return getCellContent(rowIndex, resolveColumnIndex(headerText));
    }

    /**
     * Get a Playwright locator for the body cell content at the given row and column index.
     * The row must be in view (scrolled to). Useful for interacting with
     * component renderers.
     *
     * @param rowIndex 0-based data row index
     * @param colIndex 0-based visible column index
     * @return locator for the cell content element
     */
    public Locator getCellContentLocator(int rowIndex, int colIndex) {
        // Tag the target cell content with a unique data attribute, then locate it
        String markerId = "gc-" + rowIndex + "-" + colIndex + "-" + System.nanoTime();
        String js = "(el, args) => {"
                + "  const rowIdx = args[0];"
                + "  const colIdx = args[1];"
                + "  const marker = args[2];"
                + "  const cols = el._getColumns().filter(c => !c.hidden);"
                + "  const col = cols[colIdx];"
                + "  const rows = el.$.items.children;"
                + "  for (let i = 0; i < rows.length; i++) {"
                + "    const row = rows[i];"
                + "    if (row.index === rowIdx) {"
                + "      const cells = [...row.children];"
                + "      for (const cell of cells) {"
                + "        if (cell._column === col) {"
                + "          cell._content.setAttribute('data-grid-cell-id', marker);"
                + "          return true;"
                + "        }"
                + "      }"
                + "    }"
                + "  }"
                + "  return false;"
                + "}";
        locator.evaluate(js, List.of(rowIndex, colIndex, markerId));
        return locator.page().locator("[data-grid-cell-id='" + markerId + "']");
    }

    /**
     * Get a Playwright locator for the body cell content by row index and header text.
     *
     * @param rowIndex   0-based data row index
     * @param headerText the header text to match
     * @return locator for the cell content element
     */
    public Locator getCellContentLocator(int rowIndex, String headerText) {
        return getCellContentLocator(rowIndex, resolveColumnIndex(headerText));
    }

    // ── Scroll Actions ─────────────────────────────────────────────────

    /**
     * Scroll the grid so that the given row index becomes visible.
     * Waits for the row to be rendered in the DOM before returning.
     *
     * @param rowIndex the 0-based row index to scroll to
     */
    public void scrollToRow(int rowIndex) {
        locator.evaluate("(el, idx) => el.scrollToIndex(idx)", rowIndex);
        // Wait until the target row is actually rendered in the DOM with correct index
        locator.page().waitForCondition(() -> (boolean) locator.evaluate(
                "(el, idx) => {"
                        + "  const rows = el.$.items.children;"
                        + "  for (let i = 0; i < rows.length; i++) {"
                        + "    if (rows[i].index === idx) return true;"
                        + "  }"
                        + "  return false;"
                        + "}", rowIndex));
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
        locator.evaluate(
                "el => {"
                        + "  if (el.size > 0) el.scrollToIndex(el.size - 1);"
                        + "}");
    }

    // ── Assertions ─────────────────────────────────────────────────────

    /**
     * Assert the total number of rows matches the expected count.
     *
     * @param expected expected row count
     */
    public void assertRowCount(int expected) {
        assertThat(locator).hasJSProperty("size", expected);
    }

    /**
     * Assert the grid has zero rows.
     */
    public void assertEmpty() {
        assertRowCount(0);
    }

    /**
     * Assert the number of visible columns matches the expected count.
     *
     * @param expected expected column count
     */
    public void assertColumnCount(int expected) {
        locator.page().waitForCondition(() -> getColumnCount() == expected);
    }

    /**
     * Assert that the given row index is currently visible.
     *
     * @param rowIndex the 0-based row index
     */
    public void assertRowInView(int rowIndex) {
        locator.page().waitForCondition(() -> isRowInView(rowIndex));
    }

    /**
     * Assert that the given row index is NOT currently visible.
     *
     * @param rowIndex the 0-based row index
     */
    public void assertRowNotInView(int rowIndex) {
        locator.page().waitForCondition(() -> !isRowInView(rowIndex));
    }

    /**
     * Assert that the first visible row index equals the expected value.
     *
     * @param expected expected first visible row index
     */
    public void assertFirstVisibleRow(int expected) {
        assertThat(locator).hasJSProperty("_firstVisibleIndex", expected);
    }

    /**
     * Assert that the last visible row index equals the expected value.
     *
     * @param expected expected last visible row index
     */
    public void assertLastVisibleRow(int expected) {
        assertThat(locator).hasJSProperty("_lastVisibleIndex", expected);
    }

    /**
     * Assert the text content of a body cell matches the expected value.
     *
     * @param rowIndex 0-based data row index
     * @param colIndex 0-based visible column index
     * @param expected expected text content
     */
    public void assertCellContent(int rowIndex, int colIndex, String expected) {
        locator.page().waitForCondition(
                () -> expected.equals(getCellContent(rowIndex, colIndex)));
    }

    /**
     * Assert the text content of a body cell (by header text) matches the expected value.
     *
     * @param rowIndex   0-based data row index
     * @param headerText the header text to match
     * @param expected   expected text content
     */
    public void assertCellContent(int rowIndex, String headerText, String expected) {
        locator.page().waitForCondition(
                () -> expected.equals(getCellContent(rowIndex, headerText)));
    }

    /**
     * Assert the text content of a header cell matches the expected value.
     *
     * @param colIndex 0-based visible column index
     * @param expected expected header text
     */
    public void assertHeaderCellContent(int colIndex, String expected) {
        assertThat(getHeaderCellLocator(colIndex)).hasText(expected);
    }

    /**
     * Assert the grid has {@code allRowsVisible} enabled.
     */
    public void assertAllRowsVisible() {
        assertThat(locator).hasJSProperty("allRowsVisible", true);
    }

    /**
     * Assert the grid does NOT have {@code allRowsVisible} enabled.
     */
    public void assertNotAllRowsVisible() {
        assertThat(locator).not().hasJSProperty("allRowsVisible", true);
    }

    // ── Internal ───────────────────────────────────────────────────────

    /**
     * Resolve a header text to its visible column index.
     *
     * @param headerText the header text to find
     * @return the 0-based column index
     * @throws IllegalArgumentException if no column with that header text is found
     */
    public int resolveColumnIndex(String headerText) {
        Object result = locator.evaluate(
                "(el, text) => {"
                        + "  const cols = el._getColumns().filter(c => !c.hidden);"
                        + "  for (let i = 0; i < cols.length; i++) {"
                        + "    const headerCell = cols[i]._headerCell;"
                        + "    const content = headerCell._content;"
                        + "    if (content.textContent.trim() === text) return i;"
                        + "  }"
                        + "  return -1;"
                        + "}", headerText);
        int index = ((Number) result).intValue();
        if (index < 0) {
            throw new IllegalArgumentException(
                    "No column found with header text: " + headerText);
        }
        return index;
    }
}
