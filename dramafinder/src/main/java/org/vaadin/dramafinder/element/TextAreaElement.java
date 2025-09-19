package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

@PlaywrightElement(TextAreaElement.FIELD_TAG_NAME)
public class TextAreaElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-text-area";

    public TextAreaElement(Locator locator) {
        super(locator);
    }

    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"textarea\"]").first(); // slot="helper"
    }

    public static TextAreaElement getByLabel(Page page, String label) {
        return new TextAreaElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }
}
