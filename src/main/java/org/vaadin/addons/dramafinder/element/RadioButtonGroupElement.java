package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-radio-group>}.
 * <p>
 * Provides helpers to select by label/value and assert selected state.
 */
@PlaywrightElement(RadioButtonGroupElement.FIELD_TAG_NAME)
public class RadioButtonGroupElement extends VaadinElement
        implements HasLabelElement, HasEnabledElement, HasHelperElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-radio-group";

    /**
     * Create a new {@code RadioButtonGroupElement}.
     *
     * @param locator the locator for the {@code <vaadin-radio-group>} element
     */
    public RadioButtonGroupElement(Locator locator) {
        super(locator);
    }

    /**
     * Select a radio by its label text.
     */
    public void selectByLabel(String label) {
        getRadioButtonByLabel(label).check();
    }

    /**
     * Select a radio by its value.
     */
    public void selectByValue(String value) {
        getLocator().evaluate("(el, value) => el.value = value", value);
    }

    /**
     * Get a specific radio by its label within the group.
     */
    public RadioButtonElement getRadioButtonByLabel(String label) {
        return RadioButtonElement.getByLabel(getLocator(), label);
    }

    /**
     * Get the group by its accessible label.
     */
    public static RadioButtonGroupElement getByLabel(Page page, String label) {
        return new RadioButtonGroupElement(
                page.locator(FIELD_TAG_NAME).and(
                        page.getByRole(AriaRole.RADIOGROUP,
                                new Page.GetByRoleOptions().setName(label)
                        )).first());
    }

    /**
     * Set the selected value by label.
     */
    public void setValue(String value) {
        RadioButtonElement.getByLabel(getLocator(), value).check();
    }

    /**
     * Assert the selected value by label.
     */
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
