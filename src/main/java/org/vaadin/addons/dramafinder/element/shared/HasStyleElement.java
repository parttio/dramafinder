package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components exposing styling via CSS classes.
 */
public interface HasStyleElement extends HasLocatorElement {

    /**
     * Get the raw {@code class} attribute value.
     *
     * @return the class attribute value
     */
    default String getCssClass() {
        return getLocator().getAttribute("class");
    }

    /**
     * Assert the component has exactly the provided class names, or no classes when null.
     *
     * @param classnames the expected class names
     */
    default void assertCssClass(String... classnames) {
        if (classnames != null) {
            assertThat(getLocator()).hasClass(classnames);
        } else {
            assertThat(getLocator()).not().hasClass(Pattern.compile(".*"));
        }
    }

}
