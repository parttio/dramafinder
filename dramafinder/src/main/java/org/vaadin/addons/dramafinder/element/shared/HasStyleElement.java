package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasStyleElement extends HasLocatorElement {

    default String getCssClass() {
        return getLocator().getAttribute("class");
    }

    default void assertCssClass(String... classnames) {
        if (classnames != null) {
            assertThat(getLocator()).hasClass(classnames);
        } else {
            assertThat(getLocator()).not().hasClass(Pattern.compile(".*"));
        }
    }

}