package org.vaadin.addons.dramafinder.tests.it;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Grids whose cells hold links: selecting a row must not click the link, and
 * the grid's post-click wait must not wait for a grid that a link removed.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridNavigationViewIT extends SpringPlaywrightIT {

    // Well below Playwright's 30 s default timeout, which a wait for the
    // detached grid would run into
    private static final Duration MAX_DURATION = Duration.ofSeconds(10);

    @Override
    public String getView() {
        return "grid-navigation";
    }

    @Test
    public void testSelectOnRouterLinkCellSelectsRow() {
        assertSelectsWithoutNavigating("router-link-grid");
    }

    @Test
    public void testSelectOnReloadLinkCellSelectsRow() {
        assertSelectsWithoutNavigating("reload-link-grid");
    }

    @Test
    public void testSelectWhenEveryCellIsALinkUsesPadding() {
        // Any click outside the cell padding would hit a link and navigate
        assertSelectsWithoutNavigating("all-links-grid");
    }

    @Test
    public void testSelectSkipsCoveredFirstCell() {
        // The first cell has no free spot, so only the text cell next to it can select the row
        assertSelectsWithoutNavigating("first-covered-grid");
    }

    @Test
    public void testDeselectOnLinkCell() {
        GridElement grid = GridElement.getById(page, "router-link-grid");
        grid.select(1);
        grid.deselect(1);
        assertFalse(grid.findRow(1).orElseThrow().isSelected());
        assertEquals(0, grid.getSelectedItemCount());
        assertThat(page.locator("#navigation-target")).hasCount(0);
    }

    @Test
    public void testSelectOnCoveredCellsFails() {
        GridElement grid = GridElement.getById(page, "covered-link-grid");
        assertThrows(IllegalStateException.class, () -> grid.select(0));
        assertEquals(0, grid.getSelectedItemCount());
        assertThat(page.locator("#navigation-target")).hasCount(0);
    }

    @Test
    public void testWaitForGridToStopLoadingWhenGridIsGone() {
        GridElement grid = GridElement.getById(page, "router-link-grid");
        grid.findCell(0, 0).orElseThrow().click();
        assertThat(page.locator("#navigation-target")).isVisible();
        assertThat(grid.getLocator()).hasCount(0);
        assertTimeout(MAX_DURATION, grid::waitForGridToStopLoading);
    }

    private void assertSelectsWithoutNavigating(String gridId) {
        GridElement grid = GridElement.getById(page, gridId);
        grid.select(0);
        assertTrue(grid.findRow(0).orElseThrow().isSelected());
        assertEquals(1, grid.getSelectedItemCount());
        assertThat(page.locator("#navigation-target")).hasCount(0);
    }
}
