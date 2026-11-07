package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-message-input>}.
 * <p>
 * Wraps the message input component which contains a text area for composing
 * messages and a send button for submitting them.
 */
@PlaywrightElement(MessageInputElement.FIELD_TAG_NAME)
public class MessageInputElement extends VaadinElement
        implements FocusableElement, HasEnabledElement, HasStyleElement,
        HasThemeElement, HasTooltipElement,
        Findable<ComponentQuery<MessageInputElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-message-input";

    /**
     * Create a new {@code MessageInputElement}.
     *
     * @param locator the locator for the {@code <vaadin-message-input>} element
     */
    public MessageInputElement(Locator locator) {
        super(locator);
    }

    // --- Internal locators ---

    /**
     * Locator for the internal {@code <vaadin-text-area>}.
     *
     * @return the text area locator
     */
    public Locator getTextAreaLocator() {
        return getLocator().locator("vaadin-text-area").first();
    }

    /**
     * Locator for the native textarea inside the text area ({@code slot="textarea"}).
     *
     * @return the textarea input locator
     */
    public Locator getTextAreaInputLocator() {
        return getTextAreaLocator().locator("*[slot=\"textarea\"]").first();
    }

    /**
     * Locator for the internal send button ({@code <vaadin-message-input-button>}).
     *
     * @return the send button locator
     */
    public Locator getSendButtonLocator() {
        return getLocator().locator("vaadin-message-input-button").first();
    }

    // --- Value accessors ---

    /**
     * Get the current text area value.
     *
     * @return the current message text
     */
    public String getValue() {
        return getLocator().evaluate("el => el.value").toString();
    }

    /**
     * Set the message text by filling the internal textarea input.
     * Also syncs the value to the {@code vaadin-text-area} component so
     * that the parent {@code vaadin-message-input} properly updates the
     * send button state.
     *
     * @param value the message text to set
     */
    public void setValue(String value) {
        getTextAreaInputLocator().fill(value);
        waitAfterAction();
        getTextAreaLocator().evaluate("(el, v) => { el.value = v; }", value);
    }

    /**
     * Clear the text area.
     */
    public void clear() {
        setValue("");
    }

    /**
     * Assert that the text area input has the expected value.
     *
     * @param value the expected value
     */
    public void assertValue(String value) {
        assertThat(getTextAreaInputLocator()).hasValue(value);
    }

    // --- Actions ---

    /**
     * Click the send button to submit the message.
     */
    public void submit() {
        getSendButtonLocator().click();
        waitAfterAction();
    }

    /**
     * Press Enter on the text area to submit the message.
     */
    public void submitByEnter() {
        getTextAreaInputLocator().press("Enter");
        waitAfterAction();
    }

    /**
     * Set a message value and then click the send button.
     *
     * @param message the message to type and submit
     */
    public void typeAndSubmit(String message) {
        setValue(message);
        submit();
    }

    // --- Send button assertions ---

    /**
     * Assert that the send button is visible.
     */
    public void assertSendButtonVisible() {
        assertThat(getSendButtonLocator()).isVisible();
    }

    /**
     * Assert that the send button is hidden.
     */
    public void assertSendButtonHidden() {
        assertThat(getSendButtonLocator()).isHidden();
    }

    /**
     * Assert that the send button is enabled.
     */
    public void assertSendButtonEnabled() {
        assertThat(getSendButtonLocator()).isEnabled();
    }

    /**
     * Assert that the send button is disabled.
     */
    public void assertSendButtonDisabled() {
        assertThat(getSendButtonLocator()).isDisabled();
    }

    // --- I18n accessors ---

    /**
     * Get the placeholder text on the text area.
     *
     * @return the placeholder text
     */
    public String getMessagePlaceholder() {
        return getTextAreaLocator().getAttribute("placeholder");
    }

    /**
     * Assert that the text area placeholder matches the expected text.
     *
     * @param expected the expected placeholder text
     */
    public void assertMessagePlaceholder(String expected) {
        assertThat(getTextAreaLocator()).hasAttribute("placeholder", expected);
    }

    /**
     * Get the send button text content.
     *
     * @return the send button text
     */
    public String getSendButtonText() {
        return getSendButtonLocator().textContent();
    }

    /**
     * Assert that the send button text matches the expected text.
     *
     * @param expected the expected send button text
     */
    public void assertSendButtonText(String expected) {
        assertThat(getSendButtonLocator()).hasText(expected);
    }

    // --- Factory methods ---

    /**
     * Get the first {@code <vaadin-message-input>} on the page.
     *
     * @param page the Playwright page
     * @return the first {@code MessageInputElement}
     */
    public static MessageInputElement get(Page page) {
        return new MessageInputElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code <vaadin-message-input>} within a locator scope.
     *
     * @param locator the scope to search within
     * @return the first {@code MessageInputElement}
     */
    public static MessageInputElement get(Locator locator) {
        return new MessageInputElement(locator.locator(FIELD_TAG_NAME).first());
    }

    // --- Interface overrides ---

    /** {@inheritDoc} */
    @Override
    public Locator getFocusLocator() {
        return getTextAreaInputLocator();
    }

    /** {@inheritDoc} */
    @Override
    public Locator getEnabledLocator() {
        return getTextAreaInputLocator();
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code MessageInputElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code MessageInputElement}
     */
    public static MessageInputElement getById(Page page, String id) {
        return new MessageInputElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code MessageInputElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code MessageInputElement}
     */
    public static MessageInputElement getById(Locator locator, String id) {
        return new MessageInputElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code MessageInputElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code MessageInputElement}
     */
    public static MessageInputElement getByTestId(Page page, String testId) {
        return new MessageInputElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code MessageInputElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code MessageInputElement}
     */
    public static MessageInputElement getByTestId(Locator locator, String testId) {
        return new MessageInputElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
