package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for individual menu items {@code <vaadin-menu-bar-button>}.
 */
public class MenuItemElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-menu-bar-button";

    /** Create a {@code MenuItemElement} from an existing locator. */
    public MenuItemElement(Locator locator) {
        super(locator);
    }

    /**
     * Get a menu item by its full accessible label within a scope.
     * <p>
     * The label must match the item's whole accessible name, so {@code "Save"}
     * does not resolve to {@code "Save as…"}. Two items sharing the label fail
     * with a Playwright strict-mode error.
     */
    public static MenuItemElement getByLabel(Locator locator, String label) {
        return new MenuItemElement(ItemLocator.byExactName(locator, AriaRole.MENUITEM, label));
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
