package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-dialog>}.
 * <p>
 * Provides access to header/content/footer slots, modal flags and open state.
 */
public class DialogElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-dialog";

    /**
     * Create a {@code DialogElement} by resolving the dialog with ARIA role.
     *
     * @param page the Playwright page
     */
    public DialogElement(Page page) {
        super(
                page.getByRole(AriaRole.DIALOG)
                        .and(page.locator(FIELD_TAG_NAME)));
    }

    /**
     * Create a {@code DialogElement} from an existing locator.
     *
     * @param locator the locator for the dialog element
     */
    public DialogElement(Locator locator) {
        super(locator);
    }

    /**
     * Close the dialog using the Escape key.
     */
    public void closeWithEscape() {
        getLocator().press("Escape");
    }

    /**
     * Whether the dialog is open (visible).
     *
     * @return {@code true} if the dialog is visible
     */
    public boolean isOpen() {
        return getLocator().isVisible();
    }

    /** Assert that the dialog is open. */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /**
     * Whether the dialog is modal (i.e. not modeless).
     *
     * @return {@code true} if the dialog is modal
     */
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

    /** Assert that the dialog is closed (hidden). */
    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    /**
     * Get the header text from the title slot.
     *
     * @return the header text content
     */
    public String getHeaderText() {
        return getLocator().locator("> [slot='title']").textContent();
    }

    /**
     * Assert the header text matches.
     *
     * @param headerText the expected header text
     */
    public void assertHeaderText(String headerText) {
        assertThat(getLocator().locator("> [slot='title']")).hasText(headerText);
    }

    /**
     * Locator for the header content slot.
     *
     * @return the header content locator
     */
    public Locator getHeaderLocator() {
        return getLocator().locator("> [slot='header-content']");
    }

    /**
     * Locator for the dialog content (first non-slotted child).
     *
     * @return the content locator
     */
    public Locator getContentLocator() {
        // using xpath to not pierce the shadow dom
        return getLocator().locator("xpath=./*[not(@slot)][1]");
    }

    /**
     * Locator for the footer slot.
     *
     * @return the footer locator
     */
    public Locator getFooterLocator() {
        return getLocator().locator("> [slot='footer']");
    }

    /**
     * Get a dialog by its header text (accessible name).
     *
     * @param page    the Playwright page
     * @param summary the accessible name of the dialog
     * @return the matching {@code DialogElement}
     */
    public static DialogElement getByHeaderText(Page page, String summary) {
        return new DialogElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(summary))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

}
