package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;
import java.util.OptionalInt;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridColumnsViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-columns";
    }

    // ── Column by id ───────────────────────────────────────────────────

    @Test
    public void testGetColumnIndexById() {
        GridElement grid = GridElement.getById(page, "columns-grid");
        assertEquals(0, grid.getColumnIndexById("first-name"));
        // The hidden "last-name" column is skipped
        assertEquals(1, grid.getColumnIndexById("email"));
    }

    @Test
    public void testColumnWithoutHeaderFoundById() {
        GridElement grid = GridElement.getById(page, "columns-grid");
        grid.assertCellContent(0, grid.getColumnIndexById("email"), "person1@example.com");
    }

    @Test
    public void testHiddenColumnNotFoundById() {
        GridElement grid = GridElement.getById(page, "columns-grid");
        assertEquals(OptionalInt.empty(), grid.findColumnIndexById("last-name"));
        assertThrows(IllegalArgumentException.class, () -> grid.getColumnIndexById("last-name"));
    }

    @Test
    public void testColumnKeyIsNotAnId() {
        GridElement grid = GridElement.getById(page, "columns-grid");
        assertEquals(OptionalInt.empty(), grid.findColumnIndexById("key-only"));
        assertEquals(OptionalInt.empty(), grid.findColumnIndexById("missing"));
    }

    @Test
    public void testEmptyIdRejected() {
        GridElement grid = GridElement.getById(page, "columns-grid");
        assertThrows(IllegalArgumentException.class, () -> grid.findColumnIndexById(""));
        assertThrows(IllegalArgumentException.class, () -> grid.findColumnIndexById(null));
    }

    // ── Display order ──────────────────────────────────────────────────

    @Test
    public void testColumnIndexesFollowDisplayOrderAfterReorder() {
        GridElement grid = GridElement.getById(page, "columns-grid");
        grid.assertHeaderCellContents("First Name", "", "Key Only");

        dragHeader(grid, "First Name", "Key Only");

        grid.assertHeaderCellContents("", "Key Only", "First Name");
        assertEquals(0, grid.getColumnIndexById("email"));
        assertEquals(2, grid.getColumnIndexById("first-name"));
        assertEquals(2, grid.findHeaderCellByText("First Name").orElseThrow().getColumnIndex());
        grid.assertCellContent(0, 0, "person1@example.com");
        grid.assertCellContent(0, 2, "First1");
        assertEquals(List.of(2), grid.findRowIndexesWithColumnText(2, "First3"));
        assertEquals(List.of("First1", "First2", "First3", "First4", "First5"),
                grid.getColumnTexts(grid.getColumnIndexById("first-name")));
    }

    /**
     * Drag a header cell past the right edge of another one, as a user reorders columns.
     */
    private void dragHeader(GridElement grid, String sourceHeader, String targetHeader) {
        grid.getLocator().scrollIntoViewIfNeeded();
        var source = headerContent(grid, sourceHeader).boundingBox();
        var target = headerContent(grid, targetHeader).boundingBox();
        double y = source.y + source.height / 2;
        page.mouse().move(source.x + source.width / 2, y);
        page.mouse().down();
        page.mouse().move(source.x + source.width / 2 + 10, y, new Mouse.MoveOptions().setSteps(5));
        page.mouse().move(target.x + target.width - 5, y, new Mouse.MoveOptions().setSteps(20));
        page.mouse().up();
        grid.waitForGridToStopLoading();
    }

    private Locator headerContent(GridElement grid, String text) {
        return grid.findHeaderCellByText(text).orElseThrow().getCellContentLocator();
    }
}
