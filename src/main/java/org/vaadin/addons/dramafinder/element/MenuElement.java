package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

/**
 * PlaywrightElement for the menu overlay list {@code <vaadin-menu-bar-list-box>}.
 */
public class MenuElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar-list-box";

    /** Create a {@code MenuElement} from the page. */
    public MenuElement(Page page) {
        super(page.locator(FIELD_TAG_NAME));
    }

    /** Create a {@code MenuElement} from an existing locator. */
    public MenuElement(Locator locator) {
        super(locator);
    }

    /** Get a menu item by its visible label within this menu. */
    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.getByLabel(getLocator(), name);
    }

    /** Click a menu item to open its submenu and return the next overlay. */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        menuItemElement.click();
        menuItemElement.assertExpanded();
        return new MenuElement(getLocator().page());
    }


    /** Get a menu overlay by its accessible label. */
    public static MenuElement getByLabel(Page page, String label) {
        return new MenuElement(
                page.getByRole(AriaRole.MENU, new Page.GetByRoleOptions().setName(label))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
