package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasPrefix(slot="prefix").
 */
public interface HasPrefixElement extends HasLocatorElement {

    /** Locator for the prefix slot content. */
    default Locator getPrefixLocator() {
        return getLocator().locator("*[slot=\"prefix\"]").first();
    }

    /** Text content of the prefix slot. */
    default String getPrefixText() {
        return getPrefixLocator().textContent();
    }

    /** Assert that the prefix slot has the expected text, or is hidden when null. */
    default void assertPrefixHasText(String text) {
        if (text != null) {
            assertThat(getPrefixLocator()).hasText(text);
        } else {
            assertThat(getPrefixLocator()).not().isVisible();
        }
    }
}
