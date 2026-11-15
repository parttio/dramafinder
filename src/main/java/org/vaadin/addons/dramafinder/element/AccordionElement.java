package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-accordion>}.
 * <p>
 * Provides helpers to access panels by their summary text and to assert
 * open/closed state and panel count.
 */
@PlaywrightElement(AccordionElement.FIELD_TAG_NAME)
public class AccordionElement extends VaadinElement implements HasStyleElement,
        Findable<ComponentQuery<AccordionElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-accordion";

    /**
     * Create a new {@code AccordionElement}.
     *
     * @param locator the locator for the {@code <vaadin-accordion>} element
     */
    public AccordionElement(Locator locator) {
        super(locator);
    }

    /**
     * Assert the number of panels present in the accordion.
     */
    public void assertPanelCount(int count) {
        Locator panels = getLocator().locator(AccordionPanelElement.FIELD_TAG_NAME);
        assertThat(panels).hasCount(count);
    }

    /**
     * Get a panel by its summary text.
     */
    public AccordionPanelElement getPanel(String summary) {
        return AccordionPanelElement.getAccordionPanelBySummary(getLocator(), summary);
    }

    /**
     * Open a panel by its summary text.
     */
    public void openPanel(String summary) {
        getPanel(summary).setOpen(true);
    }

    /**
     * Close a panel by its summary text.
     */
    public void closePanel(String summary) {
        getPanel(summary).setOpen(false);
    }

    /**
     * Whether the panel with the given summary is open.
     */
    public boolean isPanelOpened(String summary) {
        return getPanel(summary).isOpen();
    }

    /**
     * Assert that the panel with the given summary is open.
     */
    public void assertPanelOpened(String summary) {
        getPanel(summary).assertOpened();
    }

    /**
     * Assert that the panel with the given summary is closed.
     */
    public void assertPanelClosed(String summary) {
        getPanel(summary).assertClosed();
    }

    /**
     * Get the currently opened panel.
     */
    public AccordionPanelElement getOpenedPanel() {
        return AccordionPanelElement.getOpenedAccordionPanel(getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get an {@code AccordionElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code AccordionElement}
     */
    public static AccordionElement getById(Page page, String id) {
        return new AccordionElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code AccordionElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code AccordionElement}
     */
    public static AccordionElement getById(Locator locator, String id) {
        return new AccordionElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code AccordionElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code AccordionElement}
     */
    public static AccordionElement getByTestId(Page page, String testId) {
        return new AccordionElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get an {@code AccordionElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code AccordionElement}
     */
    public static AccordionElement getByTestId(Locator locator, String testId) {
        return new AccordionElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
