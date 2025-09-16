package org.vaadin.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasSuffix (slot="suffix").
 */
public interface HasSuffixElement extends HasLocatorElement {

    default Locator getSuffixLocator() {
        return getLocator().locator("*[slot=\"suffix\"]").first();
    }

    default String getSuffixText() {
        return getSuffixLocator().textContent();
    }

    default void assertSuffixHasText(String text) {
        if (text != null) {
            assertThat(getSuffixLocator()).hasText(text);
        } else {
            assertThat(getSuffixLocator()).not().isVisible();
        }
    }
}

