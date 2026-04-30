package org.vaadin.addons.dramafinder.element.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Utility for locating Vaadin field elements by their accessible name.
 * <p>
 * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
 * placeholder text (used as a fallback when no label is present).
 */
public final class AccessibleNameLocator {

    private AccessibleNameLocator() {
    }

    /**
     * Find a field by its accessible name, searching the entire page.
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

    private static Locator filter(Locator fields, Page page, AriaRole role, String label) {
        return fields
                .filter(new Locator.FilterOptions()
                        .setHas(page.getByRole(role, new Page.GetByRoleOptions().setName(label))
                                .or(page.getByPlaceholder(label))))
                .first();
    }
}
