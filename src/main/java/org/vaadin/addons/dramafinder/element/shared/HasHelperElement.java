package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that provide helper text via the {@code helper} slot.
 */
public interface HasHelperElement extends HasLocatorElement {

    /**
     * Locator for the helper slot content.
     *
     * @return the helper locator
     */
    default Locator getHelperLocator() {
        return getLocator().locator("*[slot=\"helper\"]").first(); // slot="helper"
    }

    /**
     * Text content of the helper slot.
     *
     * @return the helper text content
     */
    default String getHelperText() {
        return getHelperLocator().textContent(); // slot="helper"
    }

    /**
     * Assert that the helper slot has the expected text.
     *
     * @param helperText the expected helper text
     */
    default void assertHelperHasText(String helperText) {
        assertThat(getHelperLocator()).hasText(helperText);
    }
}
