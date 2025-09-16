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

    public static ButtonElement get(Page page, String text) {
        return new ButtonElement(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(text)));
    }

    public static ButtonElement getByLabel(Page page, String label) {
        return new ButtonElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(page.getByText(label))
                        ).first());
    }
}
