package org.vaadin.dramafinder.element.common;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasPlaceholderElement extends HasLocatorElement {

    default void setPlaceholder(String placeholder) {
        getLocator().evaluate("(el, placeholder) => el.placeholder = placeholder", placeholder);
    }

    default String getPlaceholder() {
        return getLocator().evaluate("el => el.placeholder").toString();
    }

    default void assertPlaceholder(String placeholder) {
        assertThat(getLocator()).hasAttribute("placeholder", placeholder);
    }
}
