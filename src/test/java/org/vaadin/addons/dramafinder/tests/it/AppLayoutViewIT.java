package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.AppLayoutElement;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.SideNavigationElement;
import org.vaadin.addons.dramafinder.tests.testuis.AppLayoutView;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AppLayoutViewIT extends SpringPlaywrightIT implements HasTestView {

    /** Viewport narrow enough for the drawer to switch to overlay mode. */
    private static final int OVERLAY_WIDTH = 500;
    private static final int OVERLAY_HEIGHT = 900;

    @Override
    public String getView() {
        return "applayout";
    }

    // ── Page title ────────────────────────────────────────────────────

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("AppLayout Demo");
    }

    // ── Factory methods ───────────────────────────────────────────────

    @Test
    public void testGetByPage() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertVisible();
    }

    @Test
    public void testGetByLocator() {
        AppLayoutElement appLayout = AppLayoutElement.get(page.locator("body"));
        appLayout.assertVisible();
    }

    @Test
    public void testGetById() {
        AppLayoutElement appLayout = AppLayoutElement.getById(page, AppLayoutView.APP_LAYOUT_ID);
        appLayout.assertVisible();
        appLayout.assertHasNavbar();
        appLayout.assertHasDrawer();
    }

    // ── Areas ─────────────────────────────────────────────────────────

    @Test
    public void testNavbar() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        assertThat(appLayout.getNavbarLocator()).hasCount(2);
        assertThat(appLayout.getNavbarLocator().getByText("AppLayout Demo")).isVisible();
    }

    @Test
    public void testBottomNavbarIsEmptyOnDesktop() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        assertThat(appLayout.getBottomNavbarLocator()).hasCount(0);
    }

    @Test
    public void testDrawer() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        assertThat(appLayout.getDrawerLocator().getByText("Dramas")).isVisible();
        SideNavigationElement sideNav = new SideNavigationElement(
                appLayout.getDrawerLocator().locator(SideNavigationElement.FIELD_TAG_NAME));
        sideNav.assertVisible();
    }

    @Test
    public void testContent() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        assertThat(appLayout.getContentLocator()).hasCount(1);
        assertThat(appLayout.getContentLocator()).containsText("App layout content");
    }

    // ── Drawer state ──────────────────────────────────────────────────

    @Test
    public void testDrawerOpenedByDefaultOnDesktop() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertNotOverlayMode();
        appLayout.assertDrawerOpened();
        assertTrue(appLayout.isDrawerOpened());
    }

    @Test
    public void testToggleDrawer() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertDrawerOpened();

        appLayout.toggleDrawer();
        appLayout.assertDrawerClosed();
        assertFalse(appLayout.isDrawerOpened());

        appLayout.toggleDrawer();
        appLayout.assertDrawerOpened();
    }

    @Test
    public void testClosedDrawerIsHiddenOnlyAfterTheTransition() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.closeDrawer();

        // The drawer state is immediate, its visibility is not: the drawer only
        // becomes visibility:hidden once the closing transition ends, so reading
        // isVisible() right away still reports true. Assert on the state, and on
        // visibility only through an auto-retrying assertion.
        appLayout.assertDrawerClosed();
        assertThat(appLayout.getDrawerLocator().first()).isHidden();
    }

    @Test
    public void testSetDrawerOpened() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);

        appLayout.setDrawerOpened(false);
        appLayout.assertDrawerClosed();

        // Already closed: the state is kept
        appLayout.setDrawerOpened(false);
        appLayout.assertDrawerClosed();

        appLayout.setDrawerOpened(true);
        appLayout.assertDrawerOpened();

        // Already opened: the state is kept
        appLayout.setDrawerOpened(true);
        appLayout.assertDrawerOpened();
    }

    @Test
    public void testDrawerToggle() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        ButtonElement drawerToggle = appLayout.getDrawerToggle();
        drawerToggle.assertVisible();
        drawerToggle.assertAriaLabel("Menu toggle");
    }

    @Test
    public void testDrawerClosedFromServer() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertDrawerOpened();

        ButtonElement.getByText(page, "Close drawer").click();

        appLayout.assertDrawerClosed();
    }

    // ── Overlay mode ──────────────────────────────────────────────────

    @Test
    public void testOverlayModeOnNarrowViewport() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertNotOverlayMode();
        assertFalse(appLayout.isOverlayMode());

        page.setViewportSize(OVERLAY_WIDTH, OVERLAY_HEIGHT);

        appLayout.assertOverlayMode();
        assertTrue(appLayout.isOverlayMode());
        // Switching to overlay mode hides the drawer
        appLayout.assertDrawerClosed();
    }

    @Test
    public void testOpenAndCloseDrawerInOverlayMode() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        page.setViewportSize(OVERLAY_WIDTH, OVERLAY_HEIGHT);
        appLayout.assertOverlayMode();

        appLayout.openDrawer();
        appLayout.assertDrawerOpened();

        // The backdrop covers the drawer toggle, so closeDrawer presses Escape instead
        appLayout.closeDrawer();
        appLayout.assertDrawerClosed();
    }

    @Test
    public void testCloseDrawerWithEscapeInOverlayMode() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        page.setViewportSize(OVERLAY_WIDTH, OVERLAY_HEIGHT);
        appLayout.assertOverlayMode();

        appLayout.openDrawer();
        appLayout.closeDrawerWithEscape();

        appLayout.assertDrawerClosed();
    }

    @Test
    public void testClickBackdropInOverlayMode() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        page.setViewportSize(OVERLAY_WIDTH, OVERLAY_HEIGHT);
        appLayout.assertOverlayMode();

        appLayout.openDrawer();
        appLayout.clickBackdrop();

        appLayout.assertDrawerClosed();
    }

    @Test
    public void testDrawerStateIsRestoredWhenLeavingOverlayMode() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertDrawerOpened();

        page.setViewportSize(OVERLAY_WIDTH, OVERLAY_HEIGHT);
        appLayout.assertDrawerClosed();

        page.setViewportSize(1280, 720);
        appLayout.assertNotOverlayMode();
        appLayout.assertDrawerOpened();
    }

    // ── Primary section ───────────────────────────────────────────────

    @Test
    public void testPrimarySection() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertPrimarySection(AppLayoutElement.PRIMARY_SECTION_DRAWER);
        assertEquals(AppLayoutElement.PRIMARY_SECTION_DRAWER, appLayout.getPrimarySection());

        ButtonElement.getByText(page, "Primary section navbar").click();

        appLayout.assertPrimarySection(AppLayoutElement.PRIMARY_SECTION_NAVBAR);
        assertEquals(AppLayoutElement.PRIMARY_SECTION_NAVBAR, appLayout.getPrimarySection());

        ButtonElement.getByText(page, "Primary section drawer").click();

        appLayout.assertPrimarySection(AppLayoutElement.PRIMARY_SECTION_DRAWER);
    }

    // ── Interface mixins ──────────────────────────────────────────────

    @Test
    public void testTheme() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);
        appLayout.assertTheme(null);

        ButtonElement.getByText(page, "Add theme").click();

        appLayout.assertTheme("demo-theme");
        assertEquals("demo-theme", appLayout.getTheme());
    }

    @Test
    public void testCssClass() {
        AppLayoutElement appLayout = AppLayoutElement.get(page);

        ButtonElement.getByText(page, "Add class name").click();

        appLayout.assertCssClass("custom-app-layout");
        assertEquals("custom-app-layout", appLayout.getCssClass());
    }
}
