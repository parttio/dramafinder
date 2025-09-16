package org.vaadin.dramafinder.element.shared;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasAllowedCharPatternElement extends HasLocatorElement {

    default String getAllowedCharPattern() {
        return getLocator().evaluate("el => el.allowedCharPattern").toString();
    }

    default void setAllowedCharPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.allowedCharPattern = p", pattern);
    }

    default void assertAllowedCharPattern(String pattern) {
        if (pattern != null) {
            assertThat(getLocator()).hasJSProperty("allowedCharPattern", pattern);
        } else {
            assertThat(getLocator()).not().hasAttribute("allowedCharPattern", "");
        }
    }
}
