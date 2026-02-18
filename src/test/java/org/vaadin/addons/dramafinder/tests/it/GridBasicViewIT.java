package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        grid.assertRowCount(100);
    }

    @Test
    public void testGetById() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertVisible();
        grid.assertRowCount(100);
    }

    @Test
    public void testGetByLocator() {
        GridElement grid = GridElement.get(page.locator("#basic-grid").locator(".."));
        grid.assertVisible();
        grid.assertRowCount(100);
    }

    // ── Row Count ──────────────────────────────────────────────────────

    @Test
    public void testRowCount() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals(100, grid.getRowCount());
        grid.assertRowCount(100);
    }

    @Test
    public void testEmptyGrid() {
        GridElement grid = GridElement.getById(page, "empty-grid");
        assertEquals(0, grid.getRowCount());
        grid.assertEmpty();
    }

    // ── Column Count ───────────────────────────────────────────────────

    @Test
    public void testColumnCount() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals(3, grid.getColumnCount());
        grid.assertColumnCount(3);
    }

    // ── Headers ────────────────────────────────────────────────────────

    @Test
    public void testHeaderCellContent() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals("First Name", grid.getHeaderCellContent(0));
        assertEquals("Last Name", grid.getHeaderCellContent(1));
        assertEquals("Email", grid.getHeaderCellContent(2));
    }

    @Test
    public void testHeaderCellContents() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        List<String> headers = grid.getHeaderCellContents();
        assertEquals(List.of("First Name", "Last Name", "Email"), headers);
    }

    @Test
    public void testAssertHeaderCellContent() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertHeaderCellContent(0, "First Name");
        grid.assertHeaderCellContent(1, "Last Name");
        grid.assertHeaderCellContent(2, "Email");
    }

    // ── Cell Content ───────────────────────────────────────────────────

    @Test
    public void testCellContentByIndex() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals("First1", grid.getCellContent(0, 0));
        assertEquals("Last1", grid.getCellContent(0, 1));
        assertEquals("person1@example.com", grid.getCellContent(0, 2));
    }

    @Test
    public void testCellContentByHeaderText() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertEquals("First1", grid.getCellContent(0, "First Name"));
        assertEquals("Last1", grid.getCellContent(0, "Last Name"));
        assertEquals("person1@example.com", grid.getCellContent(0, "Email"));
    }

    @Test
    public void testAssertCellContent() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertCellContent(0, 0, "First1");
        grid.assertCellContent(0, "First Name", "First1");
    }

    // ── Scrolling ──────────────────────────────────────────────────────

    @Test
    public void testScrollToRow() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertRowInView(0);
        grid.assertRowNotInView(90);

        grid.scrollToRow(90);

        grid.assertRowInView(90);
        grid.assertRowNotInView(0);
    }

    @Test
    public void testScrollToStart() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.scrollToRow(90);
        grid.assertRowNotInView(0);

        grid.scrollToStart();

        grid.assertFirstVisibleRow(0);
        grid.assertRowInView(0);
    }

    @Test
    public void testScrollToEnd() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.assertRowNotInView(99);

        grid.scrollToEnd();

        grid.assertRowInView(99);
        grid.assertRowNotInView(0);
    }

    @Test
    public void testVisibleRowCount() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        int visibleCount = grid.getVisibleRowCount();
        assertTrue(visibleCount > 0, "Should have at least one visible row");
        assertTrue(visibleCount < 100, "Should not show all 100 rows");
    }

    @Test
    public void testRowInView() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertTrue(grid.isRowInView(0));
        assertFalse(grid.isRowInView(99));
    }

    @Test
    public void testScrollAndReadCell() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        grid.scrollToRow(50);
        grid.assertRowInView(50);
        grid.assertCellContent(50, 0, "First51");
    }

    // ── All Rows Visible ───────────────────────────────────────────────

    @Test
    public void testAllRowsVisible() {
        GridElement grid = GridElement.getById(page, "all-rows-visible-grid");
        assertTrue(grid.isAllRowsVisible());
        grid.assertAllRowsVisible();
        grid.assertRowCount(5);
    }

    @Test
    public void testNotAllRowsVisible() {
        GridElement grid = GridElement.getById(page, "basic-grid");
        assertFalse(grid.isAllRowsVisible());
        grid.assertNotAllRowsVisible();
    }

    // ── Lazy Grid ──────────────────────────────────────────────────────

    @Test
    public void testLazyGridRowCount() {
        GridElement grid = GridElement.getById(page, "lazy-grid");
        grid.assertRowCount(10000);
    }

    @Test
    public void testLazyGridScrollToDistantRow() {
        GridElement grid = GridElement.getById(page, "lazy-grid");
        grid.scrollToRow(9000);
        grid.assertRowInView(9000);
        grid.assertCellContent(9000, 0, "First9001");
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
        assertEquals(0, grid.resolveColumnIndex("First Name"));
        assertEquals(1, grid.resolveColumnIndex("Last Name"));
        assertEquals(2, grid.resolveColumnIndex("Email"));
    }
}
