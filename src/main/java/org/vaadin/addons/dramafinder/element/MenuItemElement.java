package org.vaadin.addons.dramafinder.element;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.TextQuery;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for a menu bar item: a top-level {@code <vaadin-menu-bar-button>}
 * or a sub-menu {@code <vaadin-menu-bar-item>}.
 * <p>
 * Both carry the {@code menuitem} role and {@code aria-disabled="true"} when
 * disabled, so the enabled state is read the way Playwright reads it. The typed
 * query finds both kinds, leaving out the menu bar's hidden overflow button.
 */
@PlaywrightElement(MenuItemElement.SELECTOR)
public class MenuItemElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement,
        HasEnabledElement, Findable<TextQuery<MenuItemElement>> {

    /** Tag of a top-level item, a button of the menu bar. */
    public static final String FIELD_TAG_NAME = "vaadin-menu-bar-button";

    /** Tag of an item inside a sub-menu. */
    public static final String SUB_MENU_ITEM_TAG_NAME = "vaadin-menu-bar-item";

    /**
     * Selector the typed query searches for: visible-capable top-level buttons
     * (the overflow button while {@code hidden} is left out) and sub-menu items.
     * The {@code vaadin-menu-bar-item} wrapped inside each button has no role and
     * is not counted twice.
     */
    public static final String SELECTOR = ":is(" + FIELD_TAG_NAME + ":not([hidden]), "
            + SUB_MENU_ITEM_TAG_NAME + "[role='menuitem'])";

    /**
     * Each item's label: its own text (named-slot children left out), or its
     * {@code aria-label} — on the item, or on the {@code vaadin-menu-bar-item}
     * inside a button — when it has no text.
     */
    private static final String LABELS_SCRIPT = """
            els => els.map(el => {
              const text = [...el.childNodes]
                .filter(c => c.nodeType === 3 || (c.nodeType === 1 && !c.hasAttribute('slot')))
                .map(c => c.textContent).join(' ').replace(/\\s+/g, ' ').trim();
              if (text) return text;
              const named = el.hasAttribute('aria-label') ? el : el.querySelector(':scope > [aria-label]');
              return named ? named.getAttribute('aria-label').trim() : '';
            })
            """;

    /** Create a {@code MenuItemElement} from an existing locator. */
    public MenuItemElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a menu item by its full accessible label within a scope.
     * <p>
     * The label must match the item's whole accessible name, so {@code "Save"}
     * does not resolve to {@code "Save as…"}. Two items sharing the label fail
     * with a Playwright strict-mode error.
     * @deprecated A {@code MenuItemElement} has no visible label: this matches its
     *             accessible name. Use {@code find(locator, MenuItemElement.class).withAccessibleName(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static MenuItemElement getByLabel(Locator locator, String label) {
        return new MenuItemElement(ItemLocator.byExactName(locator, AriaRole.MENUITEM, label));
    }

    /** Assert that the menu item is expanded (shows submenu). */
    public void assertExpanded() {
        assertThat(getLocator()).hasAttribute("expanded", "");
    }

    /** Assert that the menu item is collapsed. */
    public void assertCollapsed() {
        assertThat(getLocator()).not().hasAttribute("expanded", "");
    }

    /** The menu bar's own buttons, without the hidden overflow button or sub-menu items. */
    static Locator topLevelItems(Locator menuBar) {
        return menuBar.locator("xpath=./" + FIELD_TAG_NAME + "[not(@hidden)]");
    }

    /** The items of one sub-menu list box, without those of nested sub-menus. */
    static Locator subMenuItems(Locator listBox) {
        return listBox.locator("xpath=./" + SUB_MENU_ITEM_TAG_NAME);
    }

    /**
     * Narrow {@code items} to the one labelled {@code label}: its own text is
     * exactly {@code label}, or its accessible name is (the only name an
     * icon-only item has, and the one an {@code aria-label} gives an item that
     * also shows a text). Case-sensitive, no {@code .first()}.
     */
    static MenuItemElement byLabel(Locator items, String label) {
        Locator byText = new TextQuery<>(MenuItemElement.class, items).withText(label).getLocator();
        Locator byName = items.and(items.page().getByRole(AriaRole.MENUITEM,
                new Page.GetByRoleOptions().setName(label).setExact(true)));
        return new MenuItemElement(byText.or(byName));
    }

    /** The label of each of {@code items}, in DOM order; see {@link #byLabel}. */
    @SuppressWarnings("unchecked")
    static List<String> labelsOf(Locator items) {
        return (List<String>) items.evaluateAll(LABELS_SCRIPT);
    }
}
