package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasStyleElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;

public class MenuBarElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar";

    public MenuBarElement(Page page) {
        this(page.locator(FIELD_TAG_NAME));
    }

    public MenuBarElement(Locator locator) {
        super(locator);
    }

    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.getByLabel(getLocator(), name);
    }

    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        menuItemElement.click();
        menuItemElement.assertExpanded();
        return new MenuElement(getLocator().page());
    }

    public static MenuBarElement getByLabel(Page page, String label) {
        return new MenuBarElement(
                page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName(label))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
