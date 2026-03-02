package org.vaadin.addons.dramafinder.element;

import java.util.Optional;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * PlaywrightElement for {@code <vaadin-grid-pro>}.
 * <p>
 * Extends {@link GridElement} with inline editing APIs: entering and exiting edit mode,
 * keyboard navigation between editable cells, and typed access to the active inline editor
 * (text field, checkbox, or select).
 * <p>
 * All non-editing Grid behaviour (rows, headers, sorting, selection, scrolling) is
 * inherited unchanged from {@link GridElement}.
 */
@PlaywrightElement(GridProElement.FIELD_TAG_NAME)
public class GridProElement extends GridElement {

    public static final String FIELD_TAG_NAME = "vaadin-grid-pro";

    /**
     * Tag name of the inline text-field editor rendered by Grid Pro.
     * Grid Pro uses its own subclass of {@code vaadin-text-field} rather than
     * the standard component, so this tag differs from {@link TextFieldElement#FIELD_TAG_NAME}.
     */
    public static final String EDITOR_TEXT_FIELD_TAG_NAME = "vaadin-grid-pro-edit-text-field";

    /**
     * Tag name of the inline checkbox editor rendered by Grid Pro.
     * Grid Pro uses its own subclass of {@code vaadin-checkbox} rather than
     * the standard component, so this tag differs from {@link CheckboxElement#FIELD_TAG_NAME}.
     */
    public static final String EDITOR_CHECKBOX_TAG_NAME = "vaadin-grid-pro-edit-checkbox";

    /**
     * Tag name of the inline select editor rendered by Grid Pro.
     * Grid Pro uses its own subclass of {@code vaadin-select} rather than
     * the standard component, so this tag differs from {@link SelectElement#FIELD_TAG_NAME}.
     */
    public static final String EDITOR_SELECT_TAG_NAME = "vaadin-grid-pro-edit-select";

