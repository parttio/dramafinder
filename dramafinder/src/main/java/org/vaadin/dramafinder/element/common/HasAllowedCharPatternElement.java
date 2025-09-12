package org.vaadin.dramafinder.element.common;

/**
 * Support for Vaadin's allowedCharPattern on input components.
 * Maps to the DOM attribute "allowed-char-pattern" and property "allowedCharPattern".
 */
public interface HasAllowedCharPatternElement extends HasLocatorElement {

    default String getAllowedCharPattern() {
        return getLocator().evaluate("el => el.allowedCharPattern").toString();
    }

    default void setAllowedCharPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.allowedCharPattern = p", pattern);
    }
}

