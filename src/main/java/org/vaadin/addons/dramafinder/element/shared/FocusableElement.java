package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that can receive keyboard focus.
 */
public interface FocusableElement extends HasLocatorElement {

    /**
     * The locator to focus/blur. Defaults to the component root.
     *
     * @return the focusable locator
     */
    default Locator getFocusLocator() {
        return getLocator();
    }

    /**
     * Focus the component.
     */
    default void focus() {
        getFocusLocator().focus();
    }

    /**
     * Blur the component.
     */
    default void blur() {
        getFocusLocator().blur();
    }

    /**
     * Current tab index as string (from {@code tabIndex} attribute).
     *
     * @return the tab index value
     */
    default String getTabIndex() {
        return getFocusLocator().getAttribute("tabIndex");
    }

    /** Assert that the component has focus. */
    default void assertIsFocused() {
        assertThat(getFocusLocator()).isFocused();
    }

    /** Assert that the component does not have focus. */
    default void assertIsNotFocused() {
        assertThat(getFocusLocator()).not().isFocused();
    }
}
