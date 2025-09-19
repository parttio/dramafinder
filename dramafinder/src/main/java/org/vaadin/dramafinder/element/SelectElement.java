package org.vaadin.dramafinder.element;

import java.util.Objects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(SelectElement.FIELD_TAG_NAME)
public class SelectElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasInputFieldElement,
        HasPrefixElement, HasThemeElement, HasPlaceholderElement,
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement {

    public static final String FIELD_TAG_NAME = "vaadin-select";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-select-item";
    public static final String FIELD_OVERLAY_TAG_NAME = "vaadin-select-overlay";

    @Override
    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"value\"]").first();
    }

    public SelectElement(Locator locator) {
        super(locator);
    }

    /**
     * Select the item by label
     *
     * @param item label of the item
     */
    public void selectItem(String item) {
        getLocator().click();
        getSelectItem(getLocator().page().locator(FIELD_OVERLAY_TAG_NAME + "[opened]"), item).click();
    }

    /**
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
     *
     * @param expected
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
    
    public static SelectElement getByLabel(Page page, String label) {
        return new SelectElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

}
