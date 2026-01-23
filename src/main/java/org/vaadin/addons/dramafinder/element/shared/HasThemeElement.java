package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that support the {@code theme} attribute.
 */
public interface HasThemeElement extends HasLocatorElement {

    /**
     * Get the current {@code theme} attribute value.
     *
     * @return the theme attribute value
     */
    default String getTheme() {
        return getLocator().getAttribute("theme");
    }

    /**
     * Assert that the {@code theme} attribute matches, or is absent when null.
     *
     * @param theme the expected theme, or {@code null} to assert absence
     */
    default void assertTheme(String theme) {
        if (theme != null) {
            assertThat(getLocator()).hasAttribute("theme", theme);
        } else {
            assertThat(getLocator()).not().hasAttribute("theme", Pattern.compile(".*"));
        }
    }

}
