package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.NumberUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-progress-bar>}.
 * <p>
 * Supports value/min/max setters and assertions and indeterminate state.
 */
@PlaywrightElement(ProgressBarElement.FIELD_TAG_NAME)
public class ProgressBarElement extends VaadinElement implements HasThemeElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-progress-bar";
    public static final String INDETERMINATE_ATTRIBUTE = "indeterminate";

    /** Create a {@code ProgressBarElement} from an existing locator. */
    public ProgressBarElement(Locator locator) {
        super(locator);
    }

    /** Current numeric value parsed from {@code aria-valuenow}. */
    public double getValue() {
        return Double.parseDouble(getLocator().getAttribute("aria-valuenow"));
    }

    /** Set the progress bar {@code value}. */
    public void setValue(double min) {
        getLocator().evaluate("(el, v) => el.value = v", min);
    }

    /** Assert that the numeric value matches. */
    public void assertValue(Double expected) {
        if (expected != null) {
            assertThat(getLocator()).hasAttribute("aria-valuenow", NumberUtils.formatDouble(expected));
        } else {
            assertThat(getLocator()).not().hasAttribute("aria-valuenow", "undefined");
        }
    }

    /** Get the {@code min} value. */
    public Double getMin() {
        String v = getLocator().getAttribute("aria-valuemin");
        return v == null ? null : Double.valueOf(v);
    }

    /** Set the {@code min} value. */
    public void setMin(double min) {
        getLocator().evaluate("(el, v) => el.min = v", min);
    }

    /** Assert that {@code min} matches the expected value. */
    public void assertMin(double min) {
        assertThat(getLocator()).hasAttribute("aria-valuemin", NumberUtils.formatDouble(min));
    }

    /** Get the {@code max} value. */
    public Double getMax() {
        String v = getLocator().getAttribute("aria-valuemax");
        return v == null ? null : Double.valueOf(v);
    }

    /** Set the {@code max} value. */
    public void setMax(double max) {
        getLocator().evaluate("(el, v) => el.max = v", max);
    }

    /** Assert that {@code max} matches the expected value. */
    public void assertMax(double max) {
        assertThat(getLocator()).hasAttribute("aria-valuemax", NumberUtils.formatDouble(max));
    }

    /** Whether the bar is indeterminate. */
    public boolean isIndeterminate() {
        return getLocator().getAttribute(INDETERMINATE_ATTRIBUTE) != null;
    }

    /** Assert indeterminate state. */
    public void assertIndeterminate() {
        assertThat(getLocator()).hasAttribute(INDETERMINATE_ATTRIBUTE, "");
    }

    /** Assert not indeterminate. */
    public void assertNotIndeterminate() {
        assertThat(getLocator()).not().hasAttribute(INDETERMINATE_ATTRIBUTE, "");
    }

    /** Set the indeterminate state. */
    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }
}
