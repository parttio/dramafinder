package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Base abstraction for Vaadin number-like fields.
 * <p>
 * Provides shared behavior for numeric inputs, including access to step
 * controls (increase/decrease buttons) and common mixins for validation,
 * input handling, theming, accessibility, and focus.
 */
public abstract class AbstractNumberFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement,
        HasPlaceholderElement, HasAllowedCharPatternElement, HasThemeElement,
        FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement {

    /**
     * Creates a new {@code AbstractNumberFieldElement}.
     *
     * @param locator the locator pointing at the component root element
     */
    public AbstractNumberFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Whether the step controls (increase/decrease buttons) are visible.
     *
     * @return {@code true} if the attribute is present, otherwise {@code false}
     */
    public boolean getHasControls() {
        String v = getLocator().getAttribute("step-buttons-visible");
        return Boolean.parseBoolean(v);
    }

    /**
     * Assert the visibility of step controls.
     *
     * @param hasControls expected presence of the controls
     */
    public void assertHasControls(boolean hasControls) {
        if (hasControls) {
            assertThat(getLocator()).hasAttribute("step-buttons-visible", "");
        } else {
            assertThat(getLocator()).not().hasAttribute("step-buttons-visible", "");
        }
    }

    /**
     * Click the increase button.
     */
    public void clickIncreaseButton() {
        getLocator().locator("[part='increase-button']").click();
    }

    /**
     * Click the decrease button.
     */
    public void clickDecreaseButton() {
        getLocator().locator("[part='decrease-button']").click();
    }

}
