package org.vaadin.addons.dramafinder.element.shared;

/**
 * Mixin for grid components that support row selection (single and multi).
 */
public interface HasGridSelectionElement extends HasLocatorElement {

    /**
     * Get the number of currently selected items.
     *
     * @return selected item count
     */
    default int getSelectedItemCount() {
        return ((Number) getLocator().evaluate(
                "el => el.selectedItems ? el.selectedItems.length : 0")).intValue();
    }

    /**
     * Select a row by clicking on it (works for single-select grids).
     *
     * @param rowIndex the 0-based data row index
     */
    default void selectRow(int rowIndex) {
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
     * Toggle the selection checkbox for a row (multi-select grids).
     *
     * @param rowIndex the 0-based data row index
     */
    default void toggleRowSelection(int rowIndex) {
        getLocator().evaluate(
                "(el, idx) => {"
                        + "  const rows = el.$.items.children;"
                        + "  for (let i = 0; i < rows.length; i++) {"
                        + "    if (rows[i].index === idx) {"
                        + "      const cells = [...rows[i].children];"
                        + "      for (const cell of cells) {"
                        + "        const checkbox = cell._content.querySelector('vaadin-checkbox');"
                        + "        if (checkbox) { checkbox.click(); return; }"
                        + "      }"
                        + "    }"
                        + "  }"
                        + "}", rowIndex);
    }

    /**
     * Toggle the select-all checkbox in the header (multi-select grids).
     */
    default void toggleSelectAll() {
        getLocator().evaluate(
                "el => {"
                        + "  const headerRows = el.$.header.children;"
                        + "  for (const row of headerRows) {"
                        + "    for (const cell of row.children) {"
                        + "      const checkbox = cell._content.querySelector('vaadin-checkbox');"
                        + "      if (checkbox) { checkbox.click(); return; }"
                        + "    }"
                        + "  }"
                        + "}");
    }

    /**
     * Deselect all items programmatically.
     */
    default void deselectAll() {
        getLocator().evaluate("el => el.selectedItems = []");
    }

    /**
     * Whether the given row is currently selected.
     *
     * @param rowIndex the 0-based data row index
     * @return {@code true} if the row is selected
     */
    default boolean isRowSelected(int rowIndex) {
        return (boolean) getLocator().evaluate(
                "(el, idx) => {"
                        + "  const rows = el.$.items.children;"
                        + "  for (let i = 0; i < rows.length; i++) {"
                        + "    if (rows[i].index === idx) {"
                        + "      return rows[i].hasAttribute('selected');"
                        + "    }"
                        + "  }"
                        + "  return false;"
                        + "}", rowIndex);
    }

    /**
     * Assert the number of selected items matches the expected count.
     *
     * @param expected expected selected item count
     */
    default void assertSelectedItemCount(int expected) {
        getLocator().page().waitForCondition(
                () -> getSelectedItemCount() == expected);
    }

    /**
     * Assert that the given row is selected.
     *
     * @param rowIndex the 0-based data row index
     */
    default void assertRowSelected(int rowIndex) {
        getLocator().page().waitForCondition(() -> isRowSelected(rowIndex));
    }

    /**
     * Assert that the given row is NOT selected.
     *
     * @param rowIndex the 0-based data row index
     */
    default void assertRowNotSelected(int rowIndex) {
        getLocator().page().waitForCondition(() -> !isRowSelected(rowIndex));
    }
}
