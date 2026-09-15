package org.vaadin.addons.dramafinder.element.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Utility for locating Vaadin field elements by their accessible name.
 * <p>
 * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
 * placeholder text (used as a fallback when no label is present).
 * <p>
 * {@link #find(Page, String, AriaRole, String) find} keeps Playwright's default
 * matching — case-insensitive and by <em>substring</em> — and returns the first
 * match. That is a deliberate convenience for the page-level
 * {@code getByLabel(Page, String)} field factories, where a caller may want to
 * pass only part of a long label; the price is that {@code "Name"} also matches a
 * field labelled {@code "Nickname"}. Use
 * {@link #findExact(Page, String, AriaRole, String) findExact} when the caller is
 * expected to pass the whole label and an ambiguity should fail rather than pick a
 * winner.
 *
 * @see ItemLocator for item-within-container lookups, which always match exactly
 */
public final class AccessibleNameLocator {

    private AccessibleNameLocator() {
    }

    /**
     * Find a field by its accessible name, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring, and the placeholder
     * is used as a fallback when the field has no label.
     *
     * @param page         the Playwright page
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param label        the accessible name to match
     * @return a locator for the first matching field
     */
    public static Locator find(Page page, String fieldTagName, AriaRole role, String label) {
        return filter(page.locator(fieldTagName), page, role, label);
    }

    /**
     * Find a field by its accessible name, scoped to the given locator's subtree.
     * <p>
     * The name is matched case-insensitively as a substring, and the placeholder
     * is used as a fallback when the field has no label.
     *
     * @param scope        the locator defining the search scope
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param label        the accessible name to match
     * @return a locator for the first matching field within the scope
     */
    public static Locator find(Locator scope, String fieldTagName, AriaRole role, String label) {
        return filter(scope.locator(fieldTagName), scope.page(), role, label);
    }

    /**
     * Find a field by its full accessible name, searching the entire page.
     * <p>
     * Unlike {@link #find(Page, String, AriaRole, String)} the name must match
     * exactly (case-sensitive, whitespace-normalized), there is no placeholder
     * fallback, and no {@code .first()} is applied — so two fields sharing the
     * name fail with a Playwright strict-mode error instead of resolving to
     * whichever comes first in the DOM.
     *
     * @param page         the Playwright page
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param label        the full accessible name to match
     * @return a locator for the matching field(s)
     */
    public static Locator findExact(Page page, String fieldTagName, AriaRole role, String label) {
        return filterExact(page.locator(fieldTagName), page, role, label);
    }

    /**
     * Find a field by its full accessible name, scoped to the given locator's
     * subtree.
     * <p>
     * Unlike {@link #find(Locator, String, AriaRole, String)} the name must match
     * exactly (case-sensitive, whitespace-normalized), there is no placeholder
     * fallback, and no {@code .first()} is applied — so two fields sharing the
     * name fail with a Playwright strict-mode error instead of resolving to
     * whichever comes first in the DOM.
     *
     * @param scope        the locator defining the search scope
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param label        the full accessible name to match
     * @return a locator for the matching field(s) within the scope
     */
    public static Locator findExact(Locator scope, String fieldTagName, AriaRole role, String label) {
        return filterExact(scope.locator(fieldTagName), scope.page(), role, label);
    }

    private static Locator filter(Locator fields, Page page, AriaRole role, String label) {
        return fields
                .filter(new Locator.FilterOptions()
                        .setHas(page.getByRole(role, new Page.GetByRoleOptions().setName(label))
                                .or(page.getByPlaceholder(label))))
                .first();
    }

    private static Locator filterExact(Locator fields, Page page, AriaRole role, String label) {
        return fields
                .filter(new Locator.FilterOptions()
                        .setHas(page.getByRole(role,
                                new Page.GetByRoleOptions().setName(label).setExact(true))));
    }
}
