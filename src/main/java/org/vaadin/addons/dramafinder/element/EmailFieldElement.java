package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * PlaywrightElement for {@code <vaadin-email-field>}.
 */
@PlaywrightElement(EmailFieldElement.FIELD_TAG_NAME)
public class EmailFieldElement extends TextFieldElement {

    /** The HTML tag name for this Vaadin component. */
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
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
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
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
