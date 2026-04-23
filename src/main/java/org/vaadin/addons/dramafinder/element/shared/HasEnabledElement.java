package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that expose enabled/disabled state.
 */
public interface HasEnabledElement extends HasLocatorElement {

    /** Locator used to check enablement. Defaults to root. */
    default Locator getEnabledLocator() {
        return getLocator();
    }

    /** Whether the component is enabled. */
    default boolean isEnabled() {
        return getEnabledLocator().isEnabled();
    }

    /** Whether the component's enabled state matches the expected value. */
    default boolean isEnabled(boolean enabled) {
        return isEnabled() == enabled;
    }

    /** Assert that the component is enabled. */
    default void assertEnabled() {
        assertThat(getEnabledLocator()).isEnabled();
    }

    /** Assert that the component is enabled (true) or disabled (false). */
    default void assertEnabled(boolean enabled) {
        if (enabled) {
            assertEnabled();
        } else {
            assertDisabled();
        }
    }

    /** Assert that the component is disabled. */
    default void assertDisabled() {
        assertThat(getEnabledLocator()).isDisabled();
    }
}
