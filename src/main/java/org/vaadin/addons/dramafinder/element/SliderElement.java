package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasReadOnlyElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
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
        FocusableElement, HasThemeElement, HasStyleElement, HasTooltipElement,
        Findable<FieldQuery<SliderElement>> {

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
     * and {@code change} events, the events a drag or a key press fires. Values
     * are snapped to {@code step} and clamped to {@code min}/{@code max} by the
     * component itself.
     * <p>
     * A script write is not gated by the input's state the way a user is, so a
     * disabled or read-only slider is refused up front rather than left showing a
     * value the server never accepted.
     *
     * @param value the value to apply
     * @throws IllegalStateException if the slider is disabled or read-only
     */
    public void setValue(double value) {
        if (!isEnabled()) {
            throw new IllegalStateException("Cannot set the value of a disabled slider");
        }
        if (isReadOnly()) {
            throw new IllegalStateException("Cannot set the value of a read-only slider");
        }
        getInputLocator().evaluate("""
                (el, v) => {
                    el.value = v;
                    el.dispatchEvent(new Event('input', { bubbles: true }));
                    el.dispatchEvent(new Event('change', { bubbles: true }));
                }""", value);
        waitAfterAction();
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
            waitAfterAction();
        }
        waitAfterAction();
    }

    /**
     * Get the {@code SliderElement} by its label.
     *
     * <p>Matches the internal range input by ARIA role {@code slider} and accessible name.</p>
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
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
     * Get the {@code SliderElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByLabelContaining(Page page, String text) {
        return new SliderElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.SLIDER, text));
    }

    /**
     * Get the {@code SliderElement} by its label within a given scope.
     *
     * <p>Searches under the provided locator and matches by accessible label.</p>
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the slider
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByLabel(Locator locator, String label) {
        return new SliderElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.SLIDER, label));
    }

    /**
     * Get the {@code SliderElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByLabelContaining(Locator locator, String text) {
        return new SliderElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.SLIDER, text));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code SliderElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getById(Page page, String id) {
        return new SliderElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SliderElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getById(Locator locator, String id) {
        return new SliderElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SliderElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByTestId(Page page, String testId) {
        return new SliderElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code SliderElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code SliderElement}
     */
    public static SliderElement getByTestId(Locator locator, String testId) {
        return new SliderElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
