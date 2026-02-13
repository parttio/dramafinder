package org.vaadin.addons.dramafinder.element;

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
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

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
        HasClearButtonElement, HasAllowedCharPatternElement {

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
     * Assert that the displayed value equals the expected string.
     *
     * @param expected expected label or empty string for no selection
     */
    @Override
    public void assertValue(String expected) {
        assertThat(getInputLocator()).hasValue(expected != null ? expected : "");
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
     * ComboBox listens to for filtering.
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

    /**
     * Get the {@code ComboBoxElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Page page, String label) {
        return new ComboBoxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code ComboBoxElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Locator locator, String label) {
        return new ComboBoxElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }

    private Locator getOverlayItem(String label) {
        return getLocator().page().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")
                .filter(new Locator.FilterOptions()
                        .setHasText(label)).first();
    }
}
