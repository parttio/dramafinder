package org.vaadin.dramafinder.element.common;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasThemeElement extends HasLocatorElement {

    default String getTheme() {
        return getLocator().getAttribute("theme");
    }

    default void assertTheme(String theme) {
        if (theme != null) {
            assertThat(getLocator()).hasAttribute("theme", theme);
        } else {
            assertThat(getLocator()).not().hasAttribute("theme", "");
        }
    }

}