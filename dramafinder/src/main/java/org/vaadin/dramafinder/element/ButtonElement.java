package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.*;

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
        return new ButtonElement(
                page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName(text)
                ).and(page.locator(FIELD_TAG_NAME))
        );
    }

    public static ButtonElement getByLabel(Page page, String text) {
        return getByText(page, text);
    }

}
