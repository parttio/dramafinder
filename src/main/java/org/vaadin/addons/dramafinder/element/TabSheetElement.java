package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * PlaywrightElement for {@code <vaadin-tabsheet>}.
 * <p>
 * Provides helpers to access/select tabs and current content panel. Tab lookup
 * and selection are delegated to the {@link TabsElement} of the embedded
 * {@code <vaadin-tabs>} strip.
 */
@PlaywrightElement(TabSheetElement.FIELD_TAG_NAME)
public class TabSheetElement extends VaadinElement {

    public static final String FIELD_TAG_NAME = "vaadin-tabsheet";

    private final TabsElement tabsElement;

    /** Create a {@code TabSheetElement} from an existing locator. */
    public TabSheetElement(Locator locator) {
        super(locator);
        this.tabsElement = TabsElement.get(locator);
    }

    /** Get the first tabsheet instance on the page. */
    public static TabSheetElement get(Page page) {
        return new TabSheetElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the embedded tab strip.
     *
     * @return the {@code TabsElement} for this tabsheet's {@code <vaadin-tabs>}
     */
    public TabsElement getTabsElement() {
        return tabsElement;
    }

    /** Assert the count of tabs. */
    public void assertTabsCount(int count) {
        tabsElement.assertTabCount(count);
    }

    /** Get a tab by its label. */
    public TabElement getTab(String label) {
        return tabsElement.getTab(label);
    }

    /** Get the currently selected tab. */
    public TabElement getSelectedTab() {
        return tabsElement.getSelectedTab();
    }

    /** Select a tab by label text. */
    public void selectTab(String label) {
        tabsElement.selectTab(label);
    }

    /** Select a tab by its zero-based index. */
    public void selectTab(int index) {
        tabsElement.selectTab(index);
    }

    /** Assert that the tab with the given label is selected. */
    public void assertSelectedTab(String label) {
        tabsElement.assertSelectedTab(label);
    }

    /** Locator for the currently visible content panel. */
    public Locator getContentLocator() {
        return getLocator().locator("[tab]:not([hidden])");
    }
}
