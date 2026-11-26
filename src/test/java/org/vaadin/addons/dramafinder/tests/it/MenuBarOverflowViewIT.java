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
    public void clickingAnOverflowedItemGoesThroughTheOverflowMenu() {
        MenuBarElement menuBar = phoneSizedMenuBar();
        menuBar.getMenuItemElement("Print").click();
        assertText("Print");

        // the overflow menu has closed and given the item back: it works again
        menuBar.getMenuItemElement("Edit").click();
        assertText("Edit");
        menuBar.getMenuItemElement("Print").click();
        assertText("Print");
    }

    @Test
    public void anItemHeldAcrossClicksStillWorksThroughTheOverflowMenu() {
        MenuItemElement print = phoneSizedMenuBar().getMenuItemElement("Print");
        print.click();
        assertText("Print");
        page.locator("#text").click();
        print.click();
        assertText("Print");
    }

    @Test
    public void labelsStayReadableOnceTheOverflowMenuHasBeenOpened() {
        MenuBarElement menuBar = phoneSizedMenuBar();
        menuBar.getMenuItemElement("Print").click();
        assertText("Print");

        assertEquals(List.of("File", "Edit", "View", "Insert", "Format", "Share", "Print"),
                menuBar.getItemLabels());
        assertEquals(List.of("Share", "Print"), menuBar.getOverflowItemLabels());
    }

    @Test
    public void clickingAVisibleItemStillClicksItDirectly() {
        phoneSizedMenuBar().getMenuItemElement("Edit").click();
        assertText("Edit");
    }

    @Test
    public void openSubMenuOfAnOverflowedItemGoesThroughTheOverflowMenu() {
        MenuElement share = phoneSizedMenuBar().openSubMenu("Share");
        assertEquals(List.of("By email", "Get Link"), share.getItemLabels());
        share.getMenuItemElement("Get Link").click();
        assertText("Get Link");
    }

    private void assertText(String text) {
        assertThat(page.locator("#text")).hasText("Clicked item: " + text);
    }
}
