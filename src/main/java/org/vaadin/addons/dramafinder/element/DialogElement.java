package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-dialog>}.
 * <p>
 * Provides access to header/content/footer slots, modal flags and open state.
 */
@PlaywrightElement(DialogElement.FIELD_TAG_NAME)
public class DialogElement extends VaadinElement implements HasThemeElement, HasStyleElement,
        Findable<ComponentQuery<DialogElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-dialog";

    /**
     * Create a {@code DialogElement} by resolving the dialog with ARIA role.
     */
    public DialogElement(Page page) {
        super(
                page.getByRole(AriaRole.DIALOG)
                        .and(page.locator(FIELD_TAG_NAME)));
    }

    /** Create a {@code DialogElement} from an existing locator. */
    public DialogElement(Locator locator) {
        super(locator);
    }

    /** Close the dialog using the Escape key. */
    public void closeWithEscape() {
        getLocator().press("Escape");
        waitAfterAction();
    }

    /**
     * Locator for the overlay rendered in the dialog's shadow DOM.
     * <p>
     * The {@code <vaadin-dialog>} host element itself has a zero-size bounding
     * box, so Playwright never considers it visible. The actual visible content
     * lives in the {@code <vaadin-dialog-overlay>} inside its shadow root, which
     * Playwright's CSS engine reaches by piercing the open shadow root.
     */
    public Locator getOverlayLocator() {
        return getLocator().locator("vaadin-dialog-overlay");
    }

    /** Whether the dialog is open (visible). */
    public boolean isOpen() {
        return getOverlayLocator().isVisible();
    }

    /** Assert that the dialog is open. */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Whether the dialog (its overlay) is visible. */
    @Override
    public boolean isVisible() {
        return getOverlayLocator().isVisible();
    }

    /** Assert that the dialog overlay is visible. */
    @Override
    public void assertVisible() {
        assertThat(getOverlayLocator()).isVisible();
    }

    /** Assert that the dialog overlay is hidden. */
    @Override
    public void assertHidden() {
        assertThat(getOverlayLocator()).isHidden();
    }

    /** Whether the dialog is modal (i.e. not modeless). */
    public boolean isModal() {
        return getLocator().getAttribute("modeless") == null;
    }

    /** Assert that the dialog is modal. */
    public void assertModal() {
        assertThat(getLocator()).not().hasAttribute("modeless", "");
    }

    /** Assert that the dialog is modeless. */
    public void assertModeless() {
        assertThat(getLocator()).hasAttribute("modeless", "");
    }

    /** Assert that the dialog is closed (its overlay is hidden). */
    public void assertClosed() {
        assertThat(getOverlayLocator()).isHidden();
    }

    /** Get the header text from the title slot. */
    public String getHeaderText() {
        return getLocator().locator("> [slot='title']").textContent();
    }

    /** Assert the header text matches. */
    public void assertHeaderText(String headerText) {
        assertThat(getLocator().locator("> [slot='title']")).hasText(headerText);
    }

    /** Locator for the header content slot. */
    public Locator getHeaderLocator() {
        return getLocator().locator("> [slot='header-content']");
    }

    /** Locator for the dialog content (first non-slotted child). */
    public Locator getContentLocator() {
        // using xpath to not pierce the shadow dom
        return getLocator().locator("xpath=./*[not(@slot)][1]");
    }

    /** Locator for the footer slot. */
    public Locator getFooterLocator() {
        return getLocator().locator("> [slot='footer']");
    }

    /**
     * Get a dialog by its header text, which is its accessible name.
     * <p>
     * The header must match in full, case-sensitively; two open dialogs with that
     * header fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param header the full header text
     * @return the matching {@code DialogElement}
     */
    public static DialogElement getByHeaderText(Page page, String header) {
        return new DialogElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(header).setExact(true))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code DialogElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code DialogElement}
     */
    public static DialogElement getById(Page page, String id) {
        return new DialogElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DialogElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code DialogElement}
     */
    public static DialogElement getById(Locator locator, String id) {
        return new DialogElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DialogElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code DialogElement}
     */
    public static DialogElement getByTestId(Page page, String testId) {
        return new DialogElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code DialogElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code DialogElement}
     */
    public static DialogElement getByTestId(Locator locator, String testId) {
        return new DialogElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
