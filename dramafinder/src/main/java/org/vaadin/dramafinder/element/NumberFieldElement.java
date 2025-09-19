package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(NumberFieldElement.FIELD_TAG_NAME)
public class NumberFieldElement extends AbstractNumberFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-number-field";

    public NumberFieldElement(Locator locator) {
        super(locator);
    }

    public Double getStep() {
        String v = getInputLocator().getAttribute("step");
        return v == null ? null : Double.valueOf(v);
    }

    public void setStep(double step) {
        getInputLocator().evaluate("(el, v) => el.step = v", step);
    }

    public void assertStep(Double step) {
        if (step != null) {
            assertThat(getInputLocator()).hasAttribute("step", step + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("step", "any");
        }
    }

    public Double getMin() {
        String v = getInputLocator().getAttribute("min");
        return v == null ? null : Double.valueOf(v);
    }

    public void setMin(double min) {
        getInputLocator().evaluate("(el, v) => el.min = v", min);
    }

    public void assertMin(Double min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute("min", min + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("min", "undefined");
        }
    }

    public Double getMax() {
        String v = getInputLocator().getAttribute("max");
        return v == null ? null : Double.valueOf(v);
    }

    public void setMax(double max) {
        getInputLocator().evaluate("(el, v) => el.max = v", max);
    }

    public void assertMax(Double max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute("max", max + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("max", "undefined");
        }
    }

    public static NumberFieldElement getByLabel(Page page, String label) {
        return new NumberFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.SPINBUTTON,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static NumberFieldElement getByLabel(Locator locator, String label) {
        return new NumberFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
