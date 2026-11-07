package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

/**
 * PlaywrightElement for {@code <vaadin-menu-bar>}.
 * <p>
 * Provides utilities to access menu items and open submenus.
 */
@PlaywrightElement(MenuBarElement.FIELD_TAG_NAME)
public class MenuBarElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement,
        Findable<ComponentQuery<MenuBarElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar";

    /** Create a {@code MenuBarElement} from the page. */
    public MenuBarElement(Page page) {
        this(page.locator(FIELD_TAG_NAME));
    }

    /** Create a {@code MenuBarElement} from an existing locator. */
    public MenuBarElement(Locator locator) {
        super(locator);
    }

    /** Get a menu item by visible label. */
    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.getByLabel(getLocator(), name);
    }

    /**
     * Click a menu item to open its submenu and return the submenu overlay.
     */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        menuItemElement.click();
        waitAfterAction();
        menuItemElement.assertExpanded();
        return new MenuElement(getLocator().page());
    }

    /**
     * Get the {@code MenuBarElement} by its accessible name (ARIA role {@code menubar}),
     * searching the entire page.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two matching components fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the full accessible name
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getByLabel(Page page, String label) {
        return new MenuBarElement(
                page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName(label).setExact(true))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

    /**
     * Get the {@code MenuBarElement} whose accessible name (ARIA role {@code menubar}) contains
     * {@code text}, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring. No {@code .first()} is
     * applied: several matching components fail with a strict-mode error.
     *
     * @param page  the Playwright page
     * @param text  part of the accessible name
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getByLabelContaining(Page page, String text) {
        return new MenuBarElement(
                page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName(text))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
