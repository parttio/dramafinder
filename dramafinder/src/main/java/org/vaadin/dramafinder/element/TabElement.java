package org.vaadin.dramafinder.element;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(TabElement.FIELD_TAG_NAME)
public class TabElement extends VaadinElement {

    public static final String FIELD_PARENT_TAG_NAME = "vaadin-tabs";
    public static final String FIELD_TAG_NAME = "vaadin-tab";

    public TabElement(Locator locator) {
        super(locator);
    }

    public boolean isSelected() {
        return getLocator().getAttribute("selected") != null;
    }

    public void select() {
        getLocator().click();
    }

    public String getLabel() {
        return getLocator().textContent();
    }

    public void assertSelected() {
        assertThat(getLocator()).hasAttribute("selected", "");
    }

    public void assertNotSelected() {
        assertThat(getLocator()).not().hasAttribute("selected", "");
    }

    public static TabElement getTabByText(Locator locator, String summary) {
        return new TabElement(locator.locator(FIELD_PARENT_TAG_NAME).locator(FIELD_TAG_NAME).getByText(summary));
    }

    public static TabElement getSelectedTab(Locator locator) {
        return new TabElement(locator.locator(FIELD_PARENT_TAG_NAME).locator(FIELD_TAG_NAME + "[selected]"));
    }
}
