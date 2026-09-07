package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

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
        HasEnabledElement, HasThemeElement, HasStyleElement, HasTooltipElement {

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
        waitForVaadinIdle();
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
        waitForVaadinIdle();
    }

    /**
     * Deselect every checkbox of the group.
     */
    public void deselectAll() {
        Locator checked = getCheckedCheckboxLocator();
        for (int i = checked.count() - 1; i >= 0; i--) {
            new CheckboxElement(checked.nth(i)).uncheck();
        }
        waitForVaadinIdle();
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
     * Get a {@code CheckboxGroupElement} by its accessible label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the group (ARIA role {@code group})
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByLabel(Page page, String label) {
        return new CheckboxGroupElement(
                page.locator(FIELD_TAG_NAME).and(
                        page.getByRole(AriaRole.GROUP,
                                new Page.GetByRoleOptions().setName(label))).first());
    }

    /**
     * Get a {@code CheckboxGroupElement} by its accessible label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the group (ARIA role {@code group})
     * @return the matching {@code CheckboxGroupElement}
     */
    public static CheckboxGroupElement getByLabel(Locator locator, String label) {
        return new CheckboxGroupElement(
                locator.locator(FIELD_TAG_NAME).and(
                        locator.page().getByRole(AriaRole.GROUP,
                                new Page.GetByRoleOptions().setName(label))).first());
    }

    // ── Internal ───────────────────────────────────────────────────────

    private Locator getCheckboxLocator() {
        return getLocator().locator(CheckboxElement.FIELD_TAG_NAME);
    }

    private Locator getCheckedCheckboxLocator() {
        return getLocator().locator(CheckboxElement.FIELD_TAG_NAME + ":has(input:checked)");
    }
}
