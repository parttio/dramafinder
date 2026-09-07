package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasReadOnlyElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
import org.vaadin.addons.dramafinder.element.utils.NumberUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-slider>} (the Flow {@code IntegerSlider}
 * and {@code DecimalSlider} components).
 * <p>
 * The slider renders a native {@code <input type="range">} in its light DOM,
 * which carries the ARIA role {@code slider} together with the {@code value},
 * {@code min}, {@code max} and {@code step} constraints; this element reads and
 * asserts all of them through that input.
 * <p>
 * Values are changed either directly with {@link #setValue(double)} or with the
 * keyboard helpers ({@link #increment()}, {@link #decrement()},
 * {@link #moveToMin()}, {@link #moveToMax()}). Dragging the thumb by mouse is
 * intentionally not exposed: it is pixel-based and flaky, while the keyboard
 * interaction is the same code path a keyboard user takes.
 */
@PlaywrightElement(SliderElement.FIELD_TAG_NAME)
public class SliderElement extends VaadinElement
        implements HasLabelElement, HasHelperElement, HasEnabledElement, HasReadOnlyElement,
        FocusableElement, HasThemeElement, HasStyleElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-slider";

    /**
     * Creates a new {@code SliderElement}.
     *
     * @param locator the locator for the {@code <vaadin-slider>} element
     */
    public SliderElement(Locator locator) {
        super(locator);
    }

    /**
     * Locator for the native range input rendered in the slider's light DOM.
     *
     * @return the locator of the {@code <input type="range">}
     */
    public Locator getInputLocator() {
        return getLocator().locator("input[slot='input']").first();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Focus lives on the native range input, not on the component root.
     */
    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The disabled state is carried by the native range input; the component
     * root only mirrors it with an attribute.
     */
    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    /**
     * Get the current value.
     *
     * @return the value of the slider
     */
    public double getValue() {
        return Double.parseDouble(getInputLocator().inputValue());
    }

    /**
     * Set the value.
     * <p>
     * The value is applied to the native input and committed with {@code input}
     * and {@code change} events, so the component reacts exactly as it does to a
     * user interaction. Values are snapped to {@code step} and clamped to
     * {@code min}/{@code max} by the component itself.
     *
     * @param value the value to apply
     */
    public void setValue(double value) {
        getInputLocator().evaluate("""
                (el, v) => {
                    el.value = v;
                    el.dispatchEvent(new Event('input', { bubbles: true }));
                    el.dispatchEvent(new Event('change', { bubbles: true }));
                }""", value);
        waitForVaadinIdle();
    }

    /**
     * Assert that the value matches the expected one.
     *
     * @param value the expected value
     */
    public void assertValue(double value) {
        assertThat(getInputLocator()).hasValue(NumberUtils.formatDouble(value));
    }

    /**
     * Get the {@code min} constraint.
     *
     * @return the minimum value, or {@code null} if the input exposes none
     */
    public Double getMin() {
        String v = getInputLocator().getAttribute("min");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Assert that the {@code min} constraint matches the expected value.
     *
     * @param min the expected minimum
     */
    public void assertMin(double min) {
        assertThat(getInputLocator()).hasAttribute("min", NumberUtils.formatDouble(min));
    }

    /**
     * Get the {@code max} constraint.
     *
     * @return the maximum value, or {@code null} if the input exposes none
     */
    public Double getMax() {
        String v = getInputLocator().getAttribute("max");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Assert that the {@code max} constraint matches the expected value.
     *
     * @param max the expected maximum
     */
    public void assertMax(double max) {
        assertThat(getInputLocator()).hasAttribute("max", NumberUtils.formatDouble(max));
    }

    /**
     * Get the {@code step} constraint.
     *
     * @return the stepping interval, or {@code null} if the input exposes none
     */
    public Double getStep() {
        String v = getInputLocator().getAttribute("step");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Assert that the {@code step} constraint matches the expected value.
     *
     * @param step the expected stepping interval
     */
    public void assertStep(double step) {
        assertThat(getInputLocator()).hasAttribute("step", NumberUtils.formatDouble(step));
    }

    /**
     * Increase the value by one step, as pressing the right arrow key does.
     */
    public void increment() {
        increment(1);
    }

    /**
     * Increase the value by the given number of steps.
     *
     * @param steps how many times to press the arrow key; must not be negative
     */
    public void increment(int steps) {
        pressKey("ArrowRight", steps);
    }

    /**
     * Decrease the value by one step, as pressing the left arrow key does.
     */
    public void decrement() {
        decrement(1);
    }

    /**
     * Decrease the value by the given number of steps.
     *
     * @param steps how many times to press the arrow key; must not be negative
     */
    public void decrement(int steps) {
        pressKey("ArrowLeft", steps);
    }

    /**
     * Move the value to {@code min}, as pressing the {@code Home} key does.
     */
    public void moveToMin() {
        pressKey("Home", 1);
    }

    /**
     * Move the value to {@code max}, as pressing the {@code End} key does.
     */
    public void moveToMax() {
        pressKey("End", 1);
    }

    private void pressKey(String key, int times) {
        if (times < 0) {
            throw new IllegalArgumentException("The number of key presses cannot be negative: " + times);
        }
        Locator input = getInputLocator();
        for (int i = 0; i < times; i++) {
            input.press(key);
        }
        waitForVaadinIdle();
    }

    /**
     * Get the {@code SliderElement} by its label.
     *
     * <p>Matches the internal range input by ARIA role {@code slider} and accessible name.</p>
     *
     * @param page  the Playwright page
     * @param label the accessible label of the slider
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByLabel(Page page, String label) {
        return new SliderElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.SLIDER, label));
    }

    /**
     * Get the {@code SliderElement} by its label within a given scope.
     *
     * <p>Searches under the provided locator and matches by accessible label.</p>
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the slider
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByLabel(Locator locator, String label) {
        return new SliderElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.SLIDER, label));
    }
}
