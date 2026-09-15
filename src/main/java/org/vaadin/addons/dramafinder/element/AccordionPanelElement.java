package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-accordion-panel>}.
 * <p>
 * Offers utilities to toggle open state, read summary and access content.
 */
@PlaywrightElement(AccordionPanelElement.FIELD_TAG_NAME)
public class AccordionPanelElement extends VaadinElement implements HasDisabledAttributeElement {

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

}
