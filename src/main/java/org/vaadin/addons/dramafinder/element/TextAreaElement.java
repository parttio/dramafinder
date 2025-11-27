package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * PlaywrightElement for {@code <vaadin-text-area>}.
 * <p>
 * Extends {@link TextFieldElement} with a textarea input slot and label-based lookup.
 */
@PlaywrightElement(TextAreaElement.FIELD_TAG_NAME)
public class TextAreaElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-text-area";

    /**
     * Create a new {@code TextAreaElement}.
     *
     * @param locator the locator for the {@code <vaadin-text-area>} element
     */
    public TextAreaElement(Locator locator) {
        super(locator);
    }

    /**
     * {@inheritDoc}
     */
    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"textarea\"]").first(); // slot="helper"
    }

    /**
     * Get the {@code TextAreaElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the text area
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabel(Page page, String label) {
        return new TextAreaElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }
}
