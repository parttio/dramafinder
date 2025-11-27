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
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

/**
 * PlaywrightElement for {@code <vaadin-big-decimal-field>}.
 */
@PlaywrightElement(BigDecimalFieldElement.FIELD_TAG_NAME)
public class BigDecimalFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement,
        HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-big-decimal-field";

    /**
     * Create a new {@code BigDecimalFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-big-decimal-field>} element
     */
    public BigDecimalFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code BigDecimalFieldElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByLabel(Page page, String label) {
        return new BigDecimalFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code BigDecimalFieldElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByLabel(Locator locator, String label) {
        return new BigDecimalFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
