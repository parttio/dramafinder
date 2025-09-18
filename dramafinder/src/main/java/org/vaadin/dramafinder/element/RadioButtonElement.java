package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasHelperElement;
import org.vaadin.dramafinder.element.shared.HasLabelElement;
import org.vaadin.dramafinder.element.shared.HasStyleElement;
import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.dramafinder.element.shared.HasValueElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(RadioButtonElement.FIELD_TAG_NAME)
class RadioButtonElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasEnabledElement,
        HasHelperElement, HasValueElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-radio-button";

    public RadioButtonElement(Locator locator) {
        super(locator);
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    boolean isChecked() {
        return getInputLocator().isChecked();
    }

    void assertChecked() {
        assertThat(getInputLocator()).isChecked();
    }

    void assertNotChecked() {
        assertThat(getInputLocator()).not().isChecked();
    }

    void check() {
        getInputLocator().check();
    }

    static RadioButtonElement getByLabel(Locator locator, String label) {
        return new RadioButtonElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(locator.page().getByRole(AriaRole.RADIO,
                                        new Page.GetByRoleOptions().setName(label)))).first());
    }
}
