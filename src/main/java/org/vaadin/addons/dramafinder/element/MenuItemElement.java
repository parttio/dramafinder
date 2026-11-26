package org.vaadin.addons.dramafinder.element;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.AccessibleNames;
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
     * inside a button — when it has no text. A button collapsed into the
     * overflow menu has lent its content ({@code item.component}) to that menu
     * once it was opened, and gets it back only when the menu bar lays out its
     * items again, so the label is read from that content wherever it is.
     */
    private static final String LABELS_SCRIPT = """
            els => els.map(button => {
              const lent = button.item?.component;
              const el = lent instanceof Element && !button.contains(lent) ? lent : button;
              const text = [...el.childNodes]
                .filter(c => c.nodeType === 3 || (c.nodeType === 1 && !c.hasAttribute('slot')))
                .map(c => c.textContent).join(' ').replace(/\\s+/g, ' ').trim();
              if (text) return text;
              const named = el.hasAttribute('aria-label') ? el : el.querySelector(':scope > [aria-label]');
              return named ? named.getAttribute('aria-label').trim() : '';
            })
            """;

    /**
     * The menu bar of a top-level item found through {@link MenuBarElement}, or
     * {@code null}. Kept apart from the item's own locator, which is usually
     * keyed on the item's text: while the overflow menu is open, the menu bar
     * moves the text of an overflowed button into that menu.
     */
    private final Locator menuBar;

    /** Whether a click on this item, when it is overflowed, goes through "More options". */
    private final boolean unfoldOverflow;

    /** Create a {@code MenuItemElement} from an existing locator. */
    public MenuItemElement(Locator locator) {
        this(locator, null, false);
    }

    /** Create a top-level item of the given menu bar. */
    MenuItemElement(Locator locator, Locator menuBar, boolean unfoldOverflow) {
        super(locator);
        this.menuBar = menuBar;
        this.unfoldOverflow = unfoldOverflow;
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

    /**
     * Click the item, then wait for the server round-trip it triggers; a
     * top-level item collapsed into the overflow menu fails at once instead.
     * <p>
     * Such an item (see {@link #isOverflowed()}) is not clickable in place, as
     * for a user: this throws an {@link IllegalStateException}, unless the item was found through
     * {@link MenuBarElement#unfoldingOverflow()}, in which case it opens "More
     * options" and clicks the item there, as a user does.
     *
     * @throws IllegalStateException if the item is collapsed into the overflow
     *                               menu and was not found through
     *                               {@code unfoldingOverflow()}
     */
    @Override
    public void click() {
        if (menuBar != null && isOverflowed()) {
            String label = getLabel();
            requireUnfolding(label);
            openOverflowMenu().getMenuItemElement(label).click();
            waitAfterAction();
        } else {
            super.click();
        }
    }

    /** Fails unless this overflowed item may be reached through "More options". */
    void requireUnfolding(String label) {
        if (!unfoldOverflow) {
            throw new IllegalStateException("\"" + label + "\" is collapsed into the overflow menu (\"More options\")"
                    + " and is not visible: open it with MenuBarElement.openOverflowMenu(), or find the item"
                    + " through MenuBarElement.unfoldingOverflow() to go through it automatically");
        }
    }

    /** This item's label, as {@link MenuBarElement#getItemLabels()} reads it. */
    String getLabel() {
        return labelsOf(getLocator()).getFirst();
    }

    /** Open the overflow menu of this item's menu bar; see {@link MenuBarElement#openOverflowMenu()}. */
    MenuElement openOverflowMenu() {
        return new MenuBarElement(menuBar).openOverflowMenu();
    }

    /**
     * Whether this is a top-level item collapsed into the menu bar's overflow
     * menu ("More options") because it does not fit: the menu bar keeps such a
     * button in place but hides it with {@code visibility: hidden}. A sub-menu
     * item is never overflowed. Read without waiting.
     *
     * @return {@code true} when the item is reachable only through the overflow menu
     */
    public boolean isOverflowed() {
        return (Boolean) getLocator().evaluate(
                "el => el.localName === '" + FIELD_TAG_NAME + "' && el.slot !== 'overflow'"
                        + " && getComputedStyle(el).visibility === 'hidden'");
    }

    /**
     * Assert that this top-level item is collapsed into the overflow menu,
     * retrying until the menu bar has laid out its items.
     */
    public void assertOverflowed() {
        assertThat(getLocator()).hasCSS("visibility", "hidden");
    }

    /**
     * Assert that this item is shown in the menu bar itself, not collapsed into
     * the overflow menu, retrying until the menu bar has laid out its items.
     */
    public void assertNotOverflowed() {
        assertThat(getLocator()).not().hasCSS("visibility", "hidden");
    }

    /**
     * The menu bar's own buttons, those collapsed into the overflow menu
     * included, without the overflow button itself or sub-menu items.
     */
    static Locator topLevelItems(Locator menuBar) {
        return menuBar.locator("xpath=./" + FIELD_TAG_NAME + "[not(@hidden)][not(@slot='overflow')]");
    }

    /**
     * The menu bar's buttons that are collapsed into the overflow menu: while
     * the overflow button is shown, the menu bar hides each item that no longer
     * fits with {@code visibility: hidden}.
     */
    static Locator overflowedItems(Locator menuBar) {
        return topLevelItems(menuBar).filter(new Locator.FilterOptions().setVisible(false));
    }

    /** The menu bar's overflow button ("More options"), {@code hidden} while every item fits. */
    static Locator overflowButton(Locator menuBar) {
        return menuBar.locator("xpath=./" + FIELD_TAG_NAME + "[@slot='overflow']");
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
        return new MenuItemElement(byLabelLocator(items, label));
    }

    /** Like {@link #byLabel(Locator, String)}, for a top-level item of {@code menuBar}. */
    static MenuItemElement topLevelByLabel(Locator menuBar, String label, boolean unfoldOverflow) {
        Locator items = topLevelItems(menuBar);
        Locator item = byLabelLocator(items, label);
        // Also pin the item by position when its label is already on the page: an
        // overflowed button loses its text to the overflow menu once that menu has
        // been opened, and only the position still finds it then.
        List<String> labels = labelsOf(items);
        if (labels.indexOf(label) >= 0 && labels.indexOf(label) == labels.lastIndexOf(label)) {
            item = item.or(items.nth(labels.indexOf(label)));
        }
        return new MenuItemElement(item, menuBar, unfoldOverflow);
    }

    private static Locator byLabelLocator(Locator items, String label) {
        Locator byText = new TextQuery<>(MenuItemElement.class, items).withText(label).getLocator();
        Locator byName = items.and(items.page().getByRole(AriaRole.MENUITEM,
                new Page.GetByRoleOptions().setName(label).setExact(true)));
        return byText.or(byName);
    }

    /** The label of each of {@code items}, in DOM order; see {@link #byLabel}. */
    @SuppressWarnings("unchecked")
    static List<String> labelsOf(Locator items) {
        return (List<String>) items.evaluateAll(LABELS_SCRIPT);
    }

    /**
     * Get the menu item's accessible name: its {@code aria-label} or
     * {@code aria-labelledby} text when either attribute is set, otherwise its
     * own text, without slotted children such as an icon or a tooltip — what
     * {@code withAccessibleName} matches. Read without waiting.
     *
     * @return the whitespace-normalized name, or {@code null} when it has none
     */
    @Override
    public String getAccessibleName() {
        return AccessibleNames.labelledOrOwnText(getLocator());
    }
}
