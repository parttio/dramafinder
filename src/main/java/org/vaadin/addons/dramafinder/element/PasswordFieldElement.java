package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;

/**
 * PlaywrightElement for {@code <vaadin-password-field>}.
 */
@PlaywrightElement(PasswordFieldElement.FIELD_TAG_NAME)
public class PasswordFieldElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-password-field";

    /**
     * Create a new {@code PasswordFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-password-field>} element
     */
    public PasswordFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code PasswordFieldElement} by its accessible name, searching the entire page.
     * <p>
     * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
     * placeholder text (used as a fallback when no label is present).
     *
     * @param page  the Playwright page
     * @param label the accessible name of the field
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabel(Page page, String label) {
        return new PasswordFieldElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }
}
