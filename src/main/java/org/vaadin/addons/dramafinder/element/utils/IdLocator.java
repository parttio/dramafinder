package org.vaadin.addons.dramafinder.element.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Utility for locating Vaadin components by their {@code id} or test id.
 * <p>
 * Both are fallbacks for components without a user-visible name: prefer a
 * label, text or accessible name when the component has one. The test id is
 * Playwright's test id attribute — {@code data-testid}, which Flow's
 * {@code Component.setTestId(String)} sets — unless changed with
 * {@code Selectors.setTestIdAttribute}. Matches are exact and carry no
 * {@code .first()}: two components sharing the id fail with a strict-mode
 * error.
 */
public final class IdLocator {

    private IdLocator() {
    }

    /**
     * Components with the given tag and exactly the given {@code id}, anywhere on the page.
     *
     * @param page    the Playwright page
     * @param tagName the component's web component tag name
     * @param id      the full {@code id}
     * @return a locator for the matching component(s)
     */
    public static Locator byId(Page page, String tagName, String id) {
        return page.locator(tagName + idSelector(id));
    }

    /**
     * Components with the given tag and exactly the given {@code id}, inside {@code scope}.
     *
     * @param scope   the locator whose subtree is searched
     * @param tagName the component's web component tag name
     * @param id      the full {@code id}
     * @return a locator for the matching component(s)
     */
    public static Locator byId(Locator scope, String tagName, String id) {
        return scope.locator(tagName + idSelector(id));
    }

    /**
     * Components with the given tag and exactly the given test id, anywhere on the page.
     *
     * @param page    the Playwright page
     * @param tagName the component's web component tag name
     * @param testId  the full test id
     * @return a locator for the matching component(s)
     */
    public static Locator byTestId(Page page, String tagName, String testId) {
        return page.locator(tagName).and(page.getByTestId(testId));
    }

    /**
     * Components with the given tag and exactly the given test id, inside {@code scope}.
     *
     * @param scope   the locator whose subtree is searched
     * @param tagName the component's web component tag name
     * @param testId  the full test id
     * @return a locator for the matching component(s)
     */
    public static Locator byTestId(Locator scope, String tagName, String testId) {
        return scope.locator(tagName).and(scope.page().getByTestId(testId));
    }

    /** {@code [id="..."]} with the value escaped; unlike {@code #id} it accepts any id. */
    static String idSelector(String id) {
        return "[id=\"" + id.replace("\\", "\\\\").replace("\"", "\\\"") + "\"]";
    }
}
