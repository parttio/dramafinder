package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.AssertionWaits.assertEventually;

/**
 * PlaywrightElement for {@code <vaadin-confirm-dialog>}.
 * <p>
 * Reads the header and message, and answers the dialog through its confirm,
 * reject and cancel buttons. The host carries the {@code alertdialog} ARIA role
 * and takes the header as its accessible name, which is what the factory
 * methods match on.
 * <p>
 * A Flow {@code ConfirmDialog} is only attached to the page while it is open:
 * once answered or closed with Escape, the {@code <vaadin-confirm-dialog>} is
 * removed. {@link #assertClosed()} therefore passes both when the dialog is
 * closed and when it is gone, and a dialog that is opened again is a new
 * element, so look it up again rather than reusing the old wrapper.
 */
@PlaywrightElement(ConfirmDialogElement.FIELD_TAG_NAME)
public class ConfirmDialogElement extends VaadinElement
        implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-confirm-dialog";

    private static final String OVERLAY_TAG_NAME = "vaadin-confirm-dialog-overlay";

    /**
     * Create a {@code ConfirmDialogElement} from an existing locator.
     *
     * @param locator the locator for the {@code <vaadin-confirm-dialog>} element
     */
    public ConfirmDialogElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the open confirm dialog, found by its {@code alertdialog} role.
     * <p>
     * Only one confirm dialog is normally open at a time; when several are,
     * any action on the returned element fails with a Playwright strict-mode
     * error, and {@link #getByHeaderText(Page, String)} picks one.
     *
     * @param page the Playwright page
     * @return the open {@code ConfirmDialogElement}
     */
    public static ConfirmDialogElement get(Page page) {
        return new ConfirmDialogElement(
                page.getByRole(AriaRole.ALERTDIALOG).and(page.locator(FIELD_TAG_NAME)));
    }

    /**
     * Get an open confirm dialog by its header, which is the dialog's
     * accessible name ({@code alertdialog} role).
     * <p>
     * The header is matched case-insensitively as a substring. The locator is
     * not narrowed with {@code first()}, so two open dialogs whose headers both
     * match fail with a strict-mode error instead of picking one silently.
     *
     * @param page   the Playwright page
     * @param header the header text, or part of it
     * @return the matching {@code ConfirmDialogElement}
     */
    public static ConfirmDialogElement getByHeaderText(Page page, String header) {
        return new ConfirmDialogElement(
                page.getByRole(AriaRole.ALERTDIALOG, new Page.GetByRoleOptions().setName(header))
                        .and(page.locator(FIELD_TAG_NAME)));
    }

    // ── Open state ─────────────────────────────────────────────────────

    /**
     * Locator for the overlay rendered in the dialog's shadow DOM.
     * <p>
     * The {@code <vaadin-confirm-dialog>} host has a zero-size box, so
     * Playwright never considers it visible; the overlay is what is shown.
     *
     * @return the overlay locator
     */
    public Locator getOverlayLocator() {
        return getLocator().locator(OVERLAY_TAG_NAME);
    }

    /**
     * Whether the dialog is open.
     *
     * @return {@code true} if the dialog overlay is visible
     */
    public boolean isOpen() {
        return getOverlayLocator().isVisible();
    }

    /** Assert that the dialog is open. */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /**
     * Assert that the dialog is closed. Also passes when the dialog has been
     * removed from the page, which is what Flow does once it is answered.
     */
    public void assertClosed() {
        assertThat(getOverlayLocator()).isHidden();
    }

    /**
     * Whether the dialog overlay is visible.
     *
     * @return {@code true} if the overlay is visible
     */
    @Override
    public boolean isVisible() {
        return getOverlayLocator().isVisible();
    }

    /** Assert that the dialog overlay is visible. */
    @Override
    public void assertVisible() {
        assertThat(getOverlayLocator()).isVisible();
    }

    /** Assert that the dialog overlay is hidden, or that the dialog is gone. */
    @Override
    public void assertHidden() {
        assertThat(getOverlayLocator()).isHidden();
    }

    // ── Header and message ─────────────────────────────────────────────

    /**
     * Locator for the header, the element in the {@code header} slot.
     *
     * @return the header locator
     */
    public Locator getHeaderLocator() {
        return getLocator().locator("> [slot='header']");
    }

    /**
     * Get the header text.
     *
     * @return the header text
     */
    public String getHeaderText() {
        return getHeaderLocator().textContent();
    }

    /**
     * Assert the header text.
     *
     * @param header the expected header text
     */
    public void assertHeaderText(String header) {
        assertThat(getHeaderLocator()).hasText(header);
    }

    /**
     * Locator for the message: the dialog's children that are not in a named
     * slot. A message set as a component (e.g. a paragraph with a nested
     * span) is matched as a whole.
     *
     * @return the message locator
     */
    public Locator getMessageLocator() {
        return getLocator().locator("> :not([slot])");
    }

    /**
     * Get the message text: the text of the dialog's unslotted children,
     * with whitespace collapsed. Header and button texts are not included.
     *
     * @return the message text, or an empty string when there is none
     */
    public String getMessage() {
        return (String) getLocator().evaluate("el => Array.from(el.childNodes)"
                + ".filter(n => n.nodeType === Node.TEXT_NODE"
                + " || (n.nodeType === Node.ELEMENT_NODE && !n.hasAttribute('slot')))"
                + ".map(n => n.textContent).join(' ').replace(/\\s+/g, ' ').trim()");
    }

    /**
     * Assert the message text, compared with {@link #getMessage()} after
     * collapsing whitespace. Retries until the message matches or the timeout
     * expires.
     *
     * @param message the expected message, or {@code null} to assert there is
     *                no message
     */
    public void assertMessage(String message) {
        String expected = message == null ? "" : message.replaceAll("\\s+", " ").trim();
        assertEventually(getLocator().page(), "Confirm dialog message", this::getMessage, expected);
    }

    // ── Buttons ────────────────────────────────────────────────────────

    /**
     * The confirm button.
     *
     * @return the button in the {@code confirm-button} slot
     */
    public ButtonElement getConfirmButton() {
        return new ButtonElement(getLocator().locator("> [slot='confirm-button']"));
    }

    /**
     * The cancel button. It is hidden unless the dialog is cancelable.
     *
     * @return the button in the {@code cancel-button} slot
     */
    public ButtonElement getCancelButton() {
        return new ButtonElement(getLocator().locator("> [slot='cancel-button']"));
    }

    /**
     * The reject button. It is hidden unless the dialog is rejectable.
     *
     * @return the button in the {@code reject-button} slot
     */
    public ButtonElement getRejectButton() {
        return new ButtonElement(getLocator().locator("> [slot='reject-button']"));
    }

    /** Click the confirm button. */
    public void confirm() {
        getConfirmButton().click();
    }

    /** Click the cancel button. Fails if the dialog is not cancelable. */
    public void cancel() {
        getCancelButton().click();
    }

    /** Click the reject button. Fails if the dialog is not rejectable. */
    public void reject() {
        getRejectButton().click();
    }

    /**
     * Close the dialog with the Escape key, which fires the dialog's cancel
     * event. Does nothing when the dialog disables closing on Escape.
     */
    public void closeWithEscape() {
        getLocator().press("Escape");
    }

    /**
     * Assert the confirm button's text.
     *
     * @param text the expected text
     */
    public void assertConfirmText(String text) {
        assertThat(getConfirmButton().getLocator()).hasText(text);
    }

    /**
     * Assert the cancel button's text.
     *
     * @param text the expected text
     */
    public void assertCancelText(String text) {
        assertThat(getCancelButton().getLocator()).hasText(text);
    }

    /**
     * Assert the reject button's text.
     *
     * @param text the expected text
     */
    public void assertRejectText(String text) {
        assertThat(getRejectButton().getLocator()).hasText(text);
    }

    /** Assert that the cancel button is shown. */
    public void assertCancelable() {
        getCancelButton().assertVisible();
    }

    /** Assert that the cancel button is not shown. */
    public void assertNotCancelable() {
        getCancelButton().assertHidden();
    }

    /** Assert that the reject button is shown. */
    public void assertRejectable() {
        getRejectButton().assertVisible();
    }

    /** Assert that the reject button is not shown. */
    public void assertNotRejectable() {
        getRejectButton().assertHidden();
    }
}
