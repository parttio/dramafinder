package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-integer-field>}.
 * <p>
 * Provides typed helpers to read and modify integer-specific attributes
 * such as {@code min}, {@code max}, and {@code step}, and convenient
 * factory methods to locate the element by its accessible label.
 */
@PlaywrightElement(IntegerFieldElement.FIELD_TAG_NAME)
public class IntegerFieldElement extends AbstractNumberFieldElement implements Findable<InputFieldQuery<IntegerFieldElement>> {

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
     * Get the {@code IntegerFieldElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, IntegerFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, IntegerFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByLabel(Page page, String label) {
        return new IntegerFieldElement(ElementQueries.find(page, IntegerFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code IntegerFieldElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, IntegerFieldElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByLabelContaining(Page page, String text) {
        return new IntegerFieldElement(ElementQueries.find(page, IntegerFieldElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code IntegerFieldElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, IntegerFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, IntegerFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByLabel(Locator locator, String label) {
        return new IntegerFieldElement(ElementQueries.find(locator, IntegerFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code IntegerFieldElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, IntegerFieldElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByLabelContaining(Locator locator, String text) {
        return new IntegerFieldElement(ElementQueries.find(locator, IntegerFieldElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get an {@code IntegerFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getById(Page page, String id) {
        return new IntegerFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code IntegerFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getById(Locator locator, String id) {
        return new IntegerFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code IntegerFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByTestId(Page page, String testId) {
        return new IntegerFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get an {@code IntegerFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code IntegerFieldElement}
     */
    public static IntegerFieldElement getByTestId(Locator locator, String testId) {
        return new IntegerFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
