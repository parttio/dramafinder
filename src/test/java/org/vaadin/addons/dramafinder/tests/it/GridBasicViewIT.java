package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridBasicViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-basic";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Grid Basic Demo");
    }

    // ── Factory Methods ────────────────────────────────────────────────

    @Test
    public void testGetByPage() {
        GridElement grid = GridElement.get(page);
        grid.assertVisible();
        assertEquals(100, grid.getTotalRowCount());
    }

    @Test
    public void testGetById() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertVisible();
        assertEquals(100, grid.getTotalRowCount());
    }

    @Test
    public void testGetByLocator() {
        GridElement grid = GridElement.get(page.locator("#basic-grid").locator(".."));
        grid.assertVisible();
        assertEquals(100, grid.getTotalRowCount());
    }

    // ── Row Count ──────────────────────────────────────────────────────

    @Test
    public void testRowCount() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals(100, grid.getTotalRowCount());
    }

    @Test
    public void testEmptyGrid() {
        GridElement grid = GridElement.getById(page, "empty-grid");
        assertEquals(0, grid.getTotalRowCount());
    }

    // ── Column Count ───────────────────────────────────────────────────

    @Test
    public void testColumnCount() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals(3, grid.getColumnCount());
    }

    // ── Headers ────────────────────────────────────────────────────────

    @Test
    public void testHeaderCellContent() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertThat(grid.findHeaderCell(0).get().getCellContent()).hasText("First Name");
        assertThat(grid.findHeaderCell(1).get().getCellContent()).hasText("Last Name");
        assertThat(grid.findHeaderCell(2).get().getCellContent()).hasText("Email");
    }

    @Test
    public void testHeaderCellContents() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        List<String> headers = grid.getHeaderCellContents();
        assertEquals(List.of("First Name", "Last Name", "Email"), headers);
    }

    // ── Cell Content ───────────────────────────────────────────────────

    @Test
    public void testCellContentByIndex() {
        GridElement grid = GridElement.getById(page, "basic-grid");

        var firstCell = grid.findCell(0, 0);
        var secondCell = grid.findCell(0, 1);
        var thirdCell = grid.findCell(0, 2);

        assertTrue(firstCell.isPresent());
        assertTrue(secondCell.isPresent());
        assertTrue(thirdCell.isPresent());

        assertThat(firstCell.get().getCellContent()).hasText("First1");
        assertThat(secondCell.get().getCellContent()).hasText("Last1");
        assertThat(thirdCell.get().getCellContent()).hasText("person1@example.com");
    }

    @Test
    public void testCellContentByHeaderText() {
        GridElement grid = GridElement.getById(page, "basic-grid");

        var firstCell = grid.findCell(0, "First Name");
        var secondCell = grid.findCell(0, "Last Name");
        var thirdCell = grid.findCell(0, "Email");

        assertTrue(firstCell.isPresent());
        assertTrue(secondCell.isPresent());
        assertTrue(thirdCell.isPresent());

        assertThat(firstCell.get().getCellContent()).hasText("First1");
        assertThat(secondCell.get().getCellContent()).hasText("Last1");
        assertThat(thirdCell.get().getCellContent()).hasText("person1@example.com");
    }

    @Test
    public void testVisibleRowCount() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        int visibleCount = grid.getRenderedRowCount();
        assertTrue(visibleCount > 0, "Should have at least one visible row");
        assertTrue(visibleCount < 100, "Should not show all 100 rows");
    }

    // ── All Rows Visible ───────────────────────────────────────────────

    @Test
    public void testAllRowsVisible() {
        GridElement grid = GridElement.getById(page, "all-rows-visible-grid");
        assertTrue(grid.isAllRowsVisible());
        var visibleCount = grid.getRenderedRowCount();
        assertEquals(5, visibleCount, "Should have 5 visible rows");
    }

    @Test
    public void testNotAllRowsVisible() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertFalse(grid.isAllRowsVisible());
    }

    // ── Lazy Grid ──────────────────────────────────────────────────────

    @Test
    public void testLazyGridRowCount() {
        GridElement grid = GridElement.getById(page, "lazy-grid");
        assertEquals(10000, grid.getTotalRowCount(), "Should have 10000 visible rows");
    }

    @Test
    public void testLazyGridScrollToDistantRow() {
        GridElement grid = GridElement.getById(page, "lazy-grid");
        var row = grid.findRow(9000);
        assertTrue(row.isPresent());
        assertThat(row.get().getCell(0).getCellContent()).hasText("First9001");
    }

    // ── CSS Class ──────────────────────────────────────────────────────

    @Test
    public void testCssClass() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertCssClass("styled-grid");
    }

    // ── Resolve Column Index ───────────────────────────────────────────

    @Test
    public void testResolveColumnIndex() {
        GridElement grid = GridElement.getById(page, "basic-grid");

        var firstHeaderCell = grid.findHeaderCellByText("First Name");
        var secondHeaderCell = grid.findHeaderCellByText("Last Name");
        var thirdHeaderCell = grid.findHeaderCellByText("Email");

        assertTrue(firstHeaderCell.isPresent());
        assertTrue(secondHeaderCell.isPresent());
        assertTrue(thirdHeaderCell.isPresent());

        assertEquals(0, firstHeaderCell.get().getColumnIndex());
        assertEquals(1, secondHeaderCell.get().getColumnIndex());
        assertEquals(2, thirdHeaderCell.get().getColumnIndex());
    }
}
