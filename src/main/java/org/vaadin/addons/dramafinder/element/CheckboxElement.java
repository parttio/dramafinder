package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasCheckedElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-checkbox>}.
 * <p>
 * Provides helpers to read and modify checked/indeterminate state and
 * access common mixins for label, helper, validation and enablement.
 * The checked-state API comes from {@link HasCheckedElement}.
 */
@PlaywrightElement(CheckboxElement.FIELD_TAG_NAME)
public class CheckboxElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasCheckedElement, HasEnabledElement,
        HasHelperElement, HasStyleElement, HasLabelElement, HasValidationPropertiesElement,
        Findable<FieldQuery<CheckboxElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-checkbox";

    /**
     * Create a new {@code CheckboxElement}.
     *
     * @param locator the locator for the {@code <vaadin-checkbox>} element
     */
    public CheckboxElement(Locator locator) {
        super(locator);
    }

    /**
     * Check or uncheck the checkbox.
     *
     * @param checked {@code true} to check, {@code false} to uncheck
     * @deprecated use {@link #setChecked(boolean)} instead; this name reads as a
     *             getter and is inconsistent with the other checkbox-like
     *             elements.
     */
    @Deprecated(forRemoval = true)
    public void isChecked(boolean checked) {
        setChecked(checked);
    }

    /**
     * Whether the checkbox is in indeterminate state.
     *
     * @return {@code true} when indeterminate
     */
    public boolean isIndeterminate() {
        return getLocator().getAttribute("indeterminate") != null;
    }

    /**
     * Assert that the checkbox is indeterminate.
     */
    public void assertIndeterminate() {
        assertThat(getLocator()).hasAttribute("indeterminate", "");
    }

    /**
     * Assert that the checkbox is not indeterminate.
     */
    public void assertNotIndeterminate() {
        assertThat(getLocator()).not().hasAttribute("indeterminate", "");
    }

    /**
     * Set the indeterminate state.
     *
     * @param indeterminate {@code true} to set indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        getLocator().evaluate("(el, val) => el.indeterminate = val", indeterminate);
    }

    /**
     * Get the {@code CheckboxElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} is not a label: use
     * {@code find(page, CheckboxElement.class).withAccessibleName(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, CheckboxElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabel(Page page, String label) {
        return new CheckboxElement(ElementQueries.find(page, CheckboxElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code CheckboxElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, CheckboxElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabelContaining(Page page, String text) {
        return new CheckboxElement(ElementQueries.find(page, CheckboxElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code CheckboxElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} is not a label: use
     * {@code find(locator, CheckboxElement.class).withAccessibleName(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, CheckboxElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabel(Locator locator, String label) {
        return new CheckboxElement(ElementQueries.find(locator, CheckboxElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code CheckboxElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, CheckboxElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabelContaining(Locator locator, String text) {
        return new CheckboxElement(ElementQueries.find(locator, CheckboxElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code CheckboxElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getById(Page page, String id) {
        return new CheckboxElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code CheckboxElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getById(Locator locator, String id) {
        return new CheckboxElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code CheckboxElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByTestId(Page page, String testId) {
        return new CheckboxElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code CheckboxElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByTestId(Locator locator, String testId) {
        return new CheckboxElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
