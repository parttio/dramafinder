package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasCheckedElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

/**
 * PlaywrightElement for {@code <vaadin-radio-button>} (package-private).
 * <p>
 * The checked-state API comes from {@link HasCheckedElement}.
 */
@PlaywrightElement(RadioButtonElement.FIELD_TAG_NAME)
class RadioButtonElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasCheckedElement, HasEnabledElement,
        HasHelperElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-radio-button";

    /**
     * Create a new {@code RadioButtonElement}.
     */
    public RadioButtonElement(Locator locator) {
        super(locator);
    }

    /** Get a radio by its label within a given scope. */
    static RadioButtonElement getByLabel(Locator locator, String label) {
        return new RadioButtonElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(locator.page().getByRole(AriaRole.RADIO,
                                        new Page.GetByRoleOptions().setName(label)))).first());
    }
}
