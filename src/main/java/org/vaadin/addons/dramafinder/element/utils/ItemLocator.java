package org.vaadin.addons.dramafinder.element.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Utility for locating a single item inside a container — an option in a
 * {@code vaadin-combo-box} overlay, a tab in a {@code vaadin-tabs}, a checkbox
 * in a {@code vaadin-checkbox-group}, a menu item in a menu — by the item's
 * full label.
 * <p>
 * Every lookup here matches the label <strong>exactly</strong> (case-sensitive,
 * whitespace-normalized) and returns an unfiltered locator, i.e. no
 * {@code .first()}. Both properties are deliberate: Playwright's default
 * accessible-name and text matching is a case-insensitive <em>substring</em>
 * match, so {@code "Option 1"} also matches {@code "Option 10"}, and a trailing
 * {@code .first()} would silently resolve that ambiguity in DOM order. In a test
 * library that is worse than a misdirected click — an assertion keyed on the same
 * lookup passes against the wrong element. Without {@code .first()}, a genuine
 * duplicate fails loudly with a Playwright strict-mode error instead.
 *
 * @see AccessibleNameLocator for the substring-matching lookups used by the
 *      page-level field factories
 */
public final class ItemLocator {

    private ItemLocator() {
    }

    /**
     * Locator for elements whose own visible text is exactly {@code text}.
     * <p>
     * Exact text matching compares against an element's <em>immediate</em> text
     * nodes, so a container also holding nested items is not matched by one of
     * its children's labels.
     *
     * @param page the Playwright page
     * @param text the full visible text to match
     * @return a locator for every element with exactly that text
     */
    public static Locator exactText(Page page, String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    /**
     * Find items of the given tag name inside a container by their exact
     * visible text.
     *
     * @param scope       the container to search within
     * @param itemTagName the item's web component tag name
     * @param label       the item's full label
     * @return a locator for the matching item(s); strict mode fails on duplicates
     */
    public static Locator byExactText(Locator scope, String itemTagName, String label) {
        return scope.locator(itemTagName)
                .filter(new Locator.FilterOptions().setHas(exactText(scope.page(), label)));
    }

    /**
     * Find items of the given tag name inside a container by the item's
     * <em>own</em> exact visible text.
     * <p>
     * Unlike {@link #byExactText(Locator, String, String)} this matches only the
     * text nodes the item holds directly, ignoring the text of any nested items.
     * That is what containers whose items can contain other items of the same
     * kind — {@code vaadin-side-nav-item}, for instance — need: matching the whole
     * subtree would make a parent read as {@code "Parent Child 1 Child 2"} and
     * never match its own label. The flip side is that an item whose label is
     * wrapped in an element rather than sitting in a text node is not matched, so
     * prefer {@link #byExactText(Locator, String, String)} for flat lists of
     * items that may render arbitrary content.
     *
     * @param scope       the container to search within
     * @param itemTagName the item's web component tag name
     * @param label       the item's own full label
     * @return a locator for the matching item(s); strict mode fails on duplicates
     */
    public static Locator byOwnExactText(Locator scope, String itemTagName, String label) {
        return scope.locator(itemTagName + ":text-is(" + cssString(label) + ")");
    }

    private static String cssString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /**
     * Find items inside a container by their exact accessible name and ARIA role.
     *
     * @param scope the container to search within
     * @param role  the ARIA role of the item
     * @param name  the item's full accessible name
     * @return a locator for the matching item(s); strict mode fails on duplicates
     */
    public static Locator byExactName(Locator scope, AriaRole role, String name) {
        return scope.getByRole(role,
                new Locator.GetByRoleOptions().setName(name).setExact(true));
    }
}
