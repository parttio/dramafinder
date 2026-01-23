package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * PlaywrightElement for {@code <vaadin-password-field>}.
 * <p>
 * Extends {@link TextFieldElement} with password-specific input masking.
 * Inherits all text field behaviors including validation, clear button, and placeholders.
 */
@PlaywrightElement(PasswordFieldElement.FIELD_TAG_NAME)
public class PasswordFieldElement extends TextFieldElement {

    /** The HTML tag name for this Vaadin component. */
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
     * Get the {@code PasswordFieldElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabel(Page page, String label) {
        return new PasswordFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code PasswordFieldElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabel(Locator locator, String label) {
        return new PasswordFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
