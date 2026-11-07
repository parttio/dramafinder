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
 * PlaywrightElement for the menu overlay list {@code <vaadin-menu-bar-list-box>}.
 * <p>
 * The list opens in an overlay at the page root; {@code find(page, MenuElement.class)}
 * finds it there once the menu is open.
 */
@PlaywrightElement(MenuElement.FIELD_TAG_NAME)
public class MenuElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement,
        Findable<ComponentQuery<MenuElement>> {

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
        return MenuItemElement.byName(getLocator(), name);
    }

    /** Click a menu item to open its submenu and return the next overlay. */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        menuItemElement.click();
        waitAfterAction();
        menuItemElement.assertExpanded();
        return new MenuElement(getLocator().page());
    }


    /**
     * Get the {@code MenuElement} by its accessible name (ARIA role {@code menu}),
     * searching the entire page.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two matching components fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the full accessible name
     * @return the matching {@code MenuElement}
     * @deprecated A {@code MenuElement} has no visible label: this matches its
     *             accessible name. Use {@code find(page, MenuElement.class).withAccessibleName(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static MenuElement getByLabel(Page page, String label) {
        return new MenuElement(
                page.getByRole(AriaRole.MENU, new Page.GetByRoleOptions().setName(label).setExact(true))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

    /**
     * Get the {@code MenuElement} whose accessible name (ARIA role {@code menu}) contains
     * {@code text}, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring. No {@code .first()} is
     * applied: several matching components fail with a strict-mode error.
     *
     * @param page  the Playwright page
     * @param text  part of the accessible name
     * @return the matching {@code MenuElement}
     * @deprecated A {@code MenuElement} has no visible label: this matches its
     *             accessible name. Use {@code find(page, MenuElement.class).withAccessibleNameContaining(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static MenuElement getByLabelContaining(Page page, String text) {
        return new MenuElement(
                page.getByRole(AriaRole.MENU, new Page.GetByRoleOptions().setName(text))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
