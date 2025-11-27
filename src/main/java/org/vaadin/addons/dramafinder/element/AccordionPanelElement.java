package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-accordion-panel>}.
 * <p>
 * Offers utilities to toggle open state, read summary and access content.
 */
@PlaywrightElement(AccordionPanelElement.FIELD_TAG_NAME)
public class AccordionPanelElement extends VaadinElement {

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

    /** Get an accordion panel by its summary text within a scope. */
    public static AccordionPanelElement getAccordionPanelBySummary(Locator locator, String summary) {
        return new AccordionPanelElement(locator.locator(FIELD_TAG_NAME).filter(
                new Locator.FilterOptions().setHas(
                        locator.page().locator(FIELD_HEADING_TAG_NAME,
                                new Page.LocatorOptions().setHasText(summary))
                )));
    }

    /** Get the currently opened accordion panel within a scope. */
    public static AccordionPanelElement getOpenedAccordionPanel(Locator locator) {
        return new AccordionPanelElement(locator.locator(FIELD_TAG_NAME).filter(
                new Locator.FilterOptions().setHas(
                        locator.page().locator(FIELD_HEADING_TAG_NAME + "[opened]")
                )));
    }

    /** Assert that the panel is enabled. */
    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", "");
    }

    /** Assert that the panel is disabled. */
    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

}