    /**
     * Create a new {@code GridProElement}.
     *
     * @param locator the locator for the {@code <vaadin-grid-pro>} element
     */
    public GridProElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code GridProElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code GridProElement}
     */
    public static GridProElement get(Page page) {
        return new GridProElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code GridProElement} within a parent locator.
     *
     * @param parent the parent locator to search within
     * @return the first matching {@code GridProElement}
     */
    public static GridProElement get(Locator parent) {
        return new GridProElement(parent.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get a {@code GridProElement} by its {@code id} attribute.
     *
     * @param page the Playwright page
     * @param id   the element id
     * @return the matching {@code GridProElement}
     */
    public static GridProElement getById(Page page, String id) {
        return new GridProElement(page.locator("#" + id));
    }

    // ── Grid Pro Property Getters ──────────────────────────────────────

    /**
     * Whether the grid enters edit mode on a single click (instead of double-click).
     *
     * @return {@code true} if edit-on-click is enabled
     */
    public boolean isEditOnClick() {
        return Boolean.TRUE.equals(getProperty("editOnClick"));
    }

    /**
     * Whether the grid exits edit mode when tabbing between cells.
     * When {@code false} (default), Tab saves the current cell and moves focus to
     * the next editable cell while staying in edit mode.
     *
     * @return {@code true} if single-cell edit is enabled
     */
    public boolean isSingleCellEdit() {
        return Boolean.TRUE.equals(getProperty("singleCellEdit"));
    }

    /**
     * Whether pressing Enter moves focus to the next row's editable cell
     * (and Shift+Enter moves to the previous row).
     *
     * @return {@code true} if enter-next-row is enabled
     */
    public boolean isEnterNextRow() {
        return Boolean.TRUE.equals(getProperty("enterNextRow"));
    }

    // ── Editable Cell Access ───────────────────────────────────────────

    /**
     * Find the editable cell at the given row and column, returning an
     * {@link EditableCellElement} that exposes editing state and actions.
     *
     * @param rowIndex    0-based row index
     * @param columnIndex 0-based column index
     * @return optional editable cell element; empty if the cell does not exist
     */
    public Optional<EditableCellElement> findEditableCell(int rowIndex, int columnIndex) {
        return findCell(rowIndex, columnIndex)
                .map(cell -> new EditableCellElement(cell.getTableCellLocator(), columnIndex));
    }

    /** 
     * Check whether a given cell is currently in edit mode by checking for the presence of any of the three Grid Pro inline editors.
     * Override to provide custom logic for detecting edit mode if needed (e.g. for custom editor components).
     * @param cell the cell to check
     * @return {@code true} if the cell is currently being edited
     */
    protected boolean isEditing(EditableCellElement cell) {
        return cell.getCellContentLocator().locator(
                    EDITOR_TEXT_FIELD_TAG_NAME + ", " +
                    EDITOR_CHECKBOX_TAG_NAME + ", " +
                    EDITOR_SELECT_TAG_NAME
            ).count() > 0;
    }

    // ── Inner Class: EditableCellElement ───────────────────────────────

    /**
     * Represents a cell in a Grid Pro, extending {@link GridElement.CellElement}
     * with edit-mode state queries, editing lifecycle actions, and typed access
     * to the inline editor component.
     */
    public class EditableCellElement extends GridElement.CellElement {

        /**
         * Create a new {@code EditableCellElement}.
         *
         * @param tableCell   the locator for the {@code <td>} element
         * @param columnIndex 0-based column index
         */
        public EditableCellElement(Locator tableCell, int columnIndex) {
            super(tableCell, columnIndex);
        }

        /**
         * Create a new {@code EditableCellElement} from a parent {@link GridElement.CellElement}.
         * The column index is taken from the parent cell, and the table cell locator is used as-is.
         * @param cell the parent cell element to wrap as an editable cell
         */
        public EditableCellElement(GridElement.CellElement cell) {
            super(cell.getTableCellLocator(), cell.getColumnIndex());
        }

        // ── Edit Mode State ────────────────────────────────────────────

        /**
         * Whether this cell is currently in edit mode.
         *
         * @return {@code true} if this cell is currently being edited
         */
        public boolean isEditing() {
            return GridProElement.this.isEditing(this);
        }

        // ── Enter Edit Mode ────────────────────────────────────────────

        /**
         * Double-click the cell content to enter edit mode.
         * Use this for grids where {@code editOnClick} is {@code false} (the default).
         */
        public void startEditing() {
            getCellContentLocator().dispatchEvent("dblclick");
            GridProElement.this.waitForGridToStopLoading();
            getLocator().page().waitForCondition(this::isEditing);
        }

        /**
         * Single-click the cell content to enter edit mode.
         * Use this for grids where {@code editOnClick} is {@code true}.
         */
        public void startEditingWithSingleClick() {
            getCellContentLocator().dispatchEvent("click");
            GridProElement.this.waitForGridToStopLoading();
            getLocator().page().waitForCondition(this::isEditing);
        }

        // ── Exit Edit Mode ─────────────────────────────────────────────

        /**
         * Press Enter to save the current value and exit edit mode.
         */
        public void saveEditing() {
            getLocator().page().keyboard().press("Enter");
            GridProElement.this.waitForGridToStopLoading();
        }

        /**
         * Press Escape to discard the current value and exit edit mode.
         */
        public void cancelEditing() {
            getLocator().page().keyboard().press("Escape");
            GridProElement.this.waitForGridToStopLoading();
        }

        // ── Tab Navigation ─────────────────────────────────────────────

        /**
         * Press Tab to save the current value and move focus to the next editable cell.
         * When {@code singleCellEdit} is {@code false} (default), the grid remains in
         * edit mode on the next cell. When {@code singleCellEdit} is {@code true},
         * edit mode is exited after tabbing.
         */
        public void tabToNextCell() {
            getLocator().page().keyboard().press("Tab");
            GridProElement.this.waitForGridToStopLoading();
        }

        /**
         * Press Shift+Tab to save the current value and move focus to the previous
         * editable cell.
         */
        public void tabToPreviousCell() {
            getLocator().page().keyboard().press("Shift+Tab");
            GridProElement.this.waitForGridToStopLoading();
        }

        // ── Editor Access ──────────────────────────────────────────────

        /**
         * Get the {@code vaadin-grid-pro-edit-text-field} inline editor rendered inside this cell.
         * Grid Pro renders its own subclass of {@code vaadin-text-field}; use
         * {@link GridProElement#EDITOR_TEXT_FIELD_TAG_NAME} for the exact tag name.
         * The cell must be in edit mode before calling this method.
         *
         * @return a {@link TextFieldElement} wrapping the active text-field editor
         */
        public TextFieldElement getTextFieldEditor() {
            return new TextFieldElement(
                    getCellContentLocator().locator(EDITOR_TEXT_FIELD_TAG_NAME).first());
        }

        /**
         * Get the {@code vaadin-grid-pro-edit-checkbox} editor rendered inside this cell.
         * Grid Pro renders its own subclass of {@code vaadin-checkbox}; use
         * {@link GridProElement#EDITOR_CHECKBOX_TAG_NAME} for the exact tag name.
         * The cell must be in edit mode before calling this method.
         *
         * @return a {@link CheckboxElement} wrapping the checkbox editor
         */
        public CheckboxElement getCheckboxEditor() {
            return new CheckboxElement(
                    getCellContentLocator().locator(EDITOR_CHECKBOX_TAG_NAME).first());
        }

        /**
         * Get the {@code vaadin-grid-pro-edit-select} inline editor rendered inside this cell.
         * Grid Pro renders its own subclass of {@code vaadin-select}; use
         * {@link GridProElement#EDITOR_SELECT_TAG_NAME} for the exact tag name.
         * The cell must be in edit mode before calling this method.
         *
         * @return a {@link SelectElement} wrapping the active select editor
         */
        public SelectElement getSelectEditor() {
            return new SelectElement(
                    getCellContentLocator().locator(EDITOR_SELECT_TAG_NAME).first());
        }

        // ── Convenience Edit Methods ───────────────────────────────────

        /**
         * Convenience method that enters edit mode, sets the text-field editor value,
         * and saves with Enter.
         *
         * @param value the value to enter in the text-field editor
         */
        public void editWithTextFieldValue(String value) {
            startEditing();
            getTextFieldEditor().setValue(value);
            saveEditing();
        }

        /**
         * Convenience method that enters edit mode and selects an item in the select editor.
         * Grid Pro automatically commits and exits edit mode when an item is chosen from
         * a select editor, so no explicit save is needed.
         *
         * @param item the visible label of the item to select
         */
        public void editWithSelectItem(String item) {
            startEditing();
            getSelectEditor().selectItem(item);
            GridProElement.this.waitForGridToStopLoading();
        }

        /**
         * Click the checkbox editor to toggle its checked state.
         * Checkbox editors in Grid Pro are always rendered in the cell and do not
         * require entering edit mode first.
         */
        public void toggleCheckboxEditor() {
            getCheckboxEditor().getInputLocator().click();
            GridProElement.this.waitForGridToStopLoading();
        }
    }
}
