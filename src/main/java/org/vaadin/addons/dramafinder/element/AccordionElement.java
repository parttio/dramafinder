package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-accordion>}.
 * <p>
 * Provides helpers to access panels by their summary text and to assert
 * open/closed state and panel count.
 */
@PlaywrightElement(AccordionElement.FIELD_TAG_NAME)
public class AccordionElement extends VaadinElement implements HasStyleElement {

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

}
