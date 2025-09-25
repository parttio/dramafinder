package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(RadioButtonGroupElement.FIELD_TAG_NAME)
public class RadioButtonGroupElement extends VaadinElement
        implements HasLabelElement, HasEnabledElement, HasHelperElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-radio-group";

    public RadioButtonGroupElement(Locator locator) {
        super(locator);
    }

    public void selectByLabel(String label) {
        getRadioButtonByLabel(label).check();
    }

    public void selectByValue(String value) {
        getLocator().evaluate("(el, value) => el.value = value", value);
    }

    public RadioButtonElement getRadioButtonByLabel(String label) {
        return RadioButtonElement.getByLabel(getLocator(), label);
    }

    public static RadioButtonGroupElement getByLabel(Page page, String label) {
        return new RadioButtonGroupElement(
                page.locator(FIELD_TAG_NAME).and(
                        page.getByRole(AriaRole.RADIOGROUP,
                                new Page.GetByRoleOptions().setName(label)
                        )).first());
    }

    public void setValue(String value) {
        RadioButtonElement.getByLabel(getLocator(), value).check();
    }

    public void assertValue(String value) {
        if (value != null && !value.isEmpty()) {
            assertThat(getLocator().locator("vaadin-radio-button input:checked"))
                    .hasCount(1);
            RadioButtonElement.getByLabel(getLocator(), value).assertChecked();
        } else {
            assertThat(getLocator().locator("vaadin-radio-button input:checked"))
                    .hasCount(0);
        }
    }
}
