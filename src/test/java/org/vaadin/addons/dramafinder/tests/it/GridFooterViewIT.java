package org.vaadin.addons.dramafinder.tests.it;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridFooterViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-footer";
    }

    @Test
    public void testFindFooterCellByIndex() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        var footerCell = grid.findFooterCell(0);
        assertFalse(footerCell.isEmpty());
        assertThat(footerCell.get().getCellContentLocator()).hasText("Total: 50");
    }

    @Test
    public void testFindFooterCellByRowAndIndex() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        var footerCell = grid.findFooterCell(0, 1);
        assertFalse(footerCell.isEmpty());
        assertThat(footerCell.get().getCellContentLocator()).hasText("Last names");
    }

    @Test
    public void testFindFooterCellByText() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        var footerCell = grid.findFooterCellByText("Emails");
        assertFalse(footerCell.isEmpty());
        assertThat(footerCell.get().getCellContentLocator()).hasText("Emails");
    }

    @Test
    public void testFindMissingFooterCellByText() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        assertTrue(grid.findFooterCellByText("Does not exist").isEmpty());
    }

    @Test
    public void testGetFooterCellContents() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        grid.assertFooterCellContents("Total: 50", "Last names", "Emails");
    }

    // ── Assertions ─────────────────────────────────────────────────────

    @Test
    public void testAssertFooterCell() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        grid.assertFooterCell(1, "Last names");
    }

    @Test
    public void testAssertFooterPresent() {
        GridElement grid = GridElement.getById(page, "footer-grid");

        grid.assertFooterPresent("Emails");
        grid.assertFooterNotPresent("Does not exist");
    }
}
