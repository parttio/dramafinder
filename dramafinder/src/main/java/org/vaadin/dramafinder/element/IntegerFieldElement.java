package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(IntegerFieldElement.FIELD_TAG_NAME)
public class IntegerFieldElement extends AbstractNumberFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-integer-field";

    public IntegerFieldElement(Locator locator) {
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

    public static IntegerFieldElement getByLabel(Page page, String label) {
        return new IntegerFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.SPINBUTTON,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static IntegerFieldElement getByLabel(Locator locator, String label) {
        return new IntegerFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
