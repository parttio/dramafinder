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
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the accessible name of the field
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabel(Page page, String label) {
        return new PasswordFieldElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code PasswordFieldElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabelContaining(Page page, String text) {
        return new PasswordFieldElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }
}
