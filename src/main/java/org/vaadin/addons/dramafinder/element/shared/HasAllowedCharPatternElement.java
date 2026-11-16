package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.TimeoutError;

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
        Object pattern = getLocator().evaluate("el => el.allowedCharPattern");
        return pattern == null ? null : pattern.toString();
    }

    /**
     * Set the {@code allowedCharPattern}.
     *
     * @param pattern the pattern to apply
     */
    default void setAllowedCharPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.allowedCharPattern = p", pattern);
        waitAfterAction();
    }

    /**
     * Assert that the {@code allowedCharPattern} property matches the expected
     * value, retrying until the timeout.
     * <p>
     * Note that some fields have a pattern of their own without one being set:
     * {@code vaadin-integer-field} allows {@code [-+\\d]}.
     *
     * @param pattern expected pattern, or {@code null} to assert that the property
     *                is unset or empty (the field accepts any character)
     */
    default void assertAllowedCharPattern(String pattern) {
        if (pattern != null) {
            assertThat(getLocator()).hasJSProperty("allowedCharPattern", pattern);
        } else {
            try {
                getLocator().page().waitForCondition(() -> {
                    String actual = getAllowedCharPattern();
                    return actual == null || actual.isEmpty();
                });
            } catch (TimeoutError e) {
                throw new AssertionError("allowedCharPattern mismatch. Expected: <unset>, actual: <"
                        + getAllowedCharPattern() + ">", e);
            }
        }
    }
}
