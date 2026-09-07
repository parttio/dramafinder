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
 * PlaywrightElement for {@code <vaadin-switch>}.
 * <p>
 * A switch is functionally equivalent to a checkbox, but is presented as an
 * on/off toggle and has no indeterminate state. The checked-state API comes
 * from {@link HasCheckedElement}; the other mixins cover label, helper,
 * validation and enablement.
 * <p>
 * The underlying {@code com.vaadin.flow.component.checkbox.Switch} component is
 * experimental and requires the {@code com.vaadin.experimental.switchComponent}
 * feature flag to be enabled in the application under test.
 */
@PlaywrightElement(SwitchElement.FIELD_TAG_NAME)
public class SwitchElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasCheckedElement, HasEnabledElement,
        HasHelperElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-switch";

    /**
     * Create a new {@code SwitchElement}.
     *
     * @param locator the locator for the {@code <vaadin-switch>} element
     */
    public SwitchElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a {@code SwitchElement} by its accessible label.
     * <p>
     * The switch is located through its inner input, which uses the
     * {@code switch} ARIA role.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the switch
     * @return the matching {@code SwitchElement}
     */
    public static SwitchElement getByLabel(Page page, String label) {
        return new SwitchElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.SWITCH,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }
}
