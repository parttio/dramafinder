package org.vaadin.addons.dramafinder.element;

import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
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
     * Clear the selection as a user does: click the clear button when it is
     * visible, otherwise empty the input and leave the field.
     * <p>
     * Either way the value becomes empty ({@code null} on the server). Unlike
     * {@code setValue("")}, this does not look for an item labelled {@code ""}.
     * Waits for the server round-trip afterwards.
     */
    @Override
    public void clear() {
        if (isClearButtonVisible()) {
            clickClearButton();
            return;
        }
        getInputLocator().fill("");
        getInputLocator().blur();
        waitAfterAction();
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
     * Get the {@code ComboBoxElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, ComboBoxElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, ComboBoxElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Page page, String label) {
        return new ComboBoxElement(ElementQueries.find(page, ComboBoxElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code ComboBoxElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, ComboBoxElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabelContaining(Page page, String text) {
        return new ComboBoxElement(ElementQueries.find(page, ComboBoxElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code ComboBoxElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, ComboBoxElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, ComboBoxElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Locator locator, String label) {
        return new ComboBoxElement(ElementQueries.find(locator, ComboBoxElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code ComboBoxElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, ComboBoxElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabelContaining(Locator locator, String text) {
        return new ComboBoxElement(ElementQueries.find(locator, ComboBoxElement.class).withLabelContaining(text).getLocator());
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

    // ── Items ──────────────────────────────────────────────────────────

    /**
     * Get the labels of every item the drop-down offers for the current filter, in order.
     * Opens the overlay if needed and waits for the items to load; with a lazy data provider the
     * pages not loaded yet are requested one after the other, so all items are returned, not only
     * the rendered ones. Closes the overlay again if it was closed.
     *
     * @return the item labels; empty when the combo box offers no item
     * @throws IllegalStateException if the items do not load before the timeout, for example on a
     *                               read-only or disabled combo box whose overlay does not open
     */
    public List<String> getItemLabels() {
        return ComboBoxItemLabels.read(getLocator(), this::isOpened, this::open, this::close);
    }

    /**
     * Get the label of the selected item, read from the component's {@code selectedItem}. Unlike
     * {@link #getValue()}, which reads the input text, this is not affected by a filter typed into
     * the input, and a custom value (with {@code allowCustomValue}) is not an item: the field then
     * shows the value while this returns {@code null}.
     *
     * @return the selected item's label ({@code ""} for an item without a label), or {@code null}
     *         when no item is selected
     */
    public String getSelectedItemLabel() {
        Object label = getLocator().evaluate("""
                el => { const i = el.selectedItem;
                        if (i === undefined || i === null) return null;
                        return typeof i === 'string' ? i : String(i[el.itemLabelPath || 'label'] ?? ''); }""");
        return label == null ? null : label.toString();
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
