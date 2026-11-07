package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.SideNavItemQuery;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-side-nav-item>}.
 */
@PlaywrightElement(SideNavigationItemElement.FIELD_TAG_NAME)
public class SideNavigationItemElement extends VaadinElement implements HasDisabledAttributeElement, HasPrefixElement, HasSuffixElement, HasLabelElement,
        Findable<SideNavItemQuery<SideNavigationItemElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-side-nav-item";

    public SideNavigationItemElement(Locator locator) {
        super(locator);
    }

    /**
     * Checks if the item is expanded.
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
        waitAfterAction();
    }

    @Override
    public Locator getLabelLocator() {
        return getLocator();
    }

    public void navigate() {
        getLocator().getByRole(AriaRole.LINK).first().click();
        waitAfterAction();
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code SideNavigationItemElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code SideNavigationItemElement}
     */
    public static SideNavigationItemElement getById(Page page, String id) {
        return new SideNavigationItemElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SideNavigationItemElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code SideNavigationItemElement}
     */
    public static SideNavigationItemElement getById(Locator locator, String id) {
        return new SideNavigationItemElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code SideNavigationItemElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code SideNavigationItemElement}
     */
    public static SideNavigationItemElement getByTestId(Page page, String testId) {
        return new SideNavigationItemElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code SideNavigationItemElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code SideNavigationItemElement}
     */
    public static SideNavigationItemElement getByTestId(Locator locator, String testId) {
        return new SideNavigationItemElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
