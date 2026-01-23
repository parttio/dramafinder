package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.shared.HasValueElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-radio-button>} (package-private).
 */
@PlaywrightElement(RadioButtonElement.FIELD_TAG_NAME)
class RadioButtonElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasEnabledElement,
        HasHelperElement, HasValueElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-radio-button";

    /**
     * Create a new {@code RadioButtonElement}.
     *
     * @param locator the locator for the {@code <vaadin-radio-button>} element
     */
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

    /**
     * Whether the radio is checked.
     *
     * @return {@code true} if the radio is checked
     */
    boolean isChecked() {
        return getInputLocator().isChecked();
    }

    /**
     * Assert that the radio is checked.
     */
    void assertChecked() {
        assertThat(getInputLocator()).isChecked();
    }

    /**
     * Assert that the radio is not checked.
     */
    void assertNotChecked() {
        assertThat(getInputLocator()).not().isChecked();
    }

    /**
     * Check the radio.
     */
    void check() {
        getInputLocator().check();
    }

    /**
     * Get a radio by its label within a given scope.
     *
     * @param locator the scope to search within
     * @param label   the accessible label of the radio button
     * @return the matching {@code RadioButtonElement}
     */
    static RadioButtonElement getByLabel(Locator locator, String label) {
        return new RadioButtonElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(locator.page().getByRole(AriaRole.RADIO,
                                        new Page.GetByRoleOptions().setName(label)))).first());
    }
}
