package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-integer-field>}.
 * <p>
 * Provides typed helpers to read and modify integer-specific attributes
 * such as {@code min}, {@code max}, and {@code step}, and convenient
 * factory methods to locate the element by its accessible label.
 */
@PlaywrightElement(IntegerFieldElement.FIELD_TAG_NAME)
public class IntegerFieldElement extends AbstractNumberFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-integer-field";

    /**
     * Creates a new {@code IntegerFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-integer-field>} element
     */
    public IntegerFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the current {@code step} value.
     *
     * @return the step as an {@link Integer}, or {@code null} if not set
     */
    public Integer getStep() {
        String v = getInputLocator().getAttribute("step");
        return v == null ? null : Integer.valueOf(v);
    }

    /**
     * Set the {@code step} value.
     *
     * @param step the step to apply
     */
    public void setStep(int step) {
        getInputLocator().evaluate("(el, v) => el.step = v", step);
    }

    /**
     * Assert that the {@code step} attribute matches the expected value.
     *
     * @param step the expected step, or {@code null} to assert that no explicit step is set
     */
    public void assertStep(Integer step) {
        if (step != null) {
            assertThat(getInputLocator()).hasAttribute("step", step + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("step", "any");
        }
    }

    /**
     * Get the current {@code min} value.
     *
     * @return the minimum as an {@link Integer}, or {@code null} if not set
     */
    public Integer getMin() {
        String v = getInputLocator().getAttribute("min");
        return v == null ? null : Integer.valueOf(v);
    }

    /**
     * Set the {@code min} value.
     *
     * @param min the minimum to apply
     */
    public void setMin(int min) {
        getInputLocator().evaluate("(el, v) => el.min = v", min);
    }

    /**
     * Assert that the {@code min} attribute matches the expected value.
     *
     * @param min the expected minimum, or {@code null} to assert that no explicit minimum is set
     */
    public void assertMin(Integer min) {
        if (min != null) {
            assertThat(getInputLocator()).hasAttribute("min", min + "");
        } else {
            assertThat(getInputLocator()).not().hasAttribute("min", "undefined");
        }
    }

    /**
     * Get the current {@code max} value.
     *
     * @return the maximum as an {@link Integer}, or {@code null} if not set
     */
    public Integer getMax() {
        String v = getInputLocator().getAttribute("max");
        return v == null ? null : Integer.valueOf(v);
    }

    /**
     * Set the {@code max} value.
     *
     * @param max the maximum to apply
     */
    public void setMax(int max) {
        getInputLocator().evaluate("(el, v) => el.max = v", max);
    }

    /**
     * Assert that the {@code max} attribute matches the expected value.
     *
     * @param max the expected maximum, or {@code null} to assert that no explicit maximum is set
     */
    public void assertMax(Integer max) {
        if (max != null) {
            assertThat(getInputLocator()).hasAttribute("max", max + "");
        } else {
            assertThat(getInputLocator()).hasAttribute("max", "undefined");
        }
    }

    /**
     * Get the {@code IntegerFieldElement} by its label.
     *
     * <p>Matches the internal input by ARIA role {@code spinbutton} and accessible name.</p>
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByLabel(Page page, String label) {
        return new IntegerFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.SPINBUTTON,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code IntegerFieldElement} by its label within a given scope.
     *
     * <p>Searches under the provided locator and matches by accessible label.</p>
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByLabel(Locator locator, String label) {
        return new IntegerFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
