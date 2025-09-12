package org.vaadin.dramafinder.element.common;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasPrefixAndSuffix (slot="prefix" / slot="suffix").
 */
public interface HasPrefixAndSuffixElement extends HasLocatorElement {

    default Locator getPrefixLocator() {
        return getLocator().locator("*[slot=\"prefix\"]").first();
    }

    default String getPrefixText() {
        return getPrefixLocator().textContent();
    }

    default Locator getSuffixLocator() {
        return getLocator().locator("*[slot=\"suffix\"]").first();
    }

    default String getSuffixText() {
        return getSuffixLocator().textContent();
    }

    default void assertPrefixHasText(String text) {
        if (text != null) {
            assertThat(getPrefixLocator()).hasText(text);
        } else {
            assertThat(getPrefixLocator()).not().isVisible();
        }
    }
    default void assertSuffixHasText(String text) {
        if (text != null) {
            assertThat(getSuffixLocator()).hasText(text);
        } else {
            assertThat(getSuffixLocator()).not().isVisible();
        }
    }
}

