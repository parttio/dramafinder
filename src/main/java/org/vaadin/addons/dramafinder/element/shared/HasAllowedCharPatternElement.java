package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components supporting {@code allowedCharPattern} to constrain input.
 */
public interface HasAllowedCharPatternElement extends HasLocatorElement {

    /**
     * Get the current {@code allowedCharPattern}.
     *
     * @return the pattern string or {@code null}
     */
    default String getAllowedCharPattern() {
        return getLocator().evaluate("el => el.allowedCharPattern").toString();
    }

    /**
     * Set the {@code allowedCharPattern}.
     *
     * @param pattern the pattern to apply
     */
    default void setAllowedCharPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.allowedCharPattern = p", pattern);
    }

    /**
     * Assert that the {@code allowedCharPattern} matches the expected value.
     *
     * @param pattern expected pattern, or {@code null} to assert absence
     */
    default void assertAllowedCharPattern(String pattern) {
        if (pattern != null) {
            assertThat(getLocator()).hasJSProperty("allowedCharPattern", pattern);
        } else {
            assertThat(getLocator()).not().hasAttribute("allowedCharPattern", Pattern.compile(".*"));
        }
    }
}
