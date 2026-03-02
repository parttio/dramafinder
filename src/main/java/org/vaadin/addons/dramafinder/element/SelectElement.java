package org.vaadin.addons.dramafinder.element;

import java.util.Objects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
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
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement {

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
     * <p>
     * Two cases are handled:
     * <ol>
     *   <li><b>Dropdown already open</b> (e.g. Grid Pro auto-opens it when
     *       entering edit mode): clicking any item inside the overlay is
     *       unreliable when the Vaadin loading indicator is present ("body
     *       intercepts pointer events"). Instead, the overlay is closed with
     *       Escape (Grid Pro stays in edit mode after a single Escape), and
     *       the select's {@code value} property is set directly via
     *       {@code evaluate}. Setting the property fires {@code value-changed},
     *       which Grid Pro listens to for auto-commit.</li>
     *   <li><b>Dropdown not yet open</b> (regular {@code vaadin-select}): the
     *       select is clicked to open the overlay, and the target item is
     *       clicked via {@code evaluate("el => el.click()")} to trigger the
     *       item's selection handler.</li>
     * </ol>
     *
     * @param item label of the item
     */
    public void selectItem(String item) {
        Locator listbox = getLocator().getByRole(AriaRole.LISTBOX);
        if (listbox.count() > 0) {
            // Dropdown is already open (Grid Pro context).
            // Close the dropdown with Escape; Grid Pro remains in edit mode
            // after a single Escape (a second Escape would exit edit mode).
            getLocator().page().keyboard().press("Escape");
            // Wait for the dropdown to disappear before setting the value.
            getLocator().page().waitForCondition(() -> listbox.count() == 0);
            // Setting the value property fires value-changed, which Grid Pro
            // uses to auto-commit the new value and exit edit mode.
            getLocator().evaluate("(el, v) => { el.value = v; }", item);
        } else {
            // Dropdown is not open (regular vaadin-select context).
            // Open it, then click the target item.
            getLocator().click();
            listbox.waitFor();
            getSelectItem(listbox, item).evaluate("el => el.click()");
        }
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
    public Locator getLabelLocator() {
        return getLocator().locator("label[slot=\"label\"]").first();
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

    private static Locator getSelectItem(Locator locator, String label) {
        return locator.locator(FIELD_ITEM_TAG_NAME)
                .filter(new Locator.FilterOptions()
                        .setHasText(label)).first();
    }

    /**
     * Get the {@code SelectElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code SelectElement}
     */
    public static SelectElement getByLabel(Page page, String label) {
        return new SelectElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

}
