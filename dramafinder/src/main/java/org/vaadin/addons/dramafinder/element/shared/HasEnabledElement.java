package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasEnabledElement extends HasLocatorElement {

    default Locator getEnabledLocator() {
        return getLocator();
    }

    default boolean isEnabled() {
        return getEnabledLocator().isEnabled();
    }

    default void assertEnabled() {
        assertThat(getEnabledLocator()).isEnabled();
    }

    default void assertDisabled() {
        assertThat(getEnabledLocator()).isDisabled();
    }
}
