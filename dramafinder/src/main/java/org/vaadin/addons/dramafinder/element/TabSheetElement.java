package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@PlaywrightElement(TabSheetElement.FIELD_TAG_NAME)
public class TabSheetElement extends VaadinElement {

    public static final String FIELD_TAG_NAME = "vaadin-tabsheet";

    public TabSheetElement(Locator locator) {
        super(locator);
    }

    public static TabSheetElement get(Page page) {
        return new TabSheetElement(page.locator(FIELD_TAG_NAME).first());
    }

    public void assertTabsCount(int count) {
        Locator tabs = getLocator().locator(TabElement.FIELD_TAG_NAME);
        assertThat(tabs).hasCount(count);
    }

    public TabElement getTab(String label) {
        return TabElement.getTabByText(getLocator(), label);
    }

    public TabElement getSelectedTab() {
        return TabElement.getSelectedTab(getLocator());
    }

    public void selectTab(String label) {
        TabElement tab = getTab(label);
        if (tab != null) {
            tab.select();
        }
    }

    public Locator getContentLocator() {
        return getLocator().locator("[tab]:not([hidden])");
    }
}
