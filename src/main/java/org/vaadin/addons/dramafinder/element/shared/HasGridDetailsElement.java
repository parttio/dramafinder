package org.vaadin.addons.dramafinder.element.shared;

/**
 * Mixin for grid components that support row details.
 */
public interface HasGridDetailsElement extends HasLocatorElement {

    /**
     * Click a row to toggle its details panel (when {@code detailsVisibleOnClick} is true).
     *
     * @param rowIndex the 0-based data row index
     */
    default void openDetails(int rowIndex) {
        getLocator().evaluate(
                "(el, idx) => {"
                        + "  const rows = el.$.items.children;"
                        + "  for (let i = 0; i < rows.length; i++) {"
                        + "    if (rows[i].index === idx) {"
                        + "      const cell = rows[i].children[0];"
                        + "      cell._content.click();"
                        + "      return;"
                        + "    }"
                        + "  }"
                        + "}", rowIndex);
    }

    /**
     * Whether the given row has its details panel open.
     *
     * @param rowIndex the 0-based data row index
     * @return {@code true} if details are open
     */
    default boolean isDetailsOpen(int rowIndex) {
        return (boolean) getLocator().evaluate(
                "(el, idx) => {"
                        + "  const rows = el.$.items.children;"
                        + "  for (let i = 0; i < rows.length; i++) {"
                        + "    if (rows[i].index === idx) {"
                        + "      return rows[i].hasAttribute('details-opened');"
                        + "    }"
                        + "  }"
                        + "  return false;"
                        + "}", rowIndex);
    }

    /**
     * Assert that the given row has its details panel open.
     *
     * @param rowIndex the 0-based data row index
     */
    default void assertDetailsOpen(int rowIndex) {
        getLocator().page().waitForCondition(() -> isDetailsOpen(rowIndex));
    }

    /**
     * Assert that the given row has its details panel closed.
     *
     * @param rowIndex the 0-based data row index
     */
    default void assertDetailsClosed(int rowIndex) {
        getLocator().page().waitForCondition(() -> !isDetailsOpen(rowIndex));
    }
}
