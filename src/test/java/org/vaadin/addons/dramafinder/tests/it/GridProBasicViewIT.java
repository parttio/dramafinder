package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridProElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link GridProElement}.
 * <p>
 * Test data for {@code basic-grid-pro} (10 rows):
 * <pre>
 *   row 0: First1  | Last1  | active=false | department=Marketing
 *   row 1: First2  | Last2  | active=true  | department=Sales
 *   row 2: First3  | Last3  | active=false | department=Engineering
 *   row 3: First4  | Last4  | active=true  | department=Marketing
 * </pre>
 * Column indices: 0=First Name (text editor), 1=Last Name (read-only),
 *                 2=Active (checkbox editor), 3=Department (select editor).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridProBasicViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-pro";
    }

    // ── Factory Methods ────────────────────────────────────────────────

    @Test
    public void testGetGridProByPage() {
        var grid = GridProElement.get(page);
        grid.assertVisible();
    }

    @Test
    public void testGetGridProById() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        grid.assertVisible();
    }

    @Test
    public void testGetGridProByLocator() {
        var grid = GridProElement.get(page.locator("main"));
        grid.assertVisible();
    }

    // ── Inherited Grid Behaviour ───────────────────────────────────────

    @Test
    public void testGridProHasTotalRowCount() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        assertEquals(10, grid.getTotalRowCount());
    }

    @Test
    public void testGridProHasExpectedColumnCount() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        assertEquals(4, grid.getColumnCount());
    }

    @Test
    public void testReadOnlyColumnValueIsDisplayed() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findCell(0, 1);
        assertTrue(cell.isPresent());
        assertThat(cell.get().getCellContentLocator()).hasText("Last1");
    }

    // ── Grid Pro Properties ────────────────────────────────────────────

    @Test
    public void testIsEditOnClickReturnsFalseForDefaultGrid() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        assertFalse(grid.isEditOnClick());
    }

    @Test
    public void testIsEditOnClickReturnsTrueWhenSetOnGrid() {
        var grid = GridProElement.getById(page, "edit-on-click-grid-pro");
        assertTrue(grid.isEditOnClick());
    }

    @Test
    public void testIsSingleCellEditReturnsFalseForDefaultGrid() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        assertFalse(grid.isSingleCellEdit());
    }

    @Test
    public void testIsSingleCellEditReturnsTrueWhenSetOnGrid() {
        var grid = GridProElement.getById(page, "single-cell-edit-grid-pro");
        assertTrue(grid.isSingleCellEdit());
    }

    @Test
    public void testIsEnterNextRowReturnsFalseForDefaultGrid() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        assertFalse(grid.isEnterNextRow());
    }

    @Test
    public void testIsEnterNextRowReturnsTrueWhenSetOnGrid() {
        var grid = GridProElement.getById(page, "enter-next-row-grid-pro");
        assertTrue(grid.isEnterNextRow());
    }

    // ── Edit Mode State ────────────────────────────────────────────────

    @Test
    public void testCellIsNotInEditModeInitially() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0);
        assertTrue(cell.isPresent());
        assertFalse(cell.get().isEditing());
    }

    @Test
    public void testCellIsInEditModeAfterDoubleClick() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        assertTrue(cell.isEditing());
    }

    @Test
    public void testCellIsNotInEditModeAfterPressingEscape() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        assertTrue(cell.isEditing());
        cell.cancelEditing();
        assertFalse(cell.isEditing());
    }

    @Test
    public void testCellIsNotInEditModeAfterPressingEnter() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        assertTrue(cell.isEditing());
        cell.saveEditing();
        assertFalse(cell.isEditing());
    }

    // ── TextField Editor ───────────────────────────────────────────────

    @Test
    public void testTextFieldEditorIsVisibleAfterEnteringEditMode() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        cell = grid.findEditableCell(0, 0).get();
        assertThat(cell.getTextFieldEditor().getLocator()).isVisible();
    }

    @Test
    public void testTextFieldEditorValueMatchesCellContentBeforeEditing() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        assertEquals("First1", cell.getTextFieldEditor().getValue());
    }

    @Test
    public void testSetTextFieldEditorValueAndSaveWithEnterUpdatesCell() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        cell.getTextFieldEditor().setValue("UpdatedFirst");
        cell.saveEditing();
        assertThat(cell.getCellContentLocator()).hasText("UpdatedFirst");
    }

    @Test
    public void testSetTextFieldEditorValueAndCancelWithEscapeKeepsCellUnchanged() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        cell.getTextFieldEditor().setValue("ShouldNotBeSaved");
        cell.cancelEditing();
        assertThat(cell.getCellContentLocator()).hasText("First1");
    }

    @Test
    public void testEditWithTextFieldValueConvenienceMethodUpdatesCell() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(1, 0).get();
        cell.editWithTextFieldValue("ChangedFirst");
        assertThat(cell.getCellContentLocator()).hasText("ChangedFirst");
    }

    // ── Checkbox Editor ────────────────────────────────────────────────

    @Test
    public void testCheckboxEditorIsVisibleAfterEnteringEditMode() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Column 2 is the Active (checkbox) column; row 0 has active=false
        var cell = grid.findEditableCell(0, 2).get();
        cell.startEditing();
        assertThat(cell.getCheckboxEditor().getLocator()).isVisible();
        cell.cancelEditing();
    }

    @Test
    public void testCheckboxEditorReflectsCurrentItemState() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 0: active=false, Row 1: active=true
        var uncheckedCell = grid.findEditableCell(0, 2).get();
        uncheckedCell.startEditing();
        assertFalse(uncheckedCell.getCheckboxEditor().isChecked());
        uncheckedCell.cancelEditing();

        var checkedCell = grid.findEditableCell(1, 2).get();
        checkedCell.startEditing();
        assertTrue(checkedCell.getCheckboxEditor().isChecked());
        checkedCell.cancelEditing();
    }

    @Test
    public void testToggleCheckboxEditorChangesValueFromUncheckedToChecked() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 0: active=false initially
        var cell = grid.findEditableCell(0, 2).get();
        cell.startEditing();
        cell.toggleCheckboxEditor();
        cell.tabToNextCell();
        cell.startEditing();
        assertTrue(cell.getCheckboxEditor().isChecked());
        cell.cancelEditing();
    }

    @Test
    public void testToggleCheckboxEditorChangesValueFromCheckedToUnchecked() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 1: active=true initially
        var cell = grid.findEditableCell(1, 2).get();
        cell.startEditing();
        cell.toggleCheckboxEditor();
        cell.tabToNextCell();
        cell.startEditing();
        assertFalse(cell.getCheckboxEditor().isChecked());
        cell.cancelEditing();
    }

    // ── Select Editor ──────────────────────────────────────────────────

    @Test
    public void testSelectEditorIsVisibleAfterEnteringEditMode() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Column 3 is the Department (select) column
        var cell = grid.findEditableCell(0, 3).get();
        cell.startEditing();
        assertThat(cell.getSelectEditor().getLocator()).isVisible();
    }

    @Test
    public void testSelectEditorCurrentValueMatchesCellContent() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 0: department=Marketing
        var cell = grid.findEditableCell(0, 3).get();
        assertThat(cell.getCellContentLocator()).hasText("Marketing");
        cell.startEditing();
        assertEquals("Marketing", cell.getSelectEditor().getValue());
    }

    @Test
    public void testSelectEditorItemAndSaveUpdatesCell() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 2: department=Engineering; change to Sales
        var cell = grid.findEditableCell(2, 3).get();
        assertThat(cell.getCellContentLocator()).hasText("Engineering");
        cell.startEditing();
        cell.getSelectEditor().selectItem("Sales");
        cell.tabToNextCell(); // Save changes by tabbing away
        grid.waitForGridToStopLoading();
        assertThat(cell.getCellContentLocator()).hasText("Sales");
    }

    @Test
    public void testSelectEditorItemAndCancelWithEscapeKeepsCellUnchanged() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 0: department=Marketing; start editing, press Escape without selecting.
        // Grid Pro auto-opens the select dropdown when entering edit mode, so two
        // Escapes are needed: the first closes the dropdown, the second exits edit mode.
        var cell = grid.findEditableCell(0, 3).get();
        cell.startEditing();
        cell.cancelEditing(); // Close dropdown
        cell.cancelEditing(); // Exit edit mode
        assertThat(cell.getCellContentLocator()).hasText("Marketing");
    }

    @Test
    public void testEditWithSelectItemConvenienceMethodUpdatesCell() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Row 3: department=Marketing; change to Engineering
        var cell = grid.findEditableCell(3, 3).get();
        cell.editWithSelectItem("Engineering");
        cell.tabToNextCell(); // Save changes by tabbing away
        assertThat(cell.getCellContentLocator()).hasText("Engineering");
    }

    // ── Keyboard Navigation ────────────────────────────────────────────

    @Test
    public void testEnterKeySavesChangesAndExitsEditMode() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        cell.getTextFieldEditor().setValue("SavedByEnter");
        cell.saveEditing();
        assertFalse(cell.isEditing());
        assertThat(cell.getCellContentLocator()).hasText("SavedByEnter");
    }

    @Test
    public void testEscapeKeyDiscardsChangesAndExitsEditMode() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        cell.getTextFieldEditor().setValue("DiscardedByEscape");
        cell.cancelEditing();
        assertFalse(cell.isEditing());
        assertThat(cell.getCellContentLocator()).hasText("First1");
    }

    @Test
    public void testTabKeyExitsCurrentCellEditMode() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        cell.getTextFieldEditor().setValue("TabbedAway");
        cell.tabToNextCell();
        assertFalse(cell.isEditing());
        assertThat(cell.getCellContentLocator()).hasText("TabbedAway");
    }

    @Test
    public void testShiftTabKeyExitsCurrentCellEditModeAndSavesValue() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        // Start at row 1 so Shift+Tab has a previous cell to move to
        var cell = grid.findEditableCell(1, 0).get();
        cell.startEditing();
        cell.getTextFieldEditor().setValue("ShiftTabbedAway");
        cell.tabToPreviousCell();
        assertFalse(cell.isEditing());
        assertThat(cell.getCellContentLocator()).hasText("ShiftTabbedAway");
    }

    // ── Edit on Click ──────────────────────────────────────────────────

    @Test
    public void testSingleClickEntersEditModeWhenEditOnClickIsEnabled() {
        var grid = GridProElement.getById(page, "edit-on-click-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditingWithSingleClick();
        assertTrue(cell.isEditing());
    }

    @Test
    public void testDoubleClickIsRequiredToEnterEditModeWhenEditOnClickIsDisabled() {
        var grid = GridProElement.getById(page, "basic-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        // Single click should not enter edit mode on a default grid
        cell.getCellContentLocator().dispatchEvent("click");
        grid.waitForGridToStopLoading();
        assertFalse(cell.isEditing());
        // Double click should enter edit mode
        cell.startEditing();
        assertTrue(cell.isEditing());
    }

    // ── Single Cell Edit ───────────────────────────────────────────────

    @Test
    public void testTabExitsEditModeWhenSingleCellEditIsEnabled() {
        var grid = GridProElement.getById(page, "single-cell-edit-grid-pro");
        var cell = grid.findEditableCell(0, 0).get();
        cell.startEditing();
        assertTrue(cell.isEditing());
        cell.tabToNextCell();
        // With singleCellEdit=true, tabbing exits edit mode entirely
        assertFalse(cell.isEditing());
    }

    // ── Enter Next Row ─────────────────────────────────────────────────

    @Test
    public void testEnterKeyMovesToNextRowFirstEditableCellWhenEnterNextRowIsEnabled() {
        var grid = GridProElement.getById(page, "enter-next-row-grid-pro");
        var row0Cell = grid.findEditableCell(0, 0).get();
        row0Cell.startEditing();
        row0Cell.getTextFieldEditor().setValue("Row0Value");
        // Press Enter — with enterNextRow=true this moves to row 1 col 0
        row0Cell.saveEditing();
        assertFalse(row0Cell.isEditing());
        assertThat(row0Cell.getCellContentLocator()).hasText("Row0Value");
        // Refetch row 1 cell after Enter moved focus there; the old reference is stale.
        // Wait for Grid Pro to render the edited-cell part on the new cell.
        var row1Cell = grid.findEditableCell(1, 0).get();
        page.waitForCondition(row1Cell::isEditing);
        assertTrue(row1Cell.isEditing());
    }

    @Test
    public void testShiftEnterKeyMovesToPreviousRowFirstEditableCellWhenEnterNextRowIsEnabled() {
        var grid = GridProElement.getById(page, "enter-next-row-grid-pro");
        var row1Cell = grid.findEditableCell(1, 0).get();
        // Start editing row 1, then Shift+Enter to go back to row 0
        row1Cell.startEditing();
        row1Cell.getTextFieldEditor().setValue("Row1Value");
        page.keyboard().press("Shift+Enter");
        grid.waitForGridToStopLoading();
        assertFalse(row1Cell.isEditing());
        assertThat(row1Cell.getCellContentLocator()).hasText("Row1Value");
        // Refetch row 0 cell after Shift+Enter moved focus there; the old reference is stale.
        // Wait for Grid Pro to render the edited-cell part on the new cell.
        var row0Cell = grid.findEditableCell(0, 0).get();
        page.waitForCondition(row0Cell::isEditing);
        assertTrue(row0Cell.isEditing());
    }
}
