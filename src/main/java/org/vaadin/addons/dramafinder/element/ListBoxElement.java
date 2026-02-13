package org.vaadin.addons.dramafinder.element;

import java.util.List;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-list-box>}.
 * <p>
 * Supports single and multiple selection, item-level enablement assertions,
 * and label-based lookup.
 */
@PlaywrightElement(ListBoxElement.FIELD_TAG_NAME)
public class ListBoxElement extends VaadinElement
        implements HasAriaLabelElement, HasStyleElement, HasTooltipElement,
        HasEnabledElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-list-box";
    /** The HTML tag name for list box items. */
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-item";
    /** The HTML attribute name for multiple selection mode. */
    public static final String MULTIPLE_ATTRIBUTE = "multiple";

    /**
     * Create a new {@code ListBoxElement}.
     *
     * @param locator the locator for the {@code <vaadin-list-box>} element
     */
    public ListBoxElement(Locator locator) {
        super(locator);
    }

    /**
     * Select the item based on its text.
     * In multiple mode, toggles selection state when already selected.
     *
     * @param item visible label of the item
     */
    public void selectItem(String item) {
        getItem(item).click();
    }

    /**
     * Get the selected value for single-select list boxes.
     *
     * @return the text content of the selected item
     */
    public String getSingleSelectedValue() {
        return getLocatorValue().innerText();
    }

    /**
     * Get all selected values for multi-select list boxes.
     *
     * @return list of text contents of all selected items
     */
    public List<String> getSelectedValue() {
        return getLocatorValue().allTextContents();
    }

    private Locator getLocatorValue() {
        return getLocator().locator("vaadin-item[selected]");
    }

    /**
     * Assert that the selected values match the expected labels.
     *
     * @param expected the expected selected item labels
     */
    public void assertSelectedValue(String... expected) {
        int length = expected.length;
        assertThat(getLocatorValue()).hasCount(length);
        // check that
        for (String value : expected) {
            assertThat(getItem(value)).hasAttribute("selected", "");
        }
    }

    @Override
    public boolean isEnabled() {
        return getEnabledLocator().isEnabled();
    }

    @Override
    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", Pattern.compile(".*"));
    }

    @Override
    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

    /**
     * Assert that a specific item is enabled.
     *
     * @param item the visible label of the item
     */
    public void assertItemEnabled(String item) {
        assertThat(getItem(item)).isEnabled();
    }

    /**
     * Assert that a specific item is disabled.
     *
     * @param item the visible label of the item
     */
    public void assertItemDisabled(String item) {
        assertThat(getItem(item)).isDisabled();
    }

    /**
     * Whether multiple selection mode is enabled.
     *
     * @return {@code true} if multiple selection is enabled
     */
    public boolean isMultiple() {
        return getLocator().getAttribute(MULTIPLE_ATTRIBUTE) != null;
    }

    /**
     * Assert that multiple selection is enabled.
     */
    public void assertMultiple() {
        assertThat(getLocator()).hasAttribute(MULTIPLE_ATTRIBUTE, "");
    }

    /**
     * Assert that single selection mode is enabled.
     */
    public void assertSingle() {
        assertThat(getLocator()).not().hasAttribute(MULTIPLE_ATTRIBUTE, "");
    }

    private Locator getItem(String label) {
        return locator.locator(FIELD_ITEM_TAG_NAME)
                .filter(new Locator.FilterOptions()
                        .setHasText(label)).first();
    }


    /**
     * Get the {@code ListBoxElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the list box
     * @return the matching {@code ListBoxElement}
     */
    public static ListBoxElement getByLabel(Page page, String label) {
        return new ListBoxElement(
                page.locator(FIELD_TAG_NAME)
                        .and(page.getByRole(AriaRole.LISTBOX,
                                new Page.GetByRoleOptions().setName(label))
                        ).first());
    }
}
