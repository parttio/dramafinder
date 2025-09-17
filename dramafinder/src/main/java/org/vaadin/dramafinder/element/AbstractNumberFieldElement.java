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

    public Integer getStep() {
        String v = getInputLocator().getAttribute("step");
        return v == null ? null : Integer.valueOf(v);
    }

    public void setStep(int step) {
        getInputLocator().evaluate("(el, v) => el.step = v", step);
    }

    public void assertStep(Integer step) {
        if (step != null) {
            assertThat(getInputLocator()).hasAttribute("step", step + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("step", "");
        }
    }

    public Integer getMin() {
        String v = getInputLocator().getAttribute("min");
        return v == null ? null : Integer.valueOf(v);
    }

    public void setMin(int min) {
        getInputLocator().evaluate("(el, v) => el.min = v", min);
    }

    public void assertMin(Integer min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute("min", min + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("min", "");
        }
    }

    public Integer getMax() {
        String v = getInputLocator().getAttribute("max");
        return v == null ? null : Integer.valueOf(v);
    }

    public void setMax(int max) {
        getInputLocator().evaluate("(el, v) => el.max = v", max);
    }

    public void assertMax(Integer max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute("max", max + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("max", "");
        }
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
