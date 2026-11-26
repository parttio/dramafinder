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
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-menu-bar>}.
 * <p>
 * Finds the top-level items, opens their sub-menus and closes them again. The
 * sub-menus render inside the menu bar, so a {@link MenuElement} returned by
 * {@link #openSubMenu(String)} stays scoped to this menu bar and to its level,
 * however many levels or other menu bars are open.
 */
@PlaywrightElement(MenuBarElement.FIELD_TAG_NAME)
public class MenuBarElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement,
        Findable<ComponentQuery<MenuBarElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar";

    /** Create a {@code MenuBarElement} from the page. */
    public MenuBarElement(Page page) {
        this(page.locator(FIELD_TAG_NAME));
    }

    /** Whether items found through this element go through "More options" when they are overflowed. */
    private final boolean unfoldOverflow;

    /** Create a {@code MenuBarElement} from an existing locator. */
    public MenuBarElement(Locator locator) {
        this(locator, false);
    }

    private MenuBarElement(Locator locator, boolean unfoldOverflow) {
        super(locator);
        this.unfoldOverflow = unfoldOverflow;
    }

    /**
     * Get a view of this menu bar that reaches collapsed items through the
     * overflow menu automatically.
     * <p>
     * By default an item collapsed into the overflow menu ("More options") is
     * not clickable, as for a user: {@link MenuItemElement#click()} and
     * {@link #openSubMenu(String)} fail on it, and the test opens the overflow
     * menu itself with {@link #openOverflowMenu()}. Items found through the
     * returned element instead open "More options" first when they are
     * collapsed, and are clicked in place otherwise, so the same test runs at
     * every viewport width.
     *
     * @return a {@code MenuBarElement} on the same menu bar that unfolds the overflow menu
     */
    public MenuBarElement unfoldingOverflow() {
        return new MenuBarElement(getLocator(), true);
    }

    /**
     * Get a top-level item of this menu bar by its label.
     * <p>
     * The label is the item's own text, matched in full and case-sensitively; an
     * item is also found by its accessible name, which is all an icon-only item
     * with an {@code aria-label} has. Items of open sub-menus and the overflow
     * button are not considered; two items sharing the label fail with a
     * strict-mode error. An item collapsed into the overflow menu is found too;
     * see {@link MenuItemElement#click()} for clicking it.
     *
     * @param name the item's full text or accessible name
     * @return the matching top-level item
     */
    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.topLevelByLabel(getLocator(), name, unfoldOverflow);
    }

    /**
     * Get the labels of the top-level items, in display order: each item's own
     * text, or its {@code aria-label} when it has no text. Items collapsed into
     * the overflow menu are included; the overflow button ("More options")
     * itself never is.
     *
     * @return the labels of the menu bar's items; empty when there are none
     */
    public List<String> getItemLabels() {
        return MenuItemElement.labelsOf(MenuItemElement.topLevelItems(getLocator()));
    }

    /**
     * Get the labels of the top-level items that do not fit in the menu bar and
     * are collapsed into its overflow menu ("More options"), in display order.
     * <p>
     * The menu bar lays its items out again after a resize, so read this once
     * the layout has settled. Read without waiting; empty while the menu bar
     * itself is hidden.
     *
     * @return the labels of the overflowed items; empty when every item fits
     */
    public List<String> getOverflowItemLabels() {
        if (!MenuItemElement.overflowButton(getLocator()).isVisible()) {
            return List.of();
        }
        return MenuItemElement.labelsOf(MenuItemElement.overflowedItems(getLocator()));
    }

    /**
     * Open the overflow menu ("More options") that holds the items the menu bar
     * has no room for, and return it.
     * <p>
     * This is the step a user takes on a narrow screen before reaching a
     * collapsed item: the returned menu lists the collapsed items, clicks them
     * and opens their sub-menus like any first-level sub-menu. Waits until the
     * overflow button is shown, then until the menu is visible.
     *
     * @return the opened overflow menu
     * @throws AssertionError if the overflow button does not show up before the
     *                        assertion timeout, because every item fits
     */
    public MenuElement openOverflowMenu() {
        Locator overflowButton = MenuItemElement.overflowButton(getLocator());
        assertThat(overflowButton).isVisible();
        overflowButton.click();
        waitAfterAction();
        MenuElement overflowMenu = new MenuElement(getLocator().locator(MenuElement.ROOT_LIST_BOX));
        overflowMenu.assertVisible();
        return overflowMenu;
    }

    /**
     * Click a top-level item and return the sub-menu it opens.
     * <p>
     * The returned menu is the first-level list box of this menu bar, so it
     * neither matches another menu bar's sub-menu nor a nested level; this
     * waits until it is visible.
     * <p>
     * An item collapsed into the overflow menu fails at once with an
     * {@link IllegalStateException}; open it through {@link #openOverflowMenu()}
     * instead. Through {@link #unfoldingOverflow()}, such an item is reached
     * through "More options" as a user does, and the returned menu is the
     * item's sub-menu, one level below the overflow menu.
     *
     * @param name the item's full text or accessible name, as for
     *             {@link #getMenuItemElement(String)}
     * @return the opened first-level sub-menu
     * @throws IllegalStateException if the item is collapsed into the overflow
     *                               menu and this element is not
     *                               {@link #unfoldingOverflow()}
     */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
        if (menuItemElement.isOverflowed()) {
            String label = menuItemElement.getLabel();
            menuItemElement.requireUnfolding(label);
            return menuItemElement.openOverflowMenu().openSubMenu(label);
        }
        menuItemElement.click();
        waitAfterAction();
        menuItemElement.assertExpanded();
        MenuElement menu = new MenuElement(getLocator().locator(MenuElement.ROOT_LIST_BOX));
        menu.assertVisible();
        return menu;
    }

    /**
     * Close every open sub-menu of this menu bar and wait until they are gone.
     * <p>
     * A closing sub-menu stays visible while it animates out; this returns only
     * once no list box of this menu bar is visible, so the same menu can be
     * opened again right away. Does nothing when no sub-menu is open.
     */
    public void close() {
        getLocator().evaluate("el => el.close()");
        waitAfterAction();
        assertThat(getLocator().locator(MenuElement.FIELD_TAG_NAME + " >> visible=true")).hasCount(0);
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
     * @deprecated A {@code MenuBarElement} has no visible label: this matches its
     *             accessible name. Use {@code find(page, MenuBarElement.class).withAccessibleName(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
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
     * @deprecated A {@code MenuBarElement} has no visible label: this matches its
     *             accessible name. Use {@code find(page, MenuBarElement.class).withAccessibleNameContaining(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static MenuBarElement getByLabelContaining(Page page, String text) {
        return new MenuBarElement(
                page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName(text))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code MenuBarElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getById(Page page, String id) {
        return new MenuBarElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code MenuBarElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getById(Locator locator, String id) {
        return new MenuBarElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code MenuBarElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getByTestId(Page page, String testId) {
        return new MenuBarElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code MenuBarElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code MenuBarElement}
     */
    public static MenuBarElement getByTestId(Locator locator, String testId) {
        return new MenuBarElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
