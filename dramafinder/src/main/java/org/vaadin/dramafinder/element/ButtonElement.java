package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.dramafinder.element.shared.HasStyleElement;
import org.vaadin.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;

@PlaywrightElement(ButtonElement.FIELD_TAG_NAME)
public class ButtonElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasEnabledElement,
        HasPrefixElement, HasStyleElement, HasSuffixElement, HasThemeElement, HasTooltipElement {

    public static final String FIELD_TAG_NAME = "vaadin-button";

    public ButtonElement(Locator locator) {
        super(locator);
    }

    /**
     * Get by label or text
     *
     * @param page
     * @param text
     * @return
     */
    public static ButtonElement getByText(Page page, String text) {
        return getByText(page, new Page.GetByRoleOptions().setName(text));
    }

    public static ButtonElement getByText(Page page, Page.GetByRoleOptions options) {
        return new ButtonElement(
                page.getByRole(
                        AriaRole.BUTTON,
                        options
                ).and(page.locator(FIELD_TAG_NAME))
        );
    }

    public static ButtonElement getByText(Locator locator, String text) {
        return getByText(locator, new Locator.GetByRoleOptions().setName(text));
    }

    public static ButtonElement getByText(Locator locator, Locator.GetByRoleOptions options) {
        return new ButtonElement(
                locator.getByRole(
                        AriaRole.BUTTON,
                        options
                ).and(locator.page().locator(FIELD_TAG_NAME))
        );
    }

    public static ButtonElement getByLabel(Page page, String text) {
        return getByText(page, text);
    }

}
