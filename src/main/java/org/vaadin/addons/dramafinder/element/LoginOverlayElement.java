package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-login-overlay>}.
 * <p>
 * The modal variant of the login form. The host element carries the
 * {@code dialog} role and the {@code opened} attribute, but its visible content
 * is rendered by the {@code <vaadin-login-overlay-wrapper>} inside its shadow
 * root — like {@link DialogElement}, visibility is therefore resolved through
 * that overlay rather than through the host. On top of the form itself the
 * overlay shows a branding area with an application title and description.
 */
@PlaywrightElement(LoginOverlayElement.FIELD_TAG_NAME)
public class LoginOverlayElement extends AbstractLoginElement implements Findable<ComponentQuery<LoginOverlayElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-login-overlay";

    /** Tag name of the overlay rendered in the component's shadow DOM. */
    public static final String OVERLAY_TAG_NAME = "vaadin-login-overlay-wrapper";

    /** Shadow part holding the application description. */
    protected static final String DESCRIPTION_PART = "description";

    /**
     * Create a {@code LoginOverlayElement} by resolving the overlay with its ARIA role.
     *
     * @param page the Playwright page
     */
    public LoginOverlayElement(Page page) {
        super(page.getByRole(AriaRole.DIALOG)
                .and(page.locator(FIELD_TAG_NAME)));
    }

    /**
     * Creates a new {@code LoginOverlayElement}.
     *
     * @param locator the locator for the {@code <vaadin-login-overlay>} element
     */
    public LoginOverlayElement(Locator locator) {
        super(locator);
    }

    /**
     * Locator for the overlay rendered in the component's shadow DOM.
     * <p>
     * The {@code <vaadin-login-overlay>} host element is only a positioning
     * anchor; the card, the branding area and the form wrapper all live in the
     * {@code <vaadin-login-overlay-wrapper>} inside its shadow root, which
     * Playwright's CSS engine reaches by piercing the open shadow root.
     *
     * @return the locator for the overlay
     */
    public Locator getOverlayLocator() {
        return getLocator().locator(OVERLAY_TAG_NAME);
    }

    /**
     * Whether the overlay is open (visible).
     *
     * @return {@code true} when the overlay is visible
     */
    public boolean isOpen() {
        return getOverlayLocator().isVisible();
    }

    /** Assert that the overlay is open. */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the overlay is closed (its content is no longer visible). */
    public void assertClosed() {
        assertThat(getOverlayLocator()).isHidden();
    }

    /** Whether the overlay (its content) is visible. */
    @Override
    public boolean isVisible() {
        return getOverlayLocator().isVisible();
    }

    /** Assert that the overlay content is visible. */
    @Override
    public void assertVisible() {
        assertThat(getOverlayLocator()).isVisible();
    }

    /** Assert that the overlay content is hidden. */
    @Override
    public void assertHidden() {
        assertThat(getOverlayLocator()).isHidden();
    }

    /**
     * Locator for the application title shown in the branding area.
     * <p>
     * The title is slotted into the overlay from the light DOM, so the lookup
     * uses {@code xpath} to avoid matching the {@code <slot name="title">} in the
     * shadow root. Note that {@link #getTitleLocator()} returns the title of the
     * form itself, which is a different element.
     *
     * @return the locator for the application title
     */
    public Locator getHeaderTitleLocator() {
        return getLocator().locator("xpath=./*[@slot='title']");
    }

    /**
     * Assert that the application title matches the expected text.
     *
     * @param title the expected application title
     */
    public void assertHeaderTitle(String title) {
        assertThat(getHeaderTitleLocator()).hasText(title);
    }

    /**
     * Locator for the application description shown in the branding area.
     *
     * @return the locator for the {@code description} part
     */
    public Locator getDescriptionLocator() {
        return getOverlayLocator().locator("[part='" + DESCRIPTION_PART + "']");
    }

    /**
     * Assert that the application description matches the expected text.
     *
     * @param description the expected application description
     */
    public void assertDescription(String description) {
        assertThat(getDescriptionLocator()).hasText(description);
    }

    // ── Static Factory Methods ─────────────────────────────────────────

    /**
     * Get the first {@code LoginOverlayElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code LoginOverlayElement}
     */
    public static LoginOverlayElement get(Page page) {
        return new LoginOverlayElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get a {@code LoginOverlayElement} by its application title, which is the
     * overlay's accessible name.
     * <p>
     * The title must match in full, case-sensitively.
     *
     * @param page  the Playwright page
     * @param title the full application title
     * @return the matching {@code LoginOverlayElement}
     */
    public static LoginOverlayElement getByTitle(Page page, String title) {
        return new LoginOverlayElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(title).setExact(true))
                        .and(page.locator(FIELD_TAG_NAME)));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code LoginOverlayElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code LoginOverlayElement}
     */
    public static LoginOverlayElement getById(Page page, String id) {
        return new LoginOverlayElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code LoginOverlayElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code LoginOverlayElement}
     */
    public static LoginOverlayElement getById(Locator locator, String id) {
        return new LoginOverlayElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code LoginOverlayElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code LoginOverlayElement}
     */
    public static LoginOverlayElement getByTestId(Page page, String testId) {
        return new LoginOverlayElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code LoginOverlayElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code LoginOverlayElement}
     */
    public static LoginOverlayElement getByTestId(Locator locator, String testId) {
        return new LoginOverlayElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
