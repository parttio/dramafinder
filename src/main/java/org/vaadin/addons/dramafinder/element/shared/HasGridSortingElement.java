package org.vaadin.addons.dramafinder.element.shared;

/**
 * Mixin for grid components that support column sorting.
 */
public interface HasGridSortingElement extends HasLocatorElement {

    /**
     * Resolve a header text to its visible column index.
     *
     * @param headerText the header text to find
     * @return the 0-based column index
     */
    int resolveColumnIndex(String headerText);

    /**
     * Click the sorter element in the header cell at the given column index.
     *
     * @param colIndex 0-based visible column index
     */
    default void clickHeaderToSort(int colIndex) {
        getLocator().evaluate(
                "(el, colIdx) => {"
                        + "  const cols = el._getColumns().filter(c => !c.hidden);"
                        + "  const col = cols[colIdx];"
                        + "  const headerCell = col._headerCell;"
                        + "  const content = headerCell._content;"
                        + "  const sorter = content.querySelector('vaadin-grid-sorter');"
                        + "  if (sorter) sorter.click();"
                        + "}", colIndex);
    }

    /**
     * Click the sorter element in the header cell matching the given text.
     *
     * @param headerText the header text to match
     */
    default void clickHeaderToSort(String headerText) {
        clickHeaderToSort(resolveColumnIndex(headerText));
    }

    /**
     * Get the current sort direction for the column at the given index.
     *
     * @param colIndex 0-based visible column index
     * @return {@code "asc"}, {@code "desc"}, or {@code null} if unsorted
     */
    default String getSortDirection(int colIndex) {
        Object result = getLocator().evaluate(
                "(el, colIdx) => {"
                        + "  const cols = el._getColumns().filter(c => !c.hidden);"
                        + "  const col = cols[colIdx];"
                        + "  const headerCell = col._headerCell;"
                        + "  const content = headerCell._content;"
                        + "  const sorter = content.querySelector('vaadin-grid-sorter');"
                        + "  if (!sorter) return null;"
                        + "  const dir = sorter.getAttribute('direction');"
                        + "  return dir || null;"
                        + "}", colIndex);
        return result == null ? null : result.toString();
    }

    /**
     * Get the current sort direction for the column matching the given header text.
     *
     * @param headerText the header text to match
     * @return {@code "asc"}, {@code "desc"}, or {@code null} if unsorted
     */
    default String getSortDirection(String headerText) {
        return getSortDirection(resolveColumnIndex(headerText));
    }

    /**
     * Assert the sort direction for the column at the given index.
     *
     * @param colIndex  0-based visible column index
     * @param direction expected direction ({@code "asc"}, {@code "desc"}, or {@code null})
     */
    default void assertSortDirection(int colIndex, String direction) {
        getLocator().page().waitForCondition(() -> {
            String actual = getSortDirection(colIndex);
            return direction == null ? actual == null : direction.equals(actual);
        });
    }

    /**
     * Assert the sort direction for the column matching the given header text.
     *
     * @param headerText the header text to match
     * @param direction  expected direction ({@code "asc"}, {@code "desc"}, or {@code null})
     */
    default void assertSortDirection(String headerText, String direction) {
        assertSortDirection(resolveColumnIndex(headerText), direction);
    }

    /**
     * Assert that the column at the given index is not sorted.
     *
     * @param colIndex 0-based visible column index
     */
    default void assertNotSorted(int colIndex) {
        assertSortDirection(colIndex, null);
    }
}
