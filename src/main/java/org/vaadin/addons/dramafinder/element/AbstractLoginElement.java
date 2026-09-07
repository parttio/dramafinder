package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Base abstraction shared by {@code <vaadin-login-form>} and
 * {@code <vaadin-login-overlay>}.
 * <p>
 * Both components render the very same login form: the username and password
 * fields, the submit button and the forgot password button live in the light DOM
 * of the host element, while the title, the error message and the footer are
 * parts rendered inside the internal {@code <vaadin-login-form-wrapper>}. Every
 * visible string comes from the component's i18n object, so the assertion helpers
 * take the expected text as a parameter instead of hard-coding the English
 * defaults.
 */
public abstract class AbstractLoginElement extends VaadinElement
        implements HasEnabledElement, HasStyleElement, HasThemeElement {

    /** Id of the username field rendered by the login component. */
    public static final String USERNAME_FIELD_ID = "vaadinLoginUsername";

    /** Id of the password field rendered by the login component. */
    public static final String PASSWORD_FIELD_ID = "vaadinLoginPassword";

    /** Tag name of the internal wrapper holding the title, error and footer parts. */
    public static final String FORM_WRAPPER_TAG_NAME = "vaadin-login-form-wrapper";

    /** Shadow part holding the login form title. */
    protected static final String FORM_TITLE_PART = "form-title";

    /** Shadow part holding the error message, hidden while there is no error. */
    protected static final String ERROR_MESSAGE_PART = "error-message";

    /** Shadow part holding the error message title. */
    protected static final String ERROR_MESSAGE_TITLE_PART = "error-message-title";

    /** Shadow part holding the error message description. */
    protected static final String ERROR_MESSAGE_DESCRIPTION_PART = "error-message-description";

    /** Shadow part holding the footer (additional information). */
    protected static final String FOOTER_PART = "footer";

    /**
     * Creates a new {@code AbstractLoginElement}.
     *
     * @param locator the locator pointing at the component root element
     */
    public AbstractLoginElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the username field of the login form.
     *
     * @return the {@code TextFieldElement} for the username
     */
    public TextFieldElement getUsernameField() {
        return new TextFieldElement(getLocator().locator("#" + USERNAME_FIELD_ID));
    }

    /**
     * Get the password field of the login form.
     *
     * @return the {@code PasswordFieldElement} for the password
     */
    public PasswordFieldElement getPasswordField() {
        return new PasswordFieldElement(getLocator().locator("#" + PASSWORD_FIELD_ID));
    }

    /**
     * Get the submit button of the login form.
     * <p>
     * Resolved through the {@code submit} slot rather than its text, because the
     * caption is i18n driven. The {@code xpath} lookup is deliberate: it does not
     * pierce the shadow root, where a {@code <slot name="submit" slot="submit">}
     * would otherwise match as well.
     *
     * @return the {@code ButtonElement} submitting the form
     */
    public ButtonElement getSubmitButton() {
        return new ButtonElement(
                getLocator().locator("xpath=./" + ButtonElement.FIELD_TAG_NAME + "[@slot='submit']"));
    }

    /**
     * Get the forgot password button of the login form.
     * <p>
     * The button is present but hidden when the component sets
     * {@code no-forgot-password}.
     *
     * @return the {@code ButtonElement} for the forgot password action
     */
    public ButtonElement getForgotPasswordButton() {
        return new ButtonElement(
                getLocator().locator("xpath=./" + ButtonElement.FIELD_TAG_NAME + "[@slot='forgot-password']"));
    }

    /**
     * Fill in the credentials and submit the form.
     *
     * @param username the username to type into the username field
     * @param password the password to type into the password field
     */
    public void login(String username, String password) {
        getUsernameField().setValue(username);
        getPasswordField().setValue(password);
        getSubmitButton().click();
    }

    /**
     * Locator for the login form title.
     *
     * @return the locator for the {@code form-title} part
     */
    public Locator getTitleLocator() {
        return getPartLocator(FORM_TITLE_PART);
    }

    /**
     * Assert that the login form title matches the expected text.
     *
     * @param title the expected title
     */
    public void assertTitle(String title) {
        assertThat(getTitleLocator()).hasText(title);
    }

    /**
     * Locator for the error message container.
     * <p>
     * The container is always in the DOM and is hidden while the component's
     * {@code error} property is {@code false}.
     *
     * @return the locator for the {@code error-message} part
     */
    public Locator getErrorLocator() {
        return getPartLocator(ERROR_MESSAGE_PART);
    }

    /**
     * Locator for the error message title.
     *
     * @return the locator for the {@code error-message-title} part
     */
    public Locator getErrorTitleLocator() {
        return getPartLocator(ERROR_MESSAGE_TITLE_PART);
    }

    /**
     * Locator for the error message description.
     *
     * @return the locator for the {@code error-message-description} part
     */
    public Locator getErrorMessageLocator() {
        return getPartLocator(ERROR_MESSAGE_DESCRIPTION_PART);
    }

    /**
     * Whether the error message is currently shown.
     *
     * @return {@code true} when the error message is visible
     */
    public boolean isErrorVisible() {
        return getErrorLocator().isVisible();
    }

    /** Assert that the error message is shown. */
    public void assertErrorVisible() {
        assertThat(getErrorLocator()).isVisible();
    }

    /** Assert that no error message is shown. */
    public void assertNoError() {
        assertThat(getErrorLocator()).isHidden();
    }

    /**
     * Assert that the error message title matches the expected text.
     *
     * @param title the expected error title
     */
    public void assertErrorTitle(String title) {
        assertThat(getErrorTitleLocator()).hasText(title);
    }

    /**
     * Assert that the error message description matches the expected text.
     *
     * @param message the expected error message
     */
    public void assertErrorMessage(String message) {
        assertThat(getErrorMessageLocator()).hasText(message);
    }

    /**
     * Locator for the footer, holding the additional information text.
     *
     * @return the locator for the {@code footer} part
     */
    public Locator getFooterLocator() {
        return getPartLocator(FOOTER_PART);
    }

    /**
     * Assert that the footer contains the expected additional information.
     *
     * @param additionalInformation the expected footer text
     */
    public void assertAdditionalInformation(String additionalInformation) {
        assertThat(getFooterLocator()).hasText(additionalInformation);
    }

    /**
     * Whether the login form is disabled.
     * <p>
     * The component disables itself while a login is in flight and re-enables
     * itself once an error is reported.
     *
     * @return {@code true} when the {@code disabled} property is set
     */
    public boolean isDisabled() {
        return Boolean.TRUE.equals(getProperty("disabled"));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The {@code disabled} property is not reflected to an attribute on the host,
     * so the enabled state is asserted on the submit button, which mirrors it.
     */
    @Override
    public Locator getEnabledLocator() {
        return getSubmitButton().getLocator();
    }

    /**
     * Locator for the internal {@code <vaadin-login-form-wrapper>} rendering the
     * title, the error message and the footer.
     *
     * @return the locator for the form wrapper
     */
    protected Locator getFormWrapperLocator() {
        return getLocator().locator(FORM_WRAPPER_TAG_NAME);
    }

    /**
     * Locator for a shadow part of the form wrapper.
     * <p>
     * The lookup is scoped to the wrapper on purpose: the username and password
     * fields are slotted into it from the light DOM and expose parts of their own
     * (an {@code error-message} among them), but they are not DOM descendants of
     * the wrapper, so scoping keeps the lookup unambiguous.
     *
     * @param part the part name
     * @return the locator for the given part
     */
    protected Locator getPartLocator(String part) {
        return getFormWrapperLocator().locator("[part='" + part + "']");
    }
}
