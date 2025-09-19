package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

@PlaywrightElement(PasswordFieldElement.FIELD_TAG_NAME)
public class PasswordFieldElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-password-field";

    public PasswordFieldElement(Locator locator) {
        super(locator);
    }

    public static PasswordFieldElement getByLabel(Page page, String label) {
        return new PasswordFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }
}
