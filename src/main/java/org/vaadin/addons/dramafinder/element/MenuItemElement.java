package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for individual menu items {@code <vaadin-menu-bar-button>}.
 */
public class MenuItemElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-menu-bar-button";

    /**
     * Create a {@code MenuItemElement} from an existing locator.
     *
     * @param locator the locator for the menu item
     */
    public MenuItemElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a menu item by its accessible label within a scope.
     *
     * @param locator the scope to search within
     * @param label   the accessible label of the menu item
     * @return the matching {@code MenuItemElement}
     */
    public static MenuItemElement getByLabel(Locator locator, String label) {
        return new MenuItemElement(
                locator.getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName(label))
        );
    }

    /** Assert that the menu item is expanded (shows submenu). */
    public void assertExpanded() {
        assertThat(getLocator()).hasAttribute("expanded", "");
    }

    /** Assert that the menu item is collapsed. */
    public void assertCollapsed() {
        assertThat(getLocator()).not().hasAttribute("expanded", "");
    }
}
