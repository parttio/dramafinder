package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-tabsheet>}.
 * <p>
 * Provides helpers to access/select tabs and current content panel.
 */
@PlaywrightElement(TabSheetElement.FIELD_TAG_NAME)
public class TabSheetElement extends VaadinElement {

    public static final String FIELD_TAG_NAME = "vaadin-tabsheet";

    /** Create a {@code TabSheetElement} from an existing locator. */
    public TabSheetElement(Locator locator) {
        super(locator);
    }

    /** Get the first tabsheet instance on the page. */
    public static TabSheetElement get(Page page) {
        return new TabSheetElement(page.locator(FIELD_TAG_NAME).first());
    }

    /** Assert the count of tabs. */
    public void assertTabsCount(int count) {
        Locator tabs = getLocator().locator(TabElement.FIELD_TAG_NAME);
        assertThat(tabs).hasCount(count);
    }

    /** Get a tab by its label. */
    public TabElement getTab(String label) {
        return TabElement.getTabByText(getLocator(), label);
    }

    /** Get the currently selected tab. */
    public TabElement getSelectedTab() {
        return TabElement.getSelectedTab(getLocator());
    }

    /** Select a tab by label text. */
    public void selectTab(String label) {
        TabElement tab = getTab(label);
        if (tab != null) {
            tab.select();
        }
    }

    /** Locator for the currently visible content panel. */
    public Locator getContentLocator() {
        return getLocator().locator("[tab]:not([hidden])");
    }
}
