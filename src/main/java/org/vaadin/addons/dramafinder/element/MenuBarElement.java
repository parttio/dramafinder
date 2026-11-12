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

    /** Create a {@code MenuBarElement} from an existing locator. */
    public MenuBarElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a top-level item of this menu bar by its label.
     * <p>
     * The label is the item's own text, matched in full and case-sensitively; an
     * item is also found by its accessible name, which is all an icon-only item
     * with an {@code aria-label} has. Items of open sub-menus and the hidden
     * overflow button are not considered; two items sharing the label fail with
     * a strict-mode error.
     *
     * @param name the item's full text or accessible name
     * @return the matching top-level item
     */
    public MenuItemElement getMenuItemElement(String name) {
        return MenuItemElement.byLabel(MenuItemElement.topLevelItems(getLocator()), name);
    }

    /**
     * Get the labels of the top-level items, in display order: each item's own
     * text, or its {@code aria-label} when it has no text. The hidden overflow
     * button is left out.
     *
     * @return the labels of the menu bar's buttons; empty when there are none
     */
    public List<String> getItemLabels() {
        return MenuItemElement.labelsOf(MenuItemElement.topLevelItems(getLocator()));
    }

    /**
     * Click a top-level item and return the sub-menu it opens.
     * <p>
     * The returned menu is the first-level list box of this menu bar, so it
     * neither matches another menu bar's sub-menu nor a nested level; this
     * waits until it is visible.
     *
     * @param name the item's full text or accessible name, as for
     *             {@link #getMenuItemElement(String)}
     * @return the opened first-level sub-menu
     */
    public MenuElement openSubMenu(String name) {
        MenuItemElement menuItemElement = getMenuItemElement(name);
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
