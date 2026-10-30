package org.vaadin.addons.dramafinder.tests.it;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * Clicking a cell whose content navigates away removes the grid from the DOM;
 * the grid's post-click wait must not wait for it to come back.
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
    public void testSelectOnRouterLinkCellNavigates() {
        GridElement grid = GridElement.getById(page, "router-link-grid");
        assertTimeout(MAX_DURATION, () -> grid.select(0));
        assertThat(page.locator("#navigation-target")).isVisible();
    }

    @Test
    public void testSelectOnReloadLinkCellNavigates() {
        GridElement grid = GridElement.getById(page, "reload-link-grid");
        assertTimeout(MAX_DURATION, () -> grid.select(0));
        assertThat(page.locator("#navigation-target")).isVisible();
    }

    @Test
    public void testWaitForGridToStopLoadingWhenGridIsGone() {
        GridElement grid = GridElement.getById(page, "router-link-grid");
        grid.findCell(0, 0).orElseThrow().click();
        assertThat(page.locator("#navigation-target")).isVisible();
        assertThat(grid.getLocator()).hasCount(0);
        assertTimeout(MAX_DURATION, grid::waitForGridToStopLoading);
    }
}
