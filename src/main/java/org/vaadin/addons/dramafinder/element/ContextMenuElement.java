package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.MouseButton;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for context menu overlays {@code <vaadin-context-menu-overlay>}.
 * <p>
 * Provides helpers to open a menu via a context-click on the target, inspect
 * the overlay list box, and pick menu items by their accessible label using
 * the {@code menu} role.
 */
public class ContextMenuElement extends VaadinElement implements HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-context-menu-overlay";

    /**
     * Create a {@code ContextMenuElement} from the page overlay.
     * Get the first opened context menu
     */
    public ContextMenuElement(Page page) {
        super(page.locator(FIELD_TAG_NAME + "[opened]"));
    }

    /**
     * Create a {@code ContextMenuElement} from an existing locator.
     */
    public ContextMenuElement(Locator locator) {
        super(locator);
    }

    /**
     * Open the context menu by invoking a context-click on the provided target.
     *
     * @param target the element that triggers the context menu
     */
    public static void openOn(Locator target) {
        target.click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
    }

    /**
     * Assert that the context menu overlay is open.
     */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /**
     * Assert that the context menu overlay is closed or hidden.
     */
    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    /**
     * Open a submenu and return its overlay.
     *
     * @param itemLabel visible label of the parent menu item
     * @return a {@code ContextMenuElement} pointing to the submenu overlay
     */
    public ContextMenuElement openSubMenu(String itemLabel) {
        getItemLocator(itemLabel).hover();
        return new ContextMenuElement(getLocator().locator(FIELD_TAG_NAME + "[opened]"));
    }

    /**
     * Select a menu item by its accessible name.
     *
     * @param itemLabel visible label of the menu item
     */
    public void selectItem(String itemLabel) {
        getItemLocator(itemLabel).click();
    }

    /**
     * Assert that a menu item is disabled.
     *
     * @param itemLabel visible label of the menu item
     */
    public void assertItemDisabled(String itemLabel) {
        assertThat(getItemLocator(itemLabel)).isDisabled();
    }

    /**
     * Assert that a menu item is enabled.
     *
     * @param itemLabel visible label of the menu item
     */
    public void assertItemEnabled(String itemLabel) {
        assertThat(getItemLocator(itemLabel)).isEnabled();
    }

    /**
     * Assert that a checkable menu item is checked.
     *
     * @param itemLabel visible label of the menu item
     */
    public void assertItemChecked(String itemLabel) {
        assertThat(getItemLocator(itemLabel))
                .hasAttribute("menu-item-checked", "");
    }

    /**
     * Assert that a checkable menu item is not checked.
     *
     * @param itemLabel visible label of the menu item
     */
    public void assertItemNotChecked(String itemLabel) {
        assertThat(getItemLocator(itemLabel))
                .not()
                .hasAttribute("menu-item-checked", "");
    }

    private Locator getItemLocator(String itemLabel) {
        return getLocator().page()
                .getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName(itemLabel))
                .first();
    }

}
