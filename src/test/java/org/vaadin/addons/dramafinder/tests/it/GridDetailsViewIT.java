package org.vaadin.addons.dramafinder.tests.it;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridDetailsViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-details";
    }

    @Test
    public void testDetailsInitiallyClosed() {
        GridElement grid = GridElement.getById(page, "details-grid");
        assertFalse(grid.findRow(0).get().isDetailsOpen());
    }

    @Test
    public void testOpenDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        var row = grid.findRow(0);
        assertTrue(row.isPresent());
        row.get().openDetails();

        assertTrue(row.get().isDetailsOpen());
    }

    @Test
    public void testCloseDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        var row = grid.findRow(0);
        assertTrue(row.isPresent());
        row.get().openDetails();

        assertTrue(row.get().isDetailsOpen());

        row.get().closeDetails();
        assertFalse(row.get().isDetailsOpen());
    }

    @Test
    public void testOpenDifferentRowDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        var row = grid.findRow(0);
        assertTrue(row.isPresent());
        row.get().openDetails();
        assertTrue(row.get().isDetailsOpen());

        // In default SINGLE selection, clicking row 1 moves selection and details

        var row2 = grid.findRow(1);
        assertTrue(row2.isPresent());   
        row2.get().openDetails();
        assertTrue(row2.get().isDetailsOpen());
        // Row 0 details are closed because selection moved
        assertFalse(row.get().isDetailsOpen());
    }

    // ── Assertions ─────────────────────────────────────────────────────

    @Test
    public void testAssertDetailsOpenAndClosed() {
        GridElement grid = GridElement.getById(page, "details-grid");

        grid.assertDetailsClosed(0);

        var row = grid.findRow(0).get();
        row.openDetails();
        grid.assertDetailsOpen(0);
        row.assertDetailsOpen();

        row.closeDetails();
        grid.assertDetailsClosed(0);
        row.assertDetailsClosed();
    }

    // ── Which cell is clicked ──────────────────────────────────────────

    @Test
    public void testOpenAndCloseDetailsOnSingleColumnGrid() {
        GridElement grid = GridElement.getById(page, "single-column-details-grid");
        var row = grid.findRow(0).orElseThrow();
        row.openDetails();
        row.assertDetailsOpen();
        row.closeDetails();
        row.assertDetailsClosed();
    }

    @Test
    public void testOpenDetailsDoesNotClickButtonInCell() {
        GridElement grid = GridElement.getById(page, "button-details-grid");
        var row = grid.findRow(0).orElseThrow();
        row.openDetails();
        row.assertDetailsOpen();
        assertThat(page.locator("#button-clicks")).hasText("Button clicks: 0");
    }

    @Test
    public void testOpenDetailsSkipsSelectionColumn() {
        GridElement grid = GridElement.getById(page, "multi-select-details-grid");
        var row = grid.findRow(0).orElseThrow();
        row.openDetails();
        row.assertDetailsOpen();
        assertFalse(row.isSelected());
        assertEquals(0, grid.getSelectedItemCount());
    }

    @Test
    public void testOpenDetailsFailsWhenDetailsDoNotOpenOnClick() {
        page.setDefaultTimeout(2000);
        GridElement grid = GridElement.getById(page, "no-details-on-click-grid");
        var row = grid.findRow(0).orElseThrow();
        var e = assertThrows(AssertionError.class, row::openDetails);
        assertTrue(e.getMessage().contains("setDetailsVisibleOnClick"), e.getMessage());
    }

    // ── Row click ──────────────────────────────────────────────────────

    @Test
    public void testRowClickFiresItemClickWithoutClickingButtonOrSelecting() {
        GridElement grid = GridElement.getById(page, "click-grid");
        var row = grid.findRow(1).orElseThrow();
        row.click();
        assertThat(page.locator("#item-clicked")).hasText("Clicked: " + firstName(grid, 1));
        assertThat(page.locator("#button-clicks")).hasText("Button clicks: 0");
        assertFalse(row.isSelected());
    }

    @Test
    public void testRowDoubleClickFiresItemDoubleClick() {
        GridElement grid = GridElement.getById(page, "click-grid");
        var row = grid.findRow(2).orElseThrow();
        row.doubleClick();
        assertThat(page.locator("#item-double-clicked"))
                .hasText("Double-clicked: " + firstName(grid, 2));
        assertThat(page.locator("#button-clicks")).hasText("Button clicks: 0");
    }

    private static String firstName(GridElement grid, int rowIndex) {
        return grid.findRow(rowIndex).orElseThrow().getCell("First Name").getText();
    }
}
