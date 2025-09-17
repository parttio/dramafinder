package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

@PlaywrightElement(IntegerFieldElement.FIELD_TAG_NAME)
public class IntegerFieldElement extends AbstractNumberFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-integer-field";

    public IntegerFieldElement(Locator locator) {
        super(locator);
    }

    public static IntegerFieldElement getByLabel(Page page, String label) {
        return new IntegerFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.SPINBUTTON,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static IntegerFieldElement getByLabel(Locator locator, String label) {
        return new IntegerFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
