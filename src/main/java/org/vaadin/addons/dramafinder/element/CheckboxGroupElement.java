package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-checkbox-group>}.
 * <p>
 * Provides helpers to select and deselect the group's checkboxes by their
 * label, to read the current selection and to assert it. Individual items are
 * exposed as {@link CheckboxElement} instances so the full checkbox API is
 * available for them.
 * <p>
 * The group's client-side {@code value} holds opaque server-generated item
 * keys, so this element identifies items by their visible label instead.
 */
@PlaywrightElement(CheckboxGroupElement.FIELD_TAG_NAME)
public class CheckboxGroupElement extends VaadinElement
        implements HasLabelElement, HasHelperElement, HasValidationPropertiesElement,
        HasEnabledElement, HasThemeElement, HasStyleElement, HasTooltipElement,
        Findable<FieldQuery<CheckboxGroupElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-checkbox-group";

    /**
     * Create a new {@code CheckboxGroupElement}.
     *
     * @param locator the locator for the {@code <vaadin-checkbox-group>} element
     */
    public CheckboxGroupElement(Locator locator) {
        super(locator);
    }

    // ── Items ──────────────────────────────────────────────────────────

    /**
     * Get all checkboxes of the group, in DOM order.
     *
     * @return the group's checkboxes, empty when the group has no items
     */
    public List<CheckboxElement> getCheckboxes() {
        Locator checkboxes = getCheckboxLocator();
        int count = checkboxes.count();
        List<CheckboxElement> elements = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            elements.add(new CheckboxElement(checkboxes.nth(i)));
        }
        return elements;
    }

    /**
     * Get a single checkbox of the group by its label.
     *
     * @param label the accessible label of the checkbox (ARIA role {@code checkbox})
     * @return the matching {@code CheckboxElement}
     */
    public CheckboxElement getCheckbox(String label) {
        return CheckboxElement.getByLabel(getLocator(), label);
    }

    /**
     * Count the checkboxes of the group.
     *
     * @return the number of checkboxes
     */
    public int getCheckboxCount() {
        return getCheckboxLocator().count();
    }

    /**
     * Assert that the group contains exactly the expected number of checkboxes.
     *
     * @param expected expected checkbox count
     */
    public void assertCheckboxCount(int expected) {
        assertThat(getCheckboxLocator()).hasCount(expected);
    }

    // ── Selection ──────────────────────────────────────────────────────

    /**
     * Select the checkboxes matching the given labels. Already selected
     * checkboxes are left untouched.
     *
     * @param labels labels of the checkboxes to select
     */
    public void selectByLabel(String... labels) {
        for (String label : labels) {
            getCheckbox(label).check();
        }
        waitAfterAction();
    }

    /**
     * Deselect the checkboxes matching the given labels. Already deselected
     * checkboxes are left untouched.
     *
     * @param labels labels of the checkboxes to deselect
     */
    public void deselectByLabel(String... labels) {
        for (String label : labels) {
            getCheckbox(label).uncheck();
        }
        waitAfterAction();
    }

    /**
     * Deselect every checkbox of the group.
     */
    public void deselectAll() {
        Locator checked = getCheckedCheckboxLocator();
        for (int i = checked.count() - 1; i >= 0; i--) {
            new CheckboxElement(checked.nth(i)).uncheck();
        }
        waitAfterAction();
    }

    /**
     * Get the labels of the currently selected checkboxes, in DOM order.
     * <p>
     * Labels are used rather than the group's {@code value} array because the
     * latter contains server-generated item keys that carry no meaning in a
     * test.
     *
     * @return the labels of the selected checkboxes, empty when nothing is selected
     */
    public List<String> getSelectedValues() {
        Object result = getLocator().evaluate(
                "el => Array.from(el.querySelectorAll('" + CheckboxElement.FIELD_TAG_NAME + "'))"
                        + ".filter(cb => cb.checked)"
                        + ".map(cb => cb.label != null ? cb.label : cb.textContent.trim())");
        if (result instanceof List<?> list) {
            List<String> labels = new ArrayList<>(list.size());
            for (Object label : list) {
                labels.add(String.valueOf(label));
            }
            return labels;
        }
        return Collections.emptyList();
    }

    /**
     * Assert that exactly the checkboxes with the given labels are selected.
     * Passing no label (or {@code null}) asserts that nothing is selected.
     *
     * @param labels labels of the checkboxes expected to be selected
     */
    public void assertSelected(String... labels) {
        if (labels == null || labels.length == 0) {
            assertThat(getCheckedCheckboxLocator()).hasCount(0);
            return;
        }
        assertThat(getCheckedCheckboxLocator()).hasCount(labels.length);
        for (String label : labels) {
            getCheckbox(label).assertChecked();
        }
    }

    // ── Static factories ───────────────────────────────────────────────

    /**
     * Get the {@code CheckboxGroupElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, CheckboxGroupElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, CheckboxGroupElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByLabel(Page page, String label) {
        return new CheckboxGroupElement(ElementQueries.find(page, CheckboxGroupElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code CheckboxGroupElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, CheckboxGroupElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByLabelContaining(Page page, String text) {
        return new CheckboxGroupElement(ElementQueries.find(page, CheckboxGroupElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code CheckboxGroupElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, CheckboxGroupElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, CheckboxGroupElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByLabel(Locator locator, String label) {
        return new CheckboxGroupElement(ElementQueries.find(locator, CheckboxGroupElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code CheckboxGroupElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, CheckboxGroupElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByLabelContaining(Locator locator, String text) {
        return new CheckboxGroupElement(ElementQueries.find(locator, CheckboxGroupElement.class).withLabelContaining(text).getLocator());
    }

    // ── Internal ───────────────────────────────────────────────────────

    private Locator getCheckboxLocator() {
        return getLocator().locator(CheckboxElement.FIELD_TAG_NAME);
    }

    private Locator getCheckedCheckboxLocator() {
        return getLocator().locator(CheckboxElement.FIELD_TAG_NAME + ":has(input:checked)");
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code CheckboxGroupElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getById(Page page, String id) {
        return new CheckboxGroupElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code CheckboxGroupElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getById(Locator locator, String id) {
        return new CheckboxGroupElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code CheckboxGroupElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByTestId(Page page, String testId) {
        return new CheckboxGroupElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code CheckboxGroupElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByTestId(Locator locator, String testId) {
        return new CheckboxGroupElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
