package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasStyleElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MenuItemElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar-button";

    public MenuItemElement(Locator locator) {
        super(locator);
    }

    public static MenuItemElement getByLabel(Locator locator, String label) {
        return new MenuItemElement(
                locator.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName(label))
        );
    }

    public void assertExpanded() {
        assertThat(getLocator()).hasAttribute("expanded", "");
    }

    public void assertCollapsed() {
        assertThat(getLocator()).not().hasAttribute("expanded", "");
    }
}
