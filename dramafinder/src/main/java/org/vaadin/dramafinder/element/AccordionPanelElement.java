package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(AccordionPanelElement.FIELD_TAG_NAME)
public class AccordionPanelElement extends VaadinElement {

    public static final String FIELD_TAG_NAME = "vaadin-accordion-panel";
    public static final String FIELD_HEADING_TAG_NAME = "vaadin-accordion-heading";

    public AccordionPanelElement(Locator locator) {
        super(locator);
    }

    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    public boolean isOpen() {
        return getLocator().getAttribute("opened") != null;
    }

    public void setOpen(boolean open) {
        if (isOpen() != open) {
            getSummaryLocator().click();
        }
    }

    public Locator getSummaryLocator() {
        return getLocator().locator(FIELD_HEADING_TAG_NAME);
    }

    public String getSummaryText() {
        return getSummaryLocator().textContent();
    }

    public Locator getContentLocator() {
        return getLocator().locator("> [part='content']");
    }

    public void assertContentVisible() {
        assertThat(getContentLocator()).isVisible();
    }

    public void assertContentNotVisible() {
        assertThat(getContentLocator()).not().isVisible();
    }

    public static AccordionPanelElement getAccordionPanelBySummary(Locator locator, String summary) {
        return new AccordionPanelElement(locator.locator(FIELD_TAG_NAME).filter(
                new Locator.FilterOptions().setHas(
                        locator.page().locator(FIELD_HEADING_TAG_NAME,
                                new Page.LocatorOptions().setHasText(summary))
                )));
    }

    public static AccordionPanelElement getOpenedAccordionPanel(Locator locator) {
        return new AccordionPanelElement(locator.locator(FIELD_TAG_NAME).filter(
                new Locator.FilterOptions().setHas(
                        locator.page().locator(FIELD_HEADING_TAG_NAME + "[opened]")
                )));
    }

    public void assertEnabled() {
        assertThat(getLocator()).not().hasAttribute("disabled", "");
    }

    public void assertDisabled() {
        assertThat(getLocator()).hasAttribute("disabled", "");
    }

}
