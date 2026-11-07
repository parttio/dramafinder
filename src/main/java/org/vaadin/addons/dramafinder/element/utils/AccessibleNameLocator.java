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
 * {@link #find(Page, String, AriaRole, String) find} matches the <em>whole</em> name,
 * case-sensitively, and applies no {@code .first()}: two fields with the same name fail
 * with a Playwright strict-mode error instead of resolving to whichever comes first in
 * the DOM, and {@code "Name"} never matches a field labelled {@code "Nickname"}.
 * {@link #findContaining(Page, String, AriaRole, String) findContaining} is the explicit
 * partial match — case-insensitive and by substring — for callers that pass only part of
 * a long label; it applies no {@code .first()} either.
 * {@link #findExact(Page, String, AriaRole, String) findExact} is the item lookup: exact,
 * without the placeholder fallback.
 *
 * @see ItemLocator for item-within-container lookups, which always match exactly
 */
public final class AccessibleNameLocator {

    private AccessibleNameLocator() {
    }

    /**
     * Find a field by its full accessible name, searching the entire page.
     * <p>
     * The name must match exactly (case-sensitive, whitespace-normalized); the
     * placeholder, matched the same way, is used as a fallback when the field has no
     * label. Several matches fail with a strict-mode error when the locator is used.
     *
     * @param page         the Playwright page
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param label        the full accessible name or placeholder to match
     * @return a locator for the matching field(s)
     */
    public static Locator find(Page page, String fieldTagName, AriaRole role, String label) {
        return filter(page.locator(fieldTagName), page, role, label, true);
    }

    /**
     * Find a field by its full accessible name, scoped to the given locator's subtree.
     * <p>
     * The name must match exactly (case-sensitive, whitespace-normalized); the
     * placeholder, matched the same way, is used as a fallback when the field has no
     * label. Several matches fail with a strict-mode error when the locator is used.
     *
     * @param scope        the locator defining the search scope
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param label        the full accessible name or placeholder to match
     * @return a locator for the matching field(s) within the scope
     */
    public static Locator find(Locator scope, String fieldTagName, AriaRole role, String label) {
        return filter(scope.locator(fieldTagName), scope.page(), role, label, true);
    }

    /**
     * Find a field whose accessible name contains {@code text}, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring, and the placeholder is used
     * as a fallback when the field has no label. Several matches fail with a strict-mode
     * error when the locator is used.
     *
     * @param page         the Playwright page
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param text         part of the accessible name or placeholder
     * @return a locator for the matching field(s)
     */
    public static Locator findContaining(Page page, String fieldTagName, AriaRole role, String text) {
        return filter(page.locator(fieldTagName), page, role, text, false);
    }

    /**
     * Find a field whose accessible name contains {@code text}, scoped to the given
     * locator's subtree.
     * <p>
     * The name is matched case-insensitively as a substring, and the placeholder is used
     * as a fallback when the field has no label. Several matches fail with a strict-mode
     * error when the locator is used.
     *
     * @param scope        the locator defining the search scope
     * @param fieldTagName the field's web component tag name
     * @param role         the ARIA role used to match the field's accessible name
     * @param text         part of the accessible name or placeholder
     * @return a locator for the matching field(s) within the scope
     */
    public static Locator findContaining(Locator scope, String fieldTagName, AriaRole role, String text) {
        return filter(scope.locator(fieldTagName), scope.page(), role, text, false);
    }

    /**
     * Find a field by its full accessible name, searching the entire page.
     * <p>
     * Like {@link #find(Page, String, AriaRole, String)} but without the placeholder
     * fallback: the lookup used for one item out of a container.
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
     * Like {@link #find(Locator, String, AriaRole, String)} but without the placeholder
     * fallback: the lookup used for one item out of a container.
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

    private static Locator filter(Locator fields, Page page, AriaRole role, String label, boolean exact) {
        return fields
                .filter(new Locator.FilterOptions()
                        .setHas(page.getByRole(role, new Page.GetByRoleOptions().setName(label).setExact(exact))
                                .or(page.getByPlaceholder(label, new Page.GetByPlaceholderOptions().setExact(exact)))));
    }

    private static Locator filterExact(Locator fields, Page page, AriaRole role, String label) {
        return fields
                .filter(new Locator.FilterOptions()
                        .setHas(page.getByRole(role,
                                new Page.GetByRoleOptions().setName(label).setExact(true))));
    }
}
