package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.NumberUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(ProgressBarElement.FIELD_TAG_NAME)
public class ProgressBarElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-progress-bar";
    public static final String INDETERMINATE_ATTRIBUTE = "indeterminate";

    public ProgressBarElement(Locator locator) {
        super(locator);
    }

    public double getValue() {
        return Double.parseDouble(getLocator().getAttribute("aria-valuenow"));
    }

    public void setValue(double min) {
        getLocator().evaluate("(el, v) => el.value = v", min);
    }

    public void assertValue(Double expected) {
        if (expected != null) {
            assertThat(getLocator()).hasAttribute("aria-valuenow", NumberUtils.formatDouble(expected));
        } else {
            assertThat(getLocator()).not().hasAttribute("aria-valuenow", "undefined");
        }
    }

    public Double getMin() {
        String v = getLocator().getAttribute("aria-valuemin");
        return v == null ? null : Double.valueOf(v);
    }

    public void setMin(double min) {
        getLocator().evaluate("(el, v) => el.min = v", min);
    }

    public void assertMin(double min) {
        assertThat(getLocator()).hasAttribute("aria-valuemin", NumberUtils.formatDouble(min));
    }

    public Double getMax() {
        String v = getLocator().getAttribute("aria-valuemax");
        return v == null ? null : Double.valueOf(v);
    }

    public void setMax(double max) {
        getLocator().evaluate("(el, v) => el.max = v", max);
    }

    public void assertMax(double max) {
        assertThat(getLocator()).hasAttribute("aria-valuemax", NumberUtils.formatDouble(max));
    }

    public boolean isIndeterminate() {
        return getLocator().getAttribute(INDETERMINATE_ATTRIBUTE) != null;
    }

    public void assertIndeterminate() {
        assertThat(getLocator()).hasAttribute(INDETERMINATE_ATTRIBUTE, "");
    }

    public void assertNotIndeterminate() {
        assertThat(getLocator()).not().hasAttribute(INDETERMINATE_ATTRIBUTE, "");
    }

    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }
}
