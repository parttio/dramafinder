package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
import org.vaadin.addons.dramafinder.element.utils.NumberUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-number-field>}.
 * <p>
 * Provides helpers for numeric attributes ({@code min}, {@code max}, {@code step})
 * using {@link Double} types and locator utilities to find the component by
 * its accessible label.
 */
@PlaywrightElement(NumberFieldElement.FIELD_TAG_NAME)
public class NumberFieldElement extends AbstractNumberFieldElement implements Findable<InputFieldQuery<NumberFieldElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-number-field";

    /**
     * Creates a new {@code NumberFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-number-field>} element
     */
    public NumberFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the current {@code step} value.
     *
     * @return the step as a {@link Double}, or {@code null} if not set
     */
    public Double getStep() {
        String v = getInputLocator().getAttribute("step");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code step} value.
     *
     * @param step the step to apply
     */
    public void setStep(double step) {
        getInputLocator().evaluate("(el, v) => el.step = v", step);
    }

    /**
     * Assert that the {@code step} attribute matches the expected value.
     *
     * @param step the expected step, or {@code null} to assert that no explicit step is set
     */
    public void assertStep(Double step) {
        if (step != null) {
            assertThat(getInputLocator()).hasAttribute("step", NumberUtils.formatDouble(step));
        } else {
            assertThat(getInputLocator()).not().hasAttribute("step", "any");
        }
    }

    /**
     * Get the current {@code min} value.
     *
     * @return the minimum as a {@link Double}, or {@code null} if not set
     */
    public Double getMin() {
        String v = getInputLocator().getAttribute("min");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code min} value.
     *
     * @param min the minimum to apply
     */
    public void setMin(double min) {
        getInputLocator().evaluate("(el, v) => el.min = v", min);
    }

    /**
     * Assert that the {@code min} attribute matches the expected value.
     *
     * @param min the expected minimum, or {@code null} to assert that no explicit minimum is set
     */
    public void assertMin(Double min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute("min", NumberUtils.formatDouble(min));
        } else {
            assertThat(getInputLocator()).not().hasAttribute("min", "undefined");
        }
    }

    /**
     * Get the current {@code max} value.
     *
     * @return the maximum as a {@link Double}, or {@code null} if not set
     */
    public Double getMax() {
        String v = getInputLocator().getAttribute("max");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code max} value.
     *
     * @param max the maximum to apply
     */
    public void setMax(double max) {
        getInputLocator().evaluate("(el, v) => el.max = v", max);
    }

    /**
     * Assert that the {@code max} attribute matches the expected value.
     *
     * @param max the expected maximum, or {@code null} to assert that no explicit maximum is set
     */
    public void assertMax(Double max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute("max", NumberUtils.formatDouble(max));
        } else {
            assertThat(getInputLocator()).not().hasAttribute("max", "undefined");
        }
    }

    /**
     * Get the {@code NumberFieldElement} by its label.
     *
     * <p>Matches the internal input by ARIA role {@code spinbutton} and accessible name.</p>
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabel(Page page, String label) {
        return new NumberFieldElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.SPINBUTTON, label));
    }

    /**
     * Get the {@code NumberFieldElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabelContaining(Page page, String text) {
        return new NumberFieldElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.SPINBUTTON, text));
    }

    /**
     * Get the {@code NumberFieldElement} by its label within a given scope.
     *
     * <p>Searches under the provided locator and matches by accessible label.</p>
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabel(Locator locator, String label) {
        return new NumberFieldElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.SPINBUTTON, label));
    }

    /**
     * Get the {@code NumberFieldElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabelContaining(Locator locator, String text) {
        return new NumberFieldElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.SPINBUTTON, text));
    }
}
