package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that support the {@code theme} attribute.
 * <p>
 * Vaadin combines every applied theme variant into a single space-separated
 * {@code theme} attribute, so {@link #assertTheme(String)} — which matches the
 * whole attribute — needs every variant to be listed in the right order. Use
 * {@link #assertHasThemeVariant(String)} to assert a single variant regardless
 * of the others.
 */
public interface HasThemeElement extends HasLocatorElement {

    /** Get the current {@code theme} attribute value. */
    default String getTheme() {
        return getLocator().getAttribute("theme");
    }

    /** Assert that the {@code theme} attribute matches, or is absent when null. */
    default void assertTheme(String theme) {
        if (theme != null) {
            assertThat(getLocator()).hasAttribute("theme", theme);
        } else {
            assertThat(getLocator()).not().hasAttribute("theme", Pattern.compile(".*"));
        }
    }

    /**
     * Assert that the {@code theme} attribute contains the given variant,
     * ignoring any other variants that are also applied.
     *
     * @param variant the theme variant name, e.g. {@code success}
     */
    default void assertHasThemeVariant(String variant) {
        assertThat(getLocator()).hasAttribute("theme", themeVariantPattern(variant));
    }

    /**
     * Assert that the {@code theme} attribute does not contain the given
     * variant. Also passes when the attribute is absent altogether.
     *
     * @param variant the theme variant name, e.g. {@code success}
     */
    default void assertHasNoThemeVariant(String variant) {
        assertThat(getLocator()).not().hasAttribute("theme", themeVariantPattern(variant));
    }

    /**
     * Build a pattern matching {@code variant} as a whole token inside a
     * space-separated attribute value. The escaping is done by hand because
     * Playwright compiles the pattern into a JavaScript {@code RegExp}, which
     * does not understand {@link Pattern#quote(String)}'s {@code \Q...\E}.
     */
    private static Pattern themeVariantPattern(String variant) {
        String quoted = variant.replaceAll("[\\\\^$.|?*+()\\[\\]{}]", "\\\\$0");
        return Pattern.compile("(^|\\s)" + quoted + "($|\\s)");
    }

}
