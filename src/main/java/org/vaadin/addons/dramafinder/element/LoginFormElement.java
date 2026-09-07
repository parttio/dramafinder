package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

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
public class LoginFormElement extends AbstractLoginElement {

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
}
