package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public abstract class AbstractNumberFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement,
        HasPlaceholderElement, HasAllowedCharPatternElement, HasThemeElement,
        FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement {

    public AbstractNumberFieldElement(Locator locator) {
        super(locator);
    }


    public boolean getHasControls() {
        String v = getLocator().getAttribute("step-buttons-visible");
        return Boolean.parseBoolean(v);
    }

    public void assertHasControls(boolean hasControls) {
        if (hasControls) {
            assertThat(getLocator()).hasAttribute("step-buttons-visible", "");
        } else {
            assertThat(getLocator()).not().hasAttribute("step-buttons-visible", "");
        }
    }

    public void clickIncreaseButton() {
        getLocator().locator("[part='increase-button']").click();
    }

    public void clickDecreaseButton() {
        getLocator().locator("[part='decrease-button']").click();
    }

}
