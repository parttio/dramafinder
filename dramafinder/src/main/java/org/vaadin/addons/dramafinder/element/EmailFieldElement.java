package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

@PlaywrightElement(EmailFieldElement.FIELD_TAG_NAME)
public class EmailFieldElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-email-field";

    public EmailFieldElement(Locator locator) {
        super(locator);
    }

    public static EmailFieldElement getByLabel(Page page, String label) {
        return new EmailFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static EmailFieldElement getByLabel(Locator locator, String label) {
        return new EmailFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
