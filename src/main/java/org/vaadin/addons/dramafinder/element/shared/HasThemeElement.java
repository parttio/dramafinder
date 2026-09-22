package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that support the {@code theme} attribute.
 * <p>
 * Vaadin combines every applied theme variant into a single space-separated
 * {@code theme} attribute, in an order that is neither stable nor the order the
 * variants were added. {@link #assertTheme(String)} therefore compares the
 * variants as an unordered set: it still requires the attribute to hold exactly
 * the variants given, but {@code "small primary"} and {@code "primary small"}
 * are interchangeable. Use {@link #assertHasThemeVariant(String)} to assert a
 * single variant regardless of the others.
 */
public interface HasThemeElement extends HasLocatorElement {

    /** Get the current {@code theme} attribute value. */
    default String getTheme() {
        return getLocator().getAttribute("theme");
    }

    /**
     * Assert that the {@code theme} attribute holds exactly the given variants,
     * in any order, or is absent when {@code null}.
     *
     * @param theme space-separated theme variants, or {@code null} to assert
     *              that no {@code theme} attribute is present
     */
    default void assertTheme(String theme) {
        if (theme != null) {
            assertThat(getLocator()).hasAttribute("theme", themeSetPattern(theme));
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
     * Build a pattern matching an attribute value that holds exactly the given
     * variants, in any order: one lookahead per variant, plus a token count so
     * that extra variants still fail the assertion.
     */
    private static Pattern themeSetPattern(String theme) {
        String trimmed = theme.trim();
        if (trimmed.isEmpty()) {
            return Pattern.compile("^\\s*$");
        }
        String[] variants = trimmed.split("\\s+");
        StringBuilder pattern = new StringBuilder("^");
        for (String variant : variants) {
            pattern.append("(?=(?:.*\\s)?").append(escape(variant)).append("(?:\\s.*)?$)");
        }
        return Pattern.compile(pattern.append("(?:\\s*\\S+){").append(variants.length).append("}\\s*$").toString());
    }

    /**
     * Build a pattern matching {@code variant} as a whole token inside a
     * space-separated attribute value.
     */
    private static Pattern themeVariantPattern(String variant) {
        return Pattern.compile("(^|\\s)" + escape(variant) + "($|\\s)");
    }

    /**
     * Escape regex metacharacters by hand, because Playwright compiles the
     * pattern into a JavaScript {@code RegExp}, which does not understand
     * {@link Pattern#quote(String)}'s {@code \Q...\E}.
     */
    private static String escape(String literal) {
        return literal.replaceAll("[\\\\^$.|?*+()\\[\\]{}]", "\\\\$0");
    }

}
