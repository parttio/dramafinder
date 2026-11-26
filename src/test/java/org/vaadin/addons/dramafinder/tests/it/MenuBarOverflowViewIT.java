package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.MenuBarElement;
import org.vaadin.addons.dramafinder.element.MenuElement;
import org.vaadin.addons.dramafinder.element.MenuItemElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A menu bar whose trailing items collapse into "More options" on a phone-sized viewport (issue #334). */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MenuBarOverflowViewIT extends SpringPlaywrightIT implements HasTestView {

    private static final int PHONE_WIDTH = 375;
    private static final int PHONE_HEIGHT = 812;

    @Override
    public String getView() {
        return "menubar-overflow";
    }

    private MenuBarElement menuBar() {
        return MenuBarElement.getById(page, "overflow-menu-bar");
    }

    /** Shrinks the viewport and waits until the menu bar has moved its trailing items to "More options". */
    private MenuBarElement phoneSizedMenuBar() {
        page.setViewportSize(PHONE_WIDTH, PHONE_HEIGHT);
        MenuBarElement menuBar = menuBar();
        assertThat(menuBar.getLocator().locator("[slot='overflow']")).isVisible();
        return menuBar;
    }

    @Test
    public void itemLabelsKeepOverflowedItemsButNotTheOverflowButton() {
        MenuBarElement menuBar = phoneSizedMenuBar();

        assertEquals(List.of("File", "Edit", "View", "Insert", "Format", "Share", "Print"),
                menuBar.getItemLabels());
    }

    @Test
    public void overflowItemLabelsListTheCollapsedItems() {
        assertEquals(List.of("Share", "Print"), phoneSizedMenuBar().getOverflowItemLabels());
    }

    @Test
    public void overflowItemLabelsAreEmptyWhenEverythingFits() {
        MenuBarElement menuBar = menuBar();
        assertThat(menuBar.getLocator().locator("[slot='overflow']")).isHidden();

        assertEquals(List.of(), menuBar.getOverflowItemLabels());
    }

    @Test
    public void itemReportsWhetherItIsOverflowed() {
        MenuBarElement menuBar = phoneSizedMenuBar();
        MenuItemElement print = menuBar.getMenuItemElement("Print");
        MenuItemElement file = menuBar.getMenuItemElement("File");

        print.assertOverflowed();
        assertTrue(print.isOverflowed());
        file.assertNotOverflowed();
        assertFalse(file.isOverflowed());

        page.setViewportSize(1280, 800);
        print.assertNotOverflowed();
        assertFalse(print.isOverflowed());
    }

    @Test
    public void clickingAnOverflowedItemFailsFastByDefault() {
        MenuItemElement print = phoneSizedMenuBar().getMenuItemElement("Print");

        long start = System.nanoTime();
        IllegalStateException e = assertThrows(IllegalStateException.class, print::click);
        assertTrue(e.getMessage().contains("\"Print\" is collapsed into the overflow menu"), e.getMessage());
        assertTrue(System.nanoTime() - start < 5_000_000_000L, "should not wait for the click timeout");
    }

    @Test
    public void openSubMenuOfAnOverflowedItemFailsFastByDefault() {
        MenuBarElement menuBar = phoneSizedMenuBar();
        assertThrows(IllegalStateException.class, () -> menuBar.openSubMenu("Share"));
    }

    @Test
    public void clickingAVisibleItemStillClicksItDirectly() {
        phoneSizedMenuBar().getMenuItemElement("Edit").click();
        assertText("Edit");
    }

    @Test
    public void openOverflowMenuListsAndClicksTheCollapsedItems() {
        MenuBarElement menuBar = phoneSizedMenuBar();
        MenuElement overflow = menuBar.openOverflowMenu();
        assertEquals(List.of("Share", "Print"), overflow.getItemLabels());
        overflow.getMenuItemElement("Print").click();
        assertText("Print");
    }

    @Test
    public void openOverflowMenuThenASubMenu() {
        phoneSizedMenuBar().openOverflowMenu().openSubMenu("Share").getMenuItemElement("Get Link").click();
        assertText("Get Link");
    }

    @Test
    public void labelsStayReadableOnceTheOverflowMenuHasBeenOpened() {
        MenuBarElement menuBar = phoneSizedMenuBar();
        menuBar.openOverflowMenu().getMenuItemElement("Print").click();
        assertText("Print");

        assertEquals(List.of("File", "Edit", "View", "Insert", "Format", "Share", "Print"),
                menuBar.getItemLabels());
        assertEquals(List.of("Share", "Print"), menuBar.getOverflowItemLabels());
        menuBar.getMenuItemElement("Print").assertOverflowed();
    }

    @Test
    public void openOverflowMenuFailsWhenEverythingFits() {
        MenuBarElement menuBar = menuBar();
        menuBar.getMenuItemElement("Print").assertNotOverflowed();
        assertThrows(AssertionError.class, menuBar::openOverflowMenu);
    }

    @Test
    public void unfoldingClickGoesThroughTheOverflowMenu() {
        MenuBarElement menuBar = phoneSizedMenuBar().unfoldingOverflow();
        menuBar.getMenuItemElement("Print").click();
        assertText("Print");
        menuBar.getMenuItemElement("Edit").click();
        assertText("Edit");
    }

    @Test
    public void unfoldingItemHeldAcrossClicksStillWorks() {
        MenuItemElement print = phoneSizedMenuBar().unfoldingOverflow().getMenuItemElement("Print");
        print.click();
        assertText("Print");
        page.locator("#text").click();
        print.click();
        assertText("Print");
    }

    @Test
    public void unfoldingOpenSubMenuGoesThroughTheOverflowMenu() {
        MenuElement share = phoneSizedMenuBar().unfoldingOverflow().openSubMenu("Share");
        assertEquals(List.of("By email", "Get Link"), share.getItemLabels());
        share.getMenuItemElement("Get Link").click();
        assertText("Get Link");
    }

    @Test
    public void unfoldingWorksTheSameOnAWideViewport() {
        MenuBarElement menuBar = menuBar().unfoldingOverflow();
        menuBar.getMenuItemElement("Print").click();
        assertText("Print");
        menuBar.openSubMenu("Share").getMenuItemElement("By email").click();
        assertText("By email");
    }

    private void assertText(String text) {
        assertThat(page.locator("#text")).hasText("Clicked item: " + text);
    }
}
