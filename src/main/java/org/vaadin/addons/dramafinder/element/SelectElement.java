package org.vaadin.addons.dramafinder.element;

import java.util.Objects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-select>}.
 * <p>
 * Provides helpers to open the overlay and pick items by visible text,
 * along with aria/placeholder/validation mixins.
 */
@PlaywrightElement(SelectElement.FIELD_TAG_NAME)
public class SelectElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasInputFieldElement,
        HasPrefixElement, HasThemeElement, HasPlaceholderElement,
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement,
        Findable<FieldQuery<SelectElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-select";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-select-item";
    public static final String FIELD_OVERLAY_TAG_NAME = "vaadin-select-list-box";

    @Override
    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"value\"]").first();
    }

    /**
     * Create a new {@code SelectElement}.
     *
     * @param locator the locator for the {@code <vaadin-select>} element
     */
    public SelectElement(Locator locator) {
        super(locator);
    }

    /**
     * Select an item by its visible label.
     *
     * @param item label of the item
     */
    public void selectItem(String item) {
        getLocator().click();
        getSelectItem(getLocator().getByRole(AriaRole.LISTBOX), item).click();
    }

    /**
     * Get the selected value label for single-select.
     *
     * @return the label of the value
     */
    @Override
    public String getValue() {
        return getInputLocator().innerText();
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getLocator().locator("label[slot=\"sr-label\"]").first();
    }

    @Override
    public String getAriaLabel() {
        return getAriaLabelLocator().textContent();
    }

    @Override
    public void assertAriaLabel(String ariaLabel) {
        if (ariaLabel != null) {
            assertThat(getAriaLabelLocator()).hasText(ariaLabel);
        } else {
            assertThat(getAriaLabelLocator()).isHidden();
        }
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    @Override
    public void assertPlaceholder(String placeholder) {
        assertThat(getInputLocator()).hasAttribute("placeholder", "");
        assertThat(getInputLocator()).hasText(placeholder);
    }

    /**
     * Assert the selected value label equals the expected string.
     *
     * @param expected expected label or empty for no selection
     */
    @Override
    public void assertValue(String expected) {
        assertThat(getInputLocator()).hasText(Objects.requireNonNullElse(expected, ""));
    }

    /**
     * Locator for an overlay item, matched on its full label. {@code "Option 1"}
     * does not resolve to {@code "Option 10"}, and two items sharing the label
     * fail with a Playwright strict-mode error.
     */
    private static Locator getSelectItem(Locator locator, String label) {
        return ItemLocator.byExactText(locator, FIELD_ITEM_TAG_NAME, label);
    }

    /**
     * Get the {@code SelectElement} by its label, searching the entire page.
     * <p>
     * Matches the visible label or the {@code aria-label} of the select. Unlike the
     * other fields the select's accessible name is not used as such, because it also
     * includes the selected value or placeholder, and would change on selection.
     * <p>
     * The label must match in full, case-sensitively, and no {@code .first()} is
     * applied: two selects with the same label fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full label of the select
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabel(Page page, String label) {
        return new SelectElement(byLabel(page.locator(FIELD_TAG_NAME), page, label, true));
    }

    /**
     * Get the {@code SelectElement} whose label contains {@code text}, searching the
     * entire page.
     * <p>
     * The visible label or {@code aria-label} is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching selects fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the label of the select
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabelContaining(Page page, String text) {
        return new SelectElement(byLabel(page.locator(FIELD_TAG_NAME), page, text, false));
    }

    /**
     * Get the {@code SelectElement} by its label within a given scope.
     * <p>
     * Matches the visible label or the {@code aria-label} of the select, in full and
     * case-sensitively; two selects with the same label fail with a strict-mode error.
     * Use {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator to search within
     * @param label   the full label of the select
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabel(Locator locator, String label) {
        return new SelectElement(byLabel(locator.locator(FIELD_TAG_NAME), locator.page(), label, true));
    }

    /**
     * Get the {@code SelectElement} whose label contains {@code text}, scoped to the
     * given locator.
     * <p>
     * The visible label or {@code aria-label} is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching selects fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the label of the select
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabelContaining(Locator locator, String text) {
        return new SelectElement(byLabel(locator.locator(FIELD_TAG_NAME), locator.page(), text, false));
    }

    /** Selects whose own visible label, or whose value button's aria-label, matches. */
    private static Locator byLabel(Locator selects, Page page, String label, boolean exact) {
        Locator visibleLabel = page.locator(":scope > [slot='label']")
                .and(page.getByText(label, new Page.GetByTextOptions().setExact(exact)));
        Locator ariaLabel = page.locator(":scope > [slot='value']")
                .and(page.getByLabel(label, new Page.GetByLabelOptions().setExact(exact)));
        return selects.filter(new Locator.FilterOptions().setHas(visibleLabel.or(ariaLabel)));
    }

}
