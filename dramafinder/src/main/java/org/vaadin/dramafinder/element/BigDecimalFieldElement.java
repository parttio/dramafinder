package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;

@PlaywrightElement(BigDecimalFieldElement.FIELD_TAG_NAME)
public class BigDecimalFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement,
        HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-big-decimal-field";

    public BigDecimalFieldElement(Locator locator) {
        super(locator);
    }

    public static BigDecimalFieldElement getByLabel(Page page, String label) {
        return new BigDecimalFieldElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.TEXTBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static BigDecimalFieldElement getByLabel(Locator locator, String label) {
        return new BigDecimalFieldElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
