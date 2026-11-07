package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
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
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
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
     * Get the {@code CheckboxElement} by its accessible name (ARIA role {@code checkbox}),
     * searching the entire page.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two matching components fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the full accessible name
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabel(Page page, String label) {
        return new CheckboxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.CHECKBOX,
                                        new Page.GetByRoleOptions().setName(label).setExact(true)))
                        ));
    }

    /**
     * Get the {@code CheckboxElement} whose accessible name (ARIA role {@code checkbox}) contains
     * {@code text}, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring. No {@code .first()} is
     * applied: several matching components fail with a strict-mode error.
     *
     * @param page  the Playwright page
     * @param text  part of the accessible name
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabelContaining(Page page, String text) {
        return new CheckboxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.CHECKBOX,
                                        new Page.GetByRoleOptions().setName(text)))
                        ));
    }

    /**
     * Get a {@code CheckboxElement} by its accessible label within a given scope.
     * <p>
     * Item-within-container lookup: the label must match the checkbox's full
     * accessible name, so {@code "Option 1"} no longer resolves to
     * {@code "Option 10"}. Two checkboxes sharing the label inside the scope fail
     * with a Playwright strict-mode error.
     *
     * @param locator the locator to search within (e.g. a checkbox group)
     * @param label   the full accessible label of the checkbox
     * @return the matching {@code CheckboxElement}
     */
    public static CheckboxElement getByLabel(Locator locator, String label) {
        return new CheckboxElement(
                AccessibleNameLocator.findExact(locator, FIELD_TAG_NAME, AriaRole.CHECKBOX, label));
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
