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

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-progress-bar";
    /** The HTML attribute name for indeterminate state. */
    public static final String INDETERMINATE_ATTRIBUTE = "indeterminate";

    /**
     * Create a {@code ProgressBarElement} from an existing locator.
     *
     * @param locator the locator for the {@code <vaadin-progress-bar>} element
     */
    public ProgressBarElement(Locator locator) {
        super(locator);
    }

    /**
     * Current numeric value parsed from {@code aria-valuenow}.
     *
     * @return the current progress value
     */
    public double getValue() {
        return Double.parseDouble(getLocator().getAttribute("aria-valuenow"));
    }

    /**
     * Set the progress bar {@code value}.
     *
     * @param min the value to set
     */
    public void setValue(double min) {
        getLocator().evaluate("(el, v) => el.value = v", min);
    }

    /**
     * Assert that the numeric value matches.
     *
     * @param expected the expected value, or {@code null} for undefined
     */
    public void assertValue(Double expected) {
        if (expected != null) {
            assertThat(getLocator()).hasAttribute("aria-valuenow", NumberUtils.formatDouble(expected));
        } else {
            assertThat(getLocator()).not().hasAttribute("aria-valuenow", "undefined");
        }
    }

    /**
     * Get the {@code min} value.
     *
     * @return the minimum value, or {@code null} if not set
     */
    public Double getMin() {
        String v = getLocator().getAttribute("aria-valuemin");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code min} value.
     *
     * @param min the minimum value to set
     */
    public void setMin(double min) {
        getLocator().evaluate("(el, v) => el.min = v", min);
    }

    /**
     * Assert that {@code min} matches the expected value.
     *
     * @param min the expected minimum value
     */
    public void assertMin(double min) {
        assertThat(getLocator()).hasAttribute("aria-valuemin", NumberUtils.formatDouble(min));
    }

    /**
     * Get the {@code max} value.
     *
     * @return the maximum value, or {@code null} if not set
     */
    public Double getMax() {
        String v = getLocator().getAttribute("aria-valuemax");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code max} value.
     *
     * @param max the maximum value to set
     */
    public void setMax(double max) {
        getLocator().evaluate("(el, v) => el.max = v", max);
    }

    /**
     * Assert that {@code max} matches the expected value.
     *
     * @param max the expected maximum value
     */
    public void assertMax(double max) {
        assertThat(getLocator()).hasAttribute("aria-valuemax", NumberUtils.formatDouble(max));
    }

    /**
     * Whether the bar is indeterminate.
     *
     * @return {@code true} if the progress bar is in indeterminate state
     */
    public boolean isIndeterminate() {
        return getLocator().getAttribute(INDETERMINATE_ATTRIBUTE) != null;
    }

    /**
     * Assert indeterminate state.
     */
    public void assertIndeterminate() {
        assertThat(getLocator()).hasAttribute(INDETERMINATE_ATTRIBUTE, "");
    }

    /**
     * Assert not indeterminate.
     */
    public void assertNotIndeterminate() {
        assertThat(getLocator()).not().hasAttribute(INDETERMINATE_ATTRIBUTE, "");
    }

    /**
     * Set the indeterminate state.
     *
     * @param indeterminate {@code true} for indeterminate, {@code false} otherwise
     */
    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }
}
