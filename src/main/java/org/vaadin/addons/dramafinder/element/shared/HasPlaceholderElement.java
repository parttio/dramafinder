package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that support the {@code placeholder} attribute.
 */
public interface HasPlaceholderElement extends HasLocatorElement {

    /** Set the {@code placeholder} attribute. */
    default void setPlaceholder(String placeholder) {
        getLocator().evaluate("(el, placeholder) => el.placeholder = placeholder", placeholder);
        waitAfterAction();
    }

    /**
     * Get the current {@code placeholder} text.
     *
     * @return the placeholder, or {@code null} when none is set
     */
    default String getPlaceholder() {
        Object placeholder = getLocator().evaluate("el => el.placeholder");
        return placeholder == null ? null : placeholder.toString();
    }

    /**
     * Assert that the {@code placeholder} matches the expected text.
     *
     * @param placeholder the expected placeholder, or {@code null} to assert that
     *                    the component has no {@code placeholder} attribute
     */
    default void assertPlaceholder(String placeholder) {
        if (placeholder != null) {
            assertThat(getLocator()).hasAttribute("placeholder", placeholder);
        } else {
            assertThat(getLocator()).not().hasAttribute("placeholder", Pattern.compile(".*"));
        }
    }
}
