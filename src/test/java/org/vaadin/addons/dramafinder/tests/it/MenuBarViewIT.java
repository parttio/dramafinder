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
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MenuBarViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "menubar";
    }

    private MenuBarElement mainMenuBar() {
        return MenuBarElement.getById(page, "main-menu-bar");
    }

    private MenuBarElement otherMenuBar() {
        return MenuBarElement.getById(page, "other-menu-bar");
    }

    @Test
    public void testMenuBar() {
        MenuBarElement menuBarElement = mainMenuBar();
        MenuItemElement menuItemElement = menuBarElement.getMenuItemElement("View");
        menuItemElement.click();

        assertText("View");
        MenuItemElement menuItemEditElement = menuBarElement.getMenuItemElement("Edit");
        menuItemEditElement.click();
        assertText("Edit");
        MenuElement share = menuBarElement.openSubMenu("Share");
        MenuItemElement menuItemByEmailElement = share.getMenuItemElement("By email");
        menuItemByEmailElement.click();
        assertText("By email");
    }

    @Test
    public void openSubMenu() {
        MenuBarElement menuBarElement = mainMenuBar();
        MenuElement share = menuBarElement.openSubMenu("Share");
        MenuElement menu = share.openSubMenu("On social media");
        MenuItemElement menuItemByEmailElement = menu.getMenuItemElement("Facebook");
        menuItemByEmailElement.click();
        assertText("Facebook");
    }

    @Test
    public void openMenuIsFoundFromThePageRoot() {
        mainMenuBar().openSubMenu("Share");
        MenuElement share = find(page, MenuElement.class).single();
        assertThat(share.getLocator()).isVisible();
        share.getMenuItemElement("By email").click();
        assertText("By email");
    }

    @Test
    public void nestedSubMenuWithAnotherMenuBarOnThePage() {
        MenuElement file = mainMenuBar().openSubMenu("File");
        MenuElement export = file.openSubMenu("Export");
        export.getMenuItemElement("PDF").click();
        assertText("PDF");

        otherMenuBar().openSubMenu("File").openSubMenu("Export").getMenuItemElement("PDF").click();
        assertText("Other PDF");
    }

    @Test
    public void nestedSubMenusStayScopedToTheirLevel() {
        MenuElement file = mainMenuBar().openSubMenu("File");
        MenuElement export = file.openSubMenu("Export");
        // two levels are open: each menu still resolves to its own list box
        assertThat(file.getLocator()).isVisible();
        assertThat(export.getLocator()).isVisible();
        assertEquals(List.of("New", "Export", "Print", "Close"), file.getItemLabels());
        assertEquals(List.of("PDF", "CSV"), export.getItemLabels());
    }

    @Test
    public void itemLabelsAtEveryLevel() {
        MenuBarElement menuBar = mainMenuBar();
        assertEquals(List.of("View", "Edit", "Share", "File", "Archive", "More actions"),
                menuBar.getItemLabels());
        assertEquals(List.of("File"), otherMenuBar().getItemLabels());

        MenuElement share = menuBar.openSubMenu("Share");
        assertEquals(List.of("On social media", "By email", "Get Link"), share.getItemLabels());
        assertEquals(List.of("Facebook", "Twitter", "Instagram"),
                share.openSubMenu("On social media").getItemLabels());
    }

    @Test
    public void disabledItemsReadAsDisabled() {
        MenuBarElement menuBar = mainMenuBar();
        MenuItemElement archive = menuBar.getMenuItemElement("Archive");
        archive.assertDisabled();
        assertFalse(archive.isEnabled());
        menuBar.getMenuItemElement("File").assertEnabled();

        MenuElement file = menuBar.openSubMenu("File");
        MenuItemElement print = file.getMenuItemElement("Print");
        print.assertDisabled();
        assertFalse(print.isEnabled());
        MenuItemElement newItem = file.getMenuItemElement("New");
        newItem.assertEnabled();
        assertTrue(newItem.isEnabled());
    }

    @Test
    public void iconOnlyItemIsFoundByItsAriaLabel() {
        MenuElement more = mainMenuBar().openSubMenu("More actions");
        more.getMenuItemElement("Settings").click();
        assertText("Settings");
    }

    @Test
    public void itemWithTextAndAriaLabelIsFoundByEither() {
        MenuElement file = mainMenuBar().openSubMenu("File");
        assertThat(file.getMenuItemElement("Close").getLocator()).hasCount(1);
        assertThat(file.getMenuItemElement("Close the document").getLocator()).hasCount(1);
        file.getMenuItemElement("Close").click();
        assertText("Close");
    }

    @Test
    public void openCloseAndReopen() {
        MenuBarElement menuBar = mainMenuBar();
        for (int i = 0; i < 3; i++) {
            MenuElement file = menuBar.openSubMenu("File");
            MenuElement export = file.openSubMenu("Export");
            menuBar.close();
            file.assertHidden();
            export.assertHidden();
            menuBar.getMenuItemElement("File").assertCollapsed();
        }
        menuBar.openSubMenu("File").openSubMenu("Export").getMenuItemElement("CSV").click();
        assertText("CSV");
    }

    @Test
    public void closeWithoutOpenMenuDoesNothing() {
        MenuBarElement menuBar = mainMenuBar();
        menuBar.close();
        menuBar.getMenuItemElement("View").click();
        assertText("View");
    }

    @Test
    public void itemsAreFindableByType() {
        MenuBarElement menuBar = mainMenuBar();
        // six buttons; the hidden overflow button is left out
        assertEquals(6, menuBar.find(MenuItemElement.class).count());
        assertEquals(0, menuBar.find(MenuItemElement.class).withAccessibleName("More options").count());

        menuBar.openSubMenu("File").openSubMenu("Export");
        assertEquals(1, menuBar.find(MenuItemElement.class).withText("PDF").count());
        menuBar.find(MenuItemElement.class).withText("Print").single().assertDisabled();
        menuBar.find(MenuItemElement.class).withText("PDF").single().click();
        assertText("PDF");
    }

    @Test
    public void testHasClass() {
        MenuBarElement menuBarElement = mainMenuBar();
        menuBarElement.assertCssClass("custom-menu-bar");
    }

    @Test
    public void testTheme() {
        MenuBarElement menuBarElement = mainMenuBar();
        menuBarElement.assertTheme("contrast");
    }

    private void assertText(String text) {
        assertThat(page.locator("#text")).hasText(String.format("Clicked item: %s", text));
    }
}
