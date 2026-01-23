package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-side-nav-item>}.
 */
@PlaywrightElement(SideNavigationItemElement.FIELD_TAG_NAME)
public class SideNavigationItemElement extends VaadinElement implements HasEnabledElement, HasPrefixElement, HasSuffixElement, HasLabelElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-side-nav-item";

    /**
     * Create a new {@code SideNavigationItemElement}.
     *
     * @param locator the locator for the {@code <vaadin-side-nav-item>} element
     */
    public SideNavigationItemElement(Locator locator) {
        super(locator);
    }

    /**
     * Checks if the item is expanded (showing child items).
     *
     * @return {@code true} if the item is expanded
     */
    public boolean isExpanded() {
        return getLocator().getAttribute("expanded") != null;
    }

    /**
     * Asserts that the item is expanded.
     */
    public void assertExpanded() {
        assertThat(getLocator()).hasAttribute("expanded", "");
    }

    /**
     * Asserts that the item is collapsed.
     */
    public void assertCollapsed() {
        assertThat(getLocator()).not().hasAttribute("expanded", "");
    }

    /**
     * Asserts that the item is enabled.
     */
    @Override
    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", "");
    }

    /**
     * Asserts that the item is disabled.
     */
    @Override
    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

    /**
     * Asserts that the item is current.
     */
    public void assertCurrent() {
        assertThat(getLocator()).hasAttribute("current", "");
    }

    /**
     * Asserts that the item is not current.
     */
    public void assertNotCurrent() {
        assertThat(getLocator()).not().hasAttribute("current", "");
    }


    /**
     * Toggles the expansion state of the item.
     * This relies on the toggle button inside the item.
     */
    public void toggle() {
        getLocator().locator("button[part='toggle-button']").first().click();
    }

    @Override
    public Locator getLabelLocator() {
        return getLocator();
    }

    /**
     * Navigate to this item by clicking its link.
     * This triggers navigation to the item's path.
     */
    public void navigate() {
        getLocator().getByRole(AriaRole.LINK).first().click();
    }
}
