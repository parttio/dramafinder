package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-accordion-panel>}.
 * <p>
 * Offers utilities to toggle open state, read summary and access content.
 */
@PlaywrightElement(AccordionPanelElement.FIELD_TAG_NAME)
public class AccordionPanelElement extends VaadinElement implements HasDisabledAttributeElement,
        Findable<ComponentQuery<AccordionPanelElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-accordion-panel";
    public static final String FIELD_HEADING_TAG_NAME = "vaadin-accordion-heading";

    /**
     * Create a new {@code AccordionPanelElement}.
     */
    public AccordionPanelElement(Locator locator) {
        super(locator);
    }

    /** Assert that the panel is opened. */
    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the panel is closed. */
    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    /** Whether the panel is open. */
    public boolean isOpen() {
        return getLocator().getAttribute("opened") != null;
    }

    /** Set the open state by clicking the summary when needed. */
    public void setOpen(boolean open) {
        if (isOpen() != open) {
            getSummaryLocator().click();
            waitAfterAction();
        }
    }

    /** Locator pointing to the summary heading. */
    public Locator getSummaryLocator() {
        return getLocator().locator(FIELD_HEADING_TAG_NAME);
    }

    /** Text content of the summary heading. */
    public String getSummaryText() {
        return getSummaryLocator().textContent();
    }

    /** Locator pointing to the first non-slotted content element. */
    public Locator getContentLocator() {
        return getLocator().locator("xpath=./*[not(@slot)][1]");
    }

    /** Assert that the content area is visible. */
    public void assertContentVisible() {
        assertThat(getContentLocator()).isVisible();
    }

    /** Assert that the content area is not visible. */
    public void assertContentNotVisible() {
        assertThat(getContentLocator()).not().isVisible();
    }

    /**
     * Get an accordion panel by its full summary text within a scope.
     * <p>
     * The summary must match the heading's whole text, so {@code "Section 1"}
     * does not resolve to {@code "Section 10"}. Two panels sharing the summary
     * fail with a Playwright strict-mode error.
     */
    public static AccordionPanelElement getAccordionPanelBySummary(Locator locator, String summary) {
        return new AccordionPanelElement(locator.locator(FIELD_TAG_NAME).filter(
                new Locator.FilterOptions().setHas(
                        locator.page().locator(FIELD_HEADING_TAG_NAME).filter(
                                new Locator.FilterOptions()
                                        .setHas(ItemLocator.exactText(locator.page(), summary)))
                )));
    }

    /** Get the currently opened accordion panel within a scope. */
    public static AccordionPanelElement getOpenedAccordionPanel(Locator locator) {
        return new AccordionPanelElement(locator.locator(FIELD_TAG_NAME).filter(
                new Locator.FilterOptions().setHas(
                        locator.page().locator(FIELD_HEADING_TAG_NAME + "[opened]")
                )));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get an {@code AccordionPanelElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code AccordionPanelElement}
     */
    public static AccordionPanelElement getById(Page page, String id) {
        return new AccordionPanelElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code AccordionPanelElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code AccordionPanelElement}
     */
    public static AccordionPanelElement getById(Locator locator, String id) {
        return new AccordionPanelElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code AccordionPanelElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code AccordionPanelElement}
     */
    public static AccordionPanelElement getByTestId(Page page, String testId) {
        return new AccordionPanelElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get an {@code AccordionPanelElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code AccordionPanelElement}
     */
    public static AccordionPanelElement getByTestId(Locator locator, String testId) {
        return new AccordionPanelElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
