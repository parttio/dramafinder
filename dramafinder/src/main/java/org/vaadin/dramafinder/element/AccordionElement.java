package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.dramafinder.element.shared.HasStyleElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(AccordionElement.FIELD_TAG_NAME)
public class AccordionElement extends VaadinElement implements HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-accordion";

    public AccordionElement(Locator locator) {
        super(locator);
    }

    public void assertPanelCount(int count) {
        Locator panels = getLocator().locator(AccordionPanelElement.FIELD_TAG_NAME);
        assertThat(panels).hasCount(count);
    }

    public AccordionPanelElement getPanel(String summary) {
        return AccordionPanelElement.getAccordionPanelBySummary(getLocator(), summary);
    }

    public void openPanel(String summary) {
        getPanel(summary).setOpen(true);
    }

    public void closePanel(String summary) {
        getPanel(summary).setOpen(false);
    }

    public boolean isPanelOpened(String summary) {
        return getPanel(summary).isOpen();
    }

    public void assertPanelOpened(String summary) {
        getPanel(summary).assertOpened();
    }

    public void assertPanelClosed(String summary) {
        getPanel(summary).assertClosed();
    }

    public AccordionPanelElement getOpenedPanel() {
        return AccordionPanelElement.getOpenedAccordionPanel(getLocator());
    }

}
