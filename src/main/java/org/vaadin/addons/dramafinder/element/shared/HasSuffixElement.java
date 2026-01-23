package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasSuffix (slot="suffix").
 */
public interface HasSuffixElement extends HasLocatorElement {

    /**
     * Locator for the suffix slot content.
     *
     * @return the suffix locator
     */
    default Locator getSuffixLocator() {
        return getLocator().locator("*[slot=\"suffix\"]").first();
    }

    /**
     * Text content of the suffix slot.
     *
     * @return the suffix text content
     */
    default String getSuffixText() {
        return getSuffixLocator().textContent();
    }

    /**
     * Assert that the suffix slot has the expected text, or is hidden when null.
     *
     * @param text the expected text, or {@code null} to assert hidden
     */
    default void assertSuffixHasText(String text) {
        if (text != null) {
            assertThat(getSuffixLocator()).hasText(text);
        } else {
            assertThat(getSuffixLocator()).not().isVisible();
        }
    }
}
