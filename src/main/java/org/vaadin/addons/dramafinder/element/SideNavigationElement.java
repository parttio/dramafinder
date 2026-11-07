package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
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

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code SideNavigationElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getById(Page page, String id) {
        return new SideNavigationElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SideNavigationElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getById(Locator locator, String id) {
        return new SideNavigationElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SideNavigationElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getByTestId(Page page, String testId) {
        return new SideNavigationElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code SideNavigationElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code SideNavigationElement}
     */
    public static SideNavigationElement getByTestId(Locator locator, String testId) {
        return new SideNavigationElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
