package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

/**
 * PlaywrightElement for {@code <vaadin-menu-bar>}.
 * <p>
 * Provides utilities to access menu items and open submenus.
 */
public class MenuBarElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-menu-bar";

    /**
     * Create a {@code MenuBarElement} from the page.
     *
     * @param page the Playwright page
     */
    public MenuBarElement(Page page) {
        this(page.locator(FIELD_TAG_NAME));
    }

    /**
     * Create a {@code MenuBarElement} from an existing locator.
     *
     * @param locator the locator for the {@code <vaadin-menu-bar>} element
     */
    public MenuBarElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a menu item by visible label.
     *
     * @param name the visible label of the menu item
     * @return the matching {@code MenuItemElement}
     */
    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.getByLabel(getLocator(), name);
    }

    /**
     * Click a menu item to open its submenu and return the submenu overlay.
     *
     * @param name the visible label of the menu item to click
     * @return the opened {@code MenuElement} submenu overlay
     */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        menuItemElement.click();
        menuItemElement.assertExpanded();
        return new MenuElement(getLocator().page());
    }

    /**
     * Get a menu bar by its accessible label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the menu bar
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getByLabel(Page page, String label) {
        return new MenuBarElement(
                page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName(label))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
