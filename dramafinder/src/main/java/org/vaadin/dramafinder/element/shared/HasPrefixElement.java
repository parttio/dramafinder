package org.vaadin.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasPrefix(slot="prefix").
 */
public interface HasPrefixElement extends HasLocatorElement {

    default Locator getPrefixLocator() {
        return getLocator().locator("*[slot=\"prefix\"]").first();
    }

    default String getPrefixText() {
        return getPrefixLocator().textContent();
    }

    default void assertPrefixHasText(String text) {
        if (text != null) {
            assertThat(getPrefixLocator()).hasText(text);
        } else {
            assertThat(getPrefixLocator()).not().isVisible();
        }
    }
}

