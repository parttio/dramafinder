package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for tabs {@code <vaadin-tab>}.
 */
@PlaywrightElement(TabElement.FIELD_TAG_NAME)
public class TabElement extends VaadinElement {

    public static final String FIELD_PARENT_TAG_NAME = "vaadin-tabs";
    public static final String FIELD_TAG_NAME = "vaadin-tab";

    /** Create a {@code TabElement} from an existing locator. */
    public TabElement(Locator locator) {
        super(locator);
    }

    /** Whether the tab is currently selected. */
    public boolean isSelected() {
        return getLocator().getAttribute("selected") != null;
    }

    /** Select the tab by clicking it. */
    public void select() {
        getLocator().click();
    }

    /** Get the tab label text. */
    public String getLabel() {
        return getLocator().textContent();
    }

    /** Assert that the tab is selected. */
    public void assertSelected() {
        assertThat(getLocator()).hasAttribute("selected", "");
    }

    /** Assert that the tab is not selected. */
    public void assertNotSelected() {
        assertThat(getLocator()).not().hasAttribute("selected", "");
    }

    /** Get a tab by visible text within a scope. */
    public static TabElement getTabByText(Locator locator, String summary) {
        return new TabElement(locator.locator(FIELD_PARENT_TAG_NAME).locator(FIELD_TAG_NAME).getByText(summary));
    }

    /** Get the currently selected tab within a scope. */
    public static TabElement getSelectedTab(Locator locator) {
        return new TabElement(locator.locator(FIELD_PARENT_TAG_NAME).locator(FIELD_TAG_NAME + "[selected]"));
    }
}
