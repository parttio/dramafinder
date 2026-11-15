package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-login-form>}.
 * <p>
 * The inline login form: username and password fields, a submit button and an
 * optional forgot password button, plus an error message that the application
 * turns on when the credentials are rejected. All strings come from the
 * component's i18n object, so the assertion helpers inherited from
 * {@link AbstractLoginElement} take the expected text as a parameter.
 */
@PlaywrightElement(LoginFormElement.FIELD_TAG_NAME)
public class LoginFormElement extends AbstractLoginElement implements Findable<ComponentQuery<LoginFormElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-login-form";

    /**
     * Creates a new {@code LoginFormElement}.
     *
     * @param locator the locator for the {@code <vaadin-login-form>} element
     */
    public LoginFormElement(Locator locator) {
        super(locator);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code LoginFormElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code LoginFormElement}
     */
    public static LoginFormElement get(Page page) {
        return new LoginFormElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code LoginFormElement} within a scope.
     *
     * @param locator the scope containing the login form (not the form itself)
     * @return the first matching {@code LoginFormElement}
     */
    public static LoginFormElement get(Locator locator) {
        return new LoginFormElement(locator.locator(FIELD_TAG_NAME).first());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code LoginFormElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code LoginFormElement}
     */
    public static LoginFormElement getById(Page page, String id) {
        return new LoginFormElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code LoginFormElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code LoginFormElement}
     */
    public static LoginFormElement getById(Locator locator, String id) {
        return new LoginFormElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code LoginFormElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code LoginFormElement}
     */
    public static LoginFormElement getByTestId(Page page, String testId) {
        return new LoginFormElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code LoginFormElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code LoginFormElement}
     */
    public static LoginFormElement getByTestId(Locator locator, String testId) {
        return new LoginFormElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
