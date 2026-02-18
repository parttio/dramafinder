package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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
     * Get a locator for the {@code <vaadin-grid-sorter>} at the given column index.
     *
     * @param colIndex 0-based visible column index
     * @return locator for the sorter element
     */
    default Locator getSorterLocator(int colIndex) {
        return getLocator().locator("vaadin-grid-sorter").nth(colIndex);
    }

    /**
     * Click the sorter element in the header cell at the given column index.
     *
     * @param colIndex 0-based visible column index
     */
    default void clickHeaderToSort(int colIndex) {
        getSorterLocator(colIndex).click();
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
        return getSorterLocator(colIndex).getAttribute("direction");
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
        Locator sorter = getSorterLocator(colIndex);
        if (direction == null) {
            sorter.page().waitForCondition(
                    () -> sorter.getAttribute("direction") == null);
        } else {
            assertThat(sorter).hasAttribute("direction", direction);
        }
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
