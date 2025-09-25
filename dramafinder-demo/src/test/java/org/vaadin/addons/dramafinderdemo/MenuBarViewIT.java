package org.vaadin.addons.dramafinderdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.MenuBarElement;
import org.vaadin.addons.dramafinder.element.MenuElement;
import org.vaadin.addons.dramafinder.element.MenuItemElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MenuBarViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "menubar";
    }

    @Test
    public void testMenuBar() {
        MenuBarElement menuBarElement = new MenuBarElement(page);
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
        MenuBarElement menuBarElement = new MenuBarElement(page);
        MenuElement share = menuBarElement.openSubMenu("Share");
        MenuElement menu = share.openSubMenu("On social media");
        MenuItemElement menuItemByEmailElement = menu.getMenuItemElement("Facebook");
        menuItemByEmailElement.click();
        assertText("Facebook");
    }


    @Test
    public void testHasClass() {
        MenuBarElement menuBarElement = new MenuBarElement(page);
        menuBarElement.assertCssClass("custom-menu-bar");
    }

    @Test
    public void testTheme() {
        MenuBarElement menuBarElement = new MenuBarElement(page);
        menuBarElement.assertTheme("contrast");
    }


    private void assertText(String text) {
        assertThat(page.locator("#text")).hasText(String.format("Clicked item: %s", text));
    }
}
