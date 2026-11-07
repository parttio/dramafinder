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
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
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
     * Get the {@code EmailFieldElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabelContaining(Page page, String text) {
        return new EmailFieldElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }

    /**
     * Get the {@code EmailFieldElement} by its label within a given scope.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabel(Locator locator, String label) {
        return new EmailFieldElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code EmailFieldElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabelContaining(Locator locator, String text) {
        return new EmailFieldElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }
}
