package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;

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
     * Get the {@code TextAreaElement} by its accessible name, searching the entire page.
     * <p>
     * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
     * placeholder text (used as a fallback when no label is present).
     *
     * @param page  the Playwright page
     * @param label the accessible name of the text area
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabel(Page page, String label) {
        return new TextAreaElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code TextAreaElement} by its accessible name, scoped to the given locator.
     * <p>
     * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
     * placeholder text (used as a fallback when no label is present). Only fields within
     * the subtree of the provided locator are considered.
     *
     * @param locator the locator defining the search scope
     * @param label   the accessible name of the text area
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabel(Locator locator, String label) {
        return new TextAreaElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }
}
