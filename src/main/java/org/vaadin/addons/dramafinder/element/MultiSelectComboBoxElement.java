package org.vaadin.addons.dramafinder.element;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;

import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-multi-select-combo-box>}.
 * <p>
 * Provides helpers to open the overlay, filter items, select/deselect
 * multiple items, and query selected chips.
 */
@PlaywrightElement(MultiSelectComboBoxElement.FIELD_TAG_NAME)
public class MultiSelectComboBoxElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasInputFieldElement,
        HasThemeElement, HasPlaceholderElement,
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasAllowedCharPatternElement {

    public static final String FIELD_TAG_NAME = "vaadin-multi-select-combo-box";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-multi-select-combo-box-item";
    public static final String FIELD_CHIP_TAG_NAME = "vaadin-multi-select-combo-box-chip";

    /**
     * Create a new {@code MultiSelectComboBoxElement}.
     *
     * @param locator the locator for the {@code <vaadin-multi-select-combo-box>} element
     */
    public MultiSelectComboBoxElement(Locator locator) {
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
     * Get the selected values as a comma-separated string from the
     * {@code selectedItems} property.
     *
     * @return comma-separated selected values, or empty string when nothing is selected
     */
    @Override
    public String getValue() {
        List<String> items = getSelectedItems();
        return String.join(", ", items);
    }

    /**
     * Assert that the displayed value equals the expected string.
     *
     * @param expected expected comma-separated value or empty string for no selection
     */
    @Override
    public void assertValue(String expected) {
        if (expected == null || expected.isEmpty()) {
            getLocator().page().waitForCondition(() -> getSelectedItemCount() == 0);
        } else {
            getLocator().page().waitForCondition(() -> expected.equals(getValue()));
        }
    }

    // ── Selection ──────────────────────────────────────────────────────

    /**
     * Select an item by its visible label.
     * Opens the overlay, clicks the matching item (toggling its selection).
     *
     * @param item label of the item to select
     */
    public void selectItem(String item) {
        open();
        getOverlayItem(item).click();
    }

    /**
     * Deselect an item by its visible label.
     * Opens the overlay, clicks the matching item (toggling its selection off).
     *
     * @param item label of the item to deselect
     */
    public void deselectItem(String item) {
        open();
        getOverlayItem(item).click();
    }

    /**
     * Select multiple items in sequence.
     *
     * @param items labels of the items to select
     */
    public void selectItems(String... items) {
        open();
        for (String item : items) {
            getOverlayItem(item).click();
        }
    }

    /**
     * Deselect multiple items in sequence.
     *
     * @param items labels of the items to deselect
     */
    public void deselectItems(String... items) {
        open();
        for (String item : items) {
            getOverlayItem(item).click();
        }
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
    }

    /**
     * Type into the input to trigger filtering.
     * Uses {@code pressSequentially} to fire keyboard events that the
     * component listens to for filtering.
     *
     * @param filter the filter text
     */
    public void setFilter(String filter) {
        open();
        getInputLocator().pressSequentially(filter);
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

    // ── Open / Close ───────────────────────────────────────────────────

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

    // ── Read-only ──────────────────────────────────────────────────────

    /**
     * Whether the combo box is read-only.
     *
     * @return {@code true} when read-only
     */
    public boolean isReadOnly() {
        return getLocator().getAttribute("readonly") != null;
    }

    /**
     * Assert that the combo box is read-only.
     */
    public void assertReadOnly() {
        assertThat(getLocator()).hasAttribute("readonly", "");
    }

    /**
     * Assert that the combo box is not read-only.
     */
    public void assertNotReadOnly() {
        assertThat(getLocator()).not().hasAttribute("readonly", "");
    }

    // ── Toggle button ──────────────────────────────────────────────────

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
    }

    // ── Overlay items ──────────────────────────────────────────────────

    /**
     * Count visible overlay items.
     *
     * @return the number of visible items
     */
    public int getOverlayItemCount() {
        return getLocator().page().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])").count();
    }

    /**
     * Assert that the overlay contains exactly the expected number of items.
     *
     * @param expected expected item count
     */
    public void assertItemCount(int expected) {
        assertThat(getLocator().page().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")).hasCount(expected);
    }

    // ── Chips ──────────────────────────────────────────────────────────

    /**
     * Get the locator for all non-overflow chips.
     *
     * @return locator for selected-value chips
     */
    public Locator getChipLocators() {
        return getLocator().locator(FIELD_CHIP_TAG_NAME + ":not([slot=\"overflow\"])");
    }

    /**
     * Get the locator for the overflow chip.
     *
     * @return locator for the overflow chip
     */
    public Locator getOverflowChipLocator() {
        return getLocator().locator(FIELD_CHIP_TAG_NAME + "[slot=\"overflow\"]");
    }

    /**
     * Get the labels of all currently selected items by reading the
     * {@code selectedItems} property from the web component.
     *
     * @return list of selected item labels
     */
    @SuppressWarnings("unchecked")
    public List<String> getSelectedItems() {
        Object result = getLocator().evaluate(
                "el => (el.selectedItems || []).map(i => typeof i === 'string' ? i : i[el.itemLabelPath || 'label'] || String(i))");
        if (result instanceof List<?> list) {
            List<String> items = new ArrayList<>();
            for (Object item : list) {
                items.add(String.valueOf(item));
            }
            return items;
        }
        return Collections.emptyList();
    }

    /**
     * Get the count of currently selected items from the
     * {@code selectedItems} property.
     *
     * @return number of selected items
     */
    public int getSelectedItemCount() {
        return ((Number) getLocator().evaluate(
                "el => (el.selectedItems || []).length")).intValue();
    }

    /**
     * Assert that the selected item labels match the expected values.
     *
     * @param expected expected item labels
     */
    public void assertSelectedItems(String... expected) {
        getLocator().page().waitForCondition(() -> getSelectedItemCount() == expected.length);
        List<String> actual = getSelectedItems();
        for (String exp : expected) {
            assert actual.contains(exp)
                    : "Expected item '" + exp + "' in " + actual;
        }
    }

    /**
     * Assert that the number of selected items matches.
     *
     * @param expected expected number of selected items
     */
    public void assertSelectedCount(int expected) {
        getLocator().page().waitForCondition(() -> getSelectedItemCount() == expected);
    }

    // ── Overlay item component queries ─────────────────────────────────

    /**
     * Get a typed component element from an overlay item matching the given
     * text. The component class must be annotated with
     * {@link PlaywrightElement} and have a public constructor accepting a
     * single {@link Locator} parameter.
     *
     * @param itemText text of the overlay item to search in
     * @param type     the element class (e.g. {@code ButtonElement.class})
     * @param <T>      the element type
     * @return the first matching component inside the item
     */
    public <T extends VaadinElement> T getOverlayItemComponent(String itemText, Class<T> type) {
        return createComponent(getOverlayItem(itemText), type);
    }

    /**
     * Get a typed component element from an overlay item at the given
     * visible index. The component class must be annotated with
     * {@link PlaywrightElement} and have a public constructor accepting a
     * single {@link Locator} parameter.
     *
     * @param index 0-based visible item index
     * @param type  the element class (e.g. {@code ButtonElement.class})
     * @param <T>   the element type
     * @return the first matching component inside the item
     */
    public <T extends VaadinElement> T getOverlayItemComponent(int index, Class<T> type) {
        Locator item = getLocator().page().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])").nth(index);
        return createComponent(item, type);
    }

    // ── Static factories ───────────────────────────────────────────────

    /**
     * Get the {@code MultiSelectComboBoxElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code MultiSelectComboBoxElement}
     */
    public static MultiSelectComboBoxElement getByLabel(Page page, String label) {
        return new MultiSelectComboBoxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code MultiSelectComboBoxElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code MultiSelectComboBoxElement}
     */
    public static MultiSelectComboBoxElement getByLabel(Locator locator, String label) {
        return new MultiSelectComboBoxElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }

    // ── Internal ───────────────────────────────────────────────────────

    private Locator getOverlayItem(String label) {
        return getLocator().page().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")
                .filter(new Locator.FilterOptions()
                        .setHasText(label)).first();
    }

    private <T extends VaadinElement> T createComponent(Locator parent, Class<T> type) {
        PlaywrightElement annotation = type.getAnnotation(PlaywrightElement.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    type.getSimpleName() + " is not annotated with @PlaywrightElement");
        }
        Locator componentLocator = parent.locator(annotation.value()).first();
        try {
            return type.getConstructor(Locator.class).newInstance(componentLocator);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type.getSimpleName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type.getSimpleName(), e);
        }
    }
}
