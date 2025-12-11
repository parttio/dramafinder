package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-side-nav>}.
 */
@PlaywrightElement(SideNavigationElement.FIELD_TAG_NAME)
public class SideNavigationElement extends VaadinElement implements HasLabelElement {

    public static final String FIELD_TAG_NAME = "vaadin-side-nav";

    public SideNavigationElement(Locator locator) {
        super(locator);
    }

    @Override
    public Locator getLabelLocator() {
        return getLocator().locator("> [slot='label']");
    }

    /**
     * Checks if the side nav is collapsed.
     */
    public boolean isCollapsed() {
        return getLocator().getAttribute("collapsed") != null;
    }

    /**
     * Asserts that the side nav is collapsed.
     */
    public void assertCollapsed() {
        assertThat(getLocator()).hasAttribute("collapsed", "");
    }

    /**
     * Asserts that the side nav is expanded.
     */
    public void assertExpanded() {
        assertThat(getLocator()).not().hasAttribute("collapsed", "");
    }

    /**
     * Gets a SideNavigationItemElement by its label text.
     * This searches for a direct or nested vaadin-side-nav-item with the given
     * text.
     * Note: This strictly searches for the item that contains the text.
     * Use care if multiple items have the same text.
     *
     * @param label The label of the item.
     * @return The SideNavigationItemElement.
     */
    public SideNavigationItemElement getItem(String label) {
        // Using locator with hasText might be too broad if parent contains child text.
        // But vaadin-side-nav-item encapsulates its content.
        // Let's try to match exact text or contains.
        // A common strategy is to use the label content.

        // We construct a locator that finds a vaadin-side-nav-item that has this text.
        // Since text is inside the item's shadow or light dom slots.

        return new SideNavigationItemElement(
                getLocator().locator(SideNavigationItemElement.FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHasText(label)).first());
    }

    /**
     * Clicks an item by its label.
     *
     * @param label The label of the item to click.
     */
    public void clickItem(String label) {
        getItem(label).click();
    }

    /**
     * Get the {@code SideNavigationElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the side navigation
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getByLabel(Page page, String label) {
        return new SideNavigationElement(
                page.getByRole(AriaRole.NAVIGATION,
                        new Page.GetByRoleOptions().setName(label)
                ).and(page.locator(FIELD_TAG_NAME)).first());
    }
}
