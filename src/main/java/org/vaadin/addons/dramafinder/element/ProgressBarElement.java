package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.NumberUtils;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-progress-bar>}.
 * <p>
 * Supports value/min/max setters and assertions and indeterminate state.
 */
@PlaywrightElement(ProgressBarElement.FIELD_TAG_NAME)
public class ProgressBarElement extends VaadinElement implements HasThemeElement, HasStyleElement,
        Findable<ComponentQuery<ProgressBarElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-progress-bar";
    public static final String INDETERMINATE_ATTRIBUTE = "indeterminate";

    /**
     * Create a {@code ProgressBarElement} from an existing locator.
     */
    public ProgressBarElement(Locator locator) {
        super(locator.and(locator.page().locator(FIELD_TAG_NAME)));
    }

    /**
     * Current numeric value parsed from {@code aria-valuenow}.
     */
    public double getValue() {
        return Double.parseDouble(getLocator().getAttribute("aria-valuenow"));
    }

    /**
     * Set the progress bar {@code value}.
     */
    public void setValue(double min) {
        getLocator().evaluate("(el, v) => el.value = v", min);
    }

    /**
     * Assert that the numeric value matches.
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
     */
    public Double getMin() {
        String v = getLocator().getAttribute("aria-valuemin");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code min} value.
     */
    public void setMin(double min) {
        getLocator().evaluate("(el, v) => el.min = v", min);
    }

    /**
     * Assert that {@code min} matches the expected value.
     */
    public void assertMin(double min) {
        assertThat(getLocator()).hasAttribute("aria-valuemin", NumberUtils.formatDouble(min));
    }

    /**
     * Get the {@code max} value.
     */
    public Double getMax() {
        String v = getLocator().getAttribute("aria-valuemax");
        return v == null ? null : Double.valueOf(v);
    }

    /**
     * Set the {@code max} value.
     */
    public void setMax(double max) {
        getLocator().evaluate("(el, v) => el.max = v", max);
    }

    /**
     * Assert that {@code max} matches the expected value.
     */
    public void assertMax(double max) {
        assertThat(getLocator()).hasAttribute("aria-valuemax", NumberUtils.formatDouble(max));
    }

    /**
     * Whether the bar is indeterminate.
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
     */
    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code ProgressBarElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code ProgressBarElement}
     */
    public static ProgressBarElement getById(Page page, String id) {
        return new ProgressBarElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code ProgressBarElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code ProgressBarElement}
     */
    public static ProgressBarElement getById(Locator locator, String id) {
        return new ProgressBarElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code ProgressBarElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code ProgressBarElement}
     */
    public static ProgressBarElement getByTestId(Page page, String testId) {
        return new ProgressBarElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code ProgressBarElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code ProgressBarElement}
     */
    public static ProgressBarElement getByTestId(Locator locator, String testId) {
        return new ProgressBarElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
