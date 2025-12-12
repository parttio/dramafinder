package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ContextMenuElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContextMenuViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "contextmenu";
    }

    @Test
    public void openContextMenuAndSelectItem() {
        ContextMenuElement contextMenu = new ContextMenuElement(page);
        ContextMenuElement.openOn(page.locator("#file-target"));
        contextMenu.assertOpen();

        contextMenu.selectItem("Rename");
        assertThat(page.locator("#file-selection")).hasText("Rename");
        contextMenu.assertClosed();
    }

    @Test
    public void disabledItemRemainsDisabled() {
        ContextMenuElement statusMenu = new ContextMenuElement(page);
        ContextMenuElement.openOn(page.locator("#status-target"));
        statusMenu.assertItemDisabled("Disabled action");
        statusMenu.assertItemEnabled("Refresh");

        statusMenu.selectItem("Refresh");
        assertThat(page.locator("#status-selection")).hasText("Refresh");
        statusMenu.assertClosed();
    }

    @Test
    public void testClassName() {
        ContextMenuElement contextMenu = new ContextMenuElement(page);
        ContextMenuElement.openOn(page.locator("#file-target"));

        contextMenu.assertCssClass("file-context-menu");
    }

    @Test
    public void canSelectFromSubMenu() {
        ContextMenuElement contextMenu = new ContextMenuElement(page);
        ContextMenuElement.openOn(page.locator("#file-target"));

        ContextMenuElement shareMenu = contextMenu.openSubMenu("Share");
        shareMenu.selectItem("Copy link");

        assertThat(page.locator("#file-selection")).hasText("Copy link");
        shareMenu.assertClosed();
    }

    @Test
    public void canToggleCheckableItems() {
        ContextMenuElement contextMenu = new ContextMenuElement(page);
        ContextMenuElement.openOn(page.locator("#checkable-target"));

        contextMenu.assertItemChecked("Project news");
        contextMenu.assertItemNotChecked("Security alerts");

        contextMenu.selectItem("Security alerts");
        assertThat(page.locator("#checkable-selection")).hasText("Project news, Security alerts");

        ContextMenuElement.openOn(page.locator("#checkable-target"));
        contextMenu.assertItemChecked("Security alerts");
        contextMenu.selectItem("Project news");
        assertThat(page.locator("#checkable-selection")).hasText("Security alerts");

        ContextMenuElement.openOn(page.locator("#checkable-target"));
        contextMenu.assertItemNotChecked("Project news");
    }
}
