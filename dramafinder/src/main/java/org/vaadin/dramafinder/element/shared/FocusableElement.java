package org.vaadin.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface FocusableElement extends HasLocatorElement {

    default Locator getFocusLocator() {
        return getLocator();
    }

    /**
     * Focus the field
     */
    default void focus() {
        getFocusLocator().focus();
    }

    /**
     * Blur the field
     */
    default void blur() {
        getFocusLocator().blur();
    }

    default String getTabIndex() {
        return getFocusLocator().getAttribute("tabIndex");
    }

    default void assertIsFocused() {
        assertThat(getFocusLocator()).isFocused();
    }

    default void assertIsNotFocused() {
        assertThat(getFocusLocator()).not().isFocused();
    }
}
