package org.vaadin.addons.dramafinder.element;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

/**
 * PlaywrightElement for a menu bar sub-menu, the list {@code <vaadin-menu-bar-list-box>}.
 * <p>
 * The list renders inside its {@code vaadin-menu-bar}, one
 * {@code vaadin-menu-bar-submenu} level per nesting level. A menu returned by
 * {@link MenuBarElement#openSubMenu(String)} or {@link #openSubMenu(String)} is
 * scoped to that menu bar and level; {@code find(page, MenuElement.class)} finds
 * every list box rendered on the page, including those of closed menus.
 */
@PlaywrightElement(MenuElement.FIELD_TAG_NAME)
public class MenuElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement,
        Findable<ComponentQuery<MenuElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar-list-box";

    static final String SUB_MENU_TAG_NAME = "vaadin-menu-bar-submenu";

    /**
     * A {@code vaadin-menu-bar-submenu} directly below a menu bar or a sub-menu:
     * each level nests the next one as a {@code slot="submenu"} child.
     */
    private static final String CHILD_SUB_MENU = ":scope > " + SUB_MENU_TAG_NAME;

    /**
     * The list box of a {@code vaadin-menu-bar-submenu}, without those of the
     * sub-menus nested in it, whatever wraps it inside the overlay.
     */
    private static final String OWN_LIST_BOX = ":scope > :not(" + SUB_MENU_TAG_NAME + ") " + FIELD_TAG_NAME;

    /** The {@code vaadin-menu-bar-submenu} this list box belongs to; {@code null} when not known. */
    private final Locator subMenu;

    /**
     * Create a {@code MenuElement} matching every menu bar list box on the page.
     * <p>
     * Several open levels, or another menu bar, make it ambiguous; prefer the
     * menu returned by {@link MenuBarElement#openSubMenu(String)}.
     *
     * @param page the Playwright page
     */
    public MenuElement(Page page) {
        this(page.locator(FIELD_TAG_NAME));
    }

    /** Create a {@code MenuElement} from an existing locator. */
    public MenuElement(Locator locator) {
        super(locator);
        this.subMenu = null;
    }

    private MenuElement(Locator listBox, Locator subMenu) {
        super(listBox);
        this.subMenu = subMenu;
    }

    /** The first-level menu of {@code menuBar}: the one its top-level items and its overflow button open. */
    static MenuElement firstLevel(Locator menuBar) {
        return ofSubMenu(menuBar.locator(CHILD_SUB_MENU));
    }

    private static MenuElement ofSubMenu(Locator subMenu) {
        return new MenuElement(subMenu.locator(OWN_LIST_BOX), subMenu);
    }

    /**
     * Get an item of this menu by its label.
     * <p>
     * The label is the item's own text, matched in full and case-sensitively; an
     * item is also found by its accessible name, which is all an icon-only item
     * with an {@code aria-label} has. Items of nested sub-menus are not
     * considered; two items sharing the label fail with a strict-mode error.
     *
     * @param name the item's full text or accessible name
     * @return the matching item
     */
    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.byLabel(MenuItemElement.subMenuItems(getLocator()), name);
    }

    /**
     * Get the labels of this menu's items, in display order: each item's own
     * text, or its {@code aria-label} when it has no text. Items of nested
     * sub-menus are left out.
     *
     * @return the item labels; empty when the menu has no items
     */
    public List<String> getItemLabels() {
        return MenuItemElement.labelsOf(MenuItemElement.subMenuItems(getLocator()));
    }

    /**
     * Click an item of this menu and return the nested sub-menu it opens.
     * <p>
     * The returned menu is the list box one level below this one, in the same
     * menu bar; this waits until it is visible.
     *
     * @param name the item's full text or accessible name, as for
     *             {@link #getMenuItemElement(String)}
     * @return the opened nested sub-menu
     */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        menuItemElement.click();
        waitAfterAction();
        menuItemElement.assertExpanded();
        MenuElement menu = ofSubMenu(ownSubMenu().locator(CHILD_SUB_MENU));
        menu.assertVisible();
        return menu;
    }

    /**
     * The {@code vaadin-menu-bar-submenu} of this list box. A menu returned by
     * the library knows it; one built from an arbitrary locator looks for its
     * nearest enclosing sub-menu, which only an ancestor axis can express.
     */
    private Locator ownSubMenu() {
        return subMenu != null ? subMenu : getLocator().locator("xpath=ancestor::" + SUB_MENU_TAG_NAME + "[1]");
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
