package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasReadOnlyElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-combo-box>}.
 * <p>
 * Provides helpers to open the overlay, filter items, and pick items by
 * visible text, along with aria/placeholder/validation mixins.
 */
@PlaywrightElement(ComboBoxElement.FIELD_TAG_NAME)
public class ComboBoxElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasInputFieldElement,
        HasPrefixElement, HasThemeElement, HasPlaceholderElement,
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasAllowedCharPatternElement, HasReadOnlyElement,
        Findable<InputFieldQuery<ComboBoxElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-combo-box";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-combo-box-item";

    /**
     * Create a new {@code ComboBoxElement}.
     *
     * @param locator the locator for the {@code <vaadin-combo-box>} element
     */
    public ComboBoxElement(Locator locator) {
        super(locator);
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    /**
     * Get the selected value label.
     * <p>
     * ComboBox stores an index in its {@code value} property, so this reads
     * the displayed text from the input element instead.
     *
     * @return the displayed value or empty string when nothing is selected
     */
    @Override
    public String getValue() {
        return getInputLocator().inputValue();
    }

    /**
     *
     * Assert that the displayed value equals the expected string.
     *
     * @param expected expected label or empty string for no selection
     */
    @Override
    public void assertValue(String expected) {
        assertThat(getInputLocator()).hasValue(expected != null ? expected : "");
    }

    /**
     * Select the item whose label equals {@code value}, by typing it as a filter
     * and closing the overlay once the matching item is shown.
     *
     * @param value label of the item to select
     */
    @Override
    public void setValue(String value) {
        getInputLocator().fill(value);
        waitAfterAction();
        assertThat(getOverlayItem(value)).isVisible();
        close();
    }

    /**
     * Select an item by its visible label.
     * Opens the overlay, clicks the matching item.
     *
     * @param item label of the item to select
     */
    public void selectItem(String item) {
        open();
        getOverlayItem(item).click();
        waitAfterAction();
    }

    /**
     * Type filter text into the input, then click the matching item.
     *
     * @param filter text to type for filtering
     * @param item   label of the item to select
     */
    public void filterAndSelectItem(String filter, String item) {
        setFilter(filter);
        getOverlayItem(item).click();
        waitAfterAction();
    }

    /**
     * Type into the input to trigger filtering.
     * Uses {@code pressSequentially} to fire keyboard events that the
     * ComboBox listens to for filtering.
     *
     * @param filter the filter text
     */
    public void setFilter(String filter) {
        // Clear input directly to avoid clear() triggering a server roundtrip
        // that may restore a non-empty default value.
        getInputLocator().clear();
        open();
        getInputLocator().pressSequentially(filter);
        waitAfterAction();
    }

    /**
     * Get the current filter value from the DOM property.
     *
     * @return the current filter string
     */
    public String getFilter() {
        Object value = getProperty("filter");
        return value == null ? "" : value.toString();
    }

    /**
     * Open the combo box overlay.
     */
    public void open() {
        setProperty("opened", true);
    }

    /**
     * Close the combo box overlay.
     */
    public void close() {
        setProperty("opened", false);
    }

    /**
     * Whether the overlay is currently open.
     *
     * @return {@code true} when the overlay is open
     */
    public boolean isOpened() {
        return Boolean.TRUE.equals(getProperty("opened"));
    }

    /**
     * Assert that the combo box overlay is open.
     */
    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /**
     * Assert that the combo box overlay is closed.
     */
    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    /**
     * Locator for the toggle button part.
     *
     * @return locator for the toggle button
     */
    public Locator getToggleButtonLocator() {
        return getLocator().locator("[part~=\"toggle-button\"]");
    }

    /**
     * Click the dropdown toggle button.
     */
    public void clickToggleButton() {
        getToggleButtonLocator().click();
        waitAfterAction();
    }

    /**
     * Count visible overlay items.
     *
     * @return the number of visible items
     */
    public int getOverlayItemCount() {
        return getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])").count();
    }

    /**
     * Assert that the overlay contains exactly the expected number of items.
     *
     * @param expected expected item count
     */
    public void assertItemCount(int expected) {
        assertThat(getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")).hasCount(expected);
    }

    /**
     * Get the {@code ComboBoxElement} by its label.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Page page, String label) {
        return new ComboBoxElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.COMBOBOX, label));
    }

    /**
     * Get the {@code ComboBoxElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabelContaining(Page page, String text) {
        return new ComboBoxElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.COMBOBOX, text));
    }

    /**
     * Get the {@code ComboBoxElement} by its label within a given scope.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Locator locator, String label) {
        return new ComboBoxElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.COMBOBOX, label));
    }

    /**
     * Get the {@code ComboBoxElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabelContaining(Locator locator, String text) {
        return new ComboBoxElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.COMBOBOX, text));
    }

    /**
     * Locator for a dropdown item, matched on its full label. {@code "Option 1"}
     * does not resolve to {@code "Option 10"}, and two visible items sharing the
     * label fail with a Playwright strict-mode error.
     */
    private Locator getOverlayItem(String label) {
        return ItemLocator.byExactText(getLocator(),
                FIELD_ITEM_TAG_NAME + ":not([hidden])", label);
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code ComboBoxElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getById(Page page, String id) {
        return new ComboBoxElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code ComboBoxElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getById(Locator locator, String id) {
        return new ComboBoxElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code ComboBoxElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByTestId(Page page, String testId) {
        return new ComboBoxElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code ComboBoxElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByTestId(Locator locator, String testId) {
        return new ComboBoxElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
