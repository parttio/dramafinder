package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-side-nav>}.
 */
@PlaywrightElement(SideNavigationElement.FIELD_TAG_NAME)
public class SideNavigationElement extends VaadinElement implements HasLabelElement,
        Findable<FieldQuery<SideNavigationElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-side-nav";

    public SideNavigationElement(Locator locator) {
        super(locator);
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
     * Asserts that the side nav is collapsible.
     */
    public void assertCollapsible() {
        assertThat(getLocator()).hasAttribute("collapsible", "");
    }

    /**
     * Asserts that the side nav is not collapsible.
     */
    public void assertNotCollapsible() {
        assertThat(getLocator()).not().hasAttribute("collapsible", "");
    }

    /**
     * Gets a {@code SideNavigationItemElement} by its label text, at any nesting
     * depth.
     * <p>
     * The label must match the item's own label exactly; the labels of nested
     * items are not part of the match, so a parent is found by its own label and
     * is never returned for one of its children's labels. Two items sharing the
     * label fail with a Playwright strict-mode error instead of resolving to
     * whichever comes first in the DOM.
     * <p>
     * The item has to be visible: a collapsed parent has to be expanded first.
     *
     * @param label The full label of the item.
     * @return The SideNavigationItemElement.
     */
    public SideNavigationItemElement getItem(String label) {
        return new SideNavigationItemElement(ItemLocator.byOwnExactText(
                getLocator(), SideNavigationItemElement.FIELD_TAG_NAME, label));
    }

    /**
     * Clicks an item by its label.
     *
     * @param label The label of the item to click.
     */
    public void clickItem(String label) {
        getItem(label).click();
        waitAfterAction();
    }

    /**
     * Get the {@code SideNavigationElement} by its accessible name (ARIA role {@code navigation}),
     * searching the entire page.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two matching components fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the full accessible name
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getByLabel(Page page, String label) {
        return new SideNavigationElement(
                page.getByRole(AriaRole.NAVIGATION,
                        new Page.GetByRoleOptions().setName(label).setExact(true)
                ).and(page.locator(FIELD_TAG_NAME)));
    }

    /**
     * Get the {@code SideNavigationElement} whose accessible name (ARIA role {@code navigation}) contains
     * {@code text}, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring. No {@code .first()} is
     * applied: several matching components fail with a strict-mode error.
     *
     * @param page  the Playwright page
     * @param text  part of the accessible name
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getByLabelContaining(Page page, String text) {
        return new SideNavigationElement(
                page.getByRole(AriaRole.NAVIGATION,
                        new Page.GetByRoleOptions().setName(text)
                ).and(page.locator(FIELD_TAG_NAME)));
    }

    /**
     * Toggles the expansion state of the item.
     */
    public void toggle() {
        getLocator().locator("[slot='label']").first().click();
        waitAfterAction();
    }
}
