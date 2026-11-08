package org.vaadin.addons.dramafinder.element;

import java.math.BigDecimal;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.NumberUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.vaadin.addons.dramafinder.element.AssertionWaits.assertEventually;

/**
 * PlaywrightElement for {@code <vaadin-number-field>}.
 * <p>
 * Provides helpers for numeric attributes ({@code min}, {@code max}, {@code step})
 * using {@link Double} types and locator utilities to find the component by
 * its accessible label. Adds a typed {@link Double} value next to the text value:
 * {@link #setValue(Double)}, {@link #getValueAsDouble()} and {@link #assertValue(Double)}.
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
        waitAfterAction();
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
        waitAfterAction();
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
        waitAfterAction();
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
     * Get the {@code NumberFieldElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, NumberFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, NumberFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabel(Page page, String label) {
        return new NumberFieldElement(ElementQueries.find(page, NumberFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code NumberFieldElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, NumberFieldElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabelContaining(Page page, String text) {
        return new NumberFieldElement(ElementQueries.find(page, NumberFieldElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code NumberFieldElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, NumberFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, NumberFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabel(Locator locator, String label) {
        return new NumberFieldElement(ElementQueries.find(locator, NumberFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code NumberFieldElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, NumberFieldElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByLabelContaining(Locator locator, String text) {
        return new NumberFieldElement(ElementQueries.find(locator, NumberFieldElement.class).withLabelContaining(text).getLocator());
    }

    // ── Typed value ────────────────────────────────────────────────────

    /**
     * Set the value as a number, typed the way a user would type it: a whole number without a
     * fraction ({@code 170}, not {@code 170.0}) and no exponent. See
     * {@link #setValue(String)} for how the value is entered.
     *
     * @param value the value to set; {@code null} clears the field. A literal {@code null} is
     *              ambiguous with {@link #setValue(String)}: write {@code (Double) null}, or call
     *              {@link #clear()}
     * @throws IllegalArgumentException if the value is NaN or infinite
     */
    public void setValue(Double value) {
        if (value == null) {
            clear();
            return;
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Cannot type a non-finite number: " + value);
        }
        setValue(BigDecimal.valueOf(value).stripTrailingZeros().toPlainString());
    }

    /**
     * Get the current value as a number.
     *
     * @return the value, or {@code null} when the field is empty (or holds text the browser does
     *         not accept as a number, which it reports as an empty value)
     */
    public Double getValueAsDouble() {
        String value = getValue();
        return value == null || value.isEmpty() ? null : Double.valueOf(value);
    }

    /**
     * Assert the value as a number, compared numerically: {@code 2.5} matches the text
     * {@code 2.50}, and {@code 0.0} matches {@code -0}. Text that is not a number never matches.
     * Retries until the value matches or the timeout passes.
     *
     * @param expected the expected value; {@code null} asserts the field is empty. A literal
     *                 {@code null} is ambiguous with {@link #assertValue(String)}: write
     *                 {@code (Double) null}
     */
    public void assertValue(Double expected) {
        assertEventually(getLocator().page(), "Number field value", this::getValue, text -> {
            if (text == null || text.isEmpty()) {
                return expected == null;
            }
            try {
                return expected != null && Double.parseDouble(text) == expected;
            } catch (NumberFormatException e) {
                return false;
            }
        }, String.valueOf(expected));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code NumberFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getById(Page page, String id) {
        return new NumberFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code NumberFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getById(Locator locator, String id) {
        return new NumberFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code NumberFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByTestId(Page page, String testId) {
        return new NumberFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code NumberFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code NumberFieldElement}
     */
    public static NumberFieldElement getByTestId(Locator locator, String testId) {
        return new NumberFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
