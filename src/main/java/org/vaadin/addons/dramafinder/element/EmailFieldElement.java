package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;

/**
 * PlaywrightElement for {@code <vaadin-email-field>}.
 */
@PlaywrightElement(EmailFieldElement.FIELD_TAG_NAME)
public class EmailFieldElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-email-field";

    /**
     * Create a new {@code EmailFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-email-field>} element
     */
    public EmailFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code EmailFieldElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabel(Page page, String label) {
        return new EmailFieldElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code EmailFieldElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabel(Locator locator, String label) {
        return new EmailFieldElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }
}
