package org.vaadin.addons.dramafinder.element.shared;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that support the {@code placeholder} attribute.
 */
public interface HasPlaceholderElement extends HasLocatorElement {

    /**
     * Set the {@code placeholder} attribute.
     *
     * @param placeholder the placeholder text to set
     */
    default void setPlaceholder(String placeholder) {
        getLocator().evaluate("(el, placeholder) => el.placeholder = placeholder", placeholder);
    }

    /**
     * Get the current {@code placeholder} text.
     *
     * @return the placeholder text
     */
    default String getPlaceholder() {
        return getLocator().evaluate("el => el.placeholder").toString();
    }

    /**
     * Assert that the {@code placeholder} matches the expected text.
     *
     * @param placeholder the expected placeholder text
     */
    default void assertPlaceholder(String placeholder) {
        assertThat(getLocator()).hasAttribute("placeholder", placeholder);
    }
}
