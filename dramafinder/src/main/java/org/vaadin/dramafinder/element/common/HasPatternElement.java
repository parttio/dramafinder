package org.vaadin.dramafinder.element.common;

/**
 * Support for pattern and preventInvalidInput on input components.
 * Maps to the DOM attribute "pattern" and boolean attribute "prevent-invalid-input".
 */
public interface HasPatternElement extends HasLocatorElement {

    default String getPattern() {
        return getLocator().getAttribute("pattern");
    }

    default void setPattern(String pattern) {
        getLocator().evaluate("(el, p) => el.pattern = p", pattern);
    }

    default boolean isPreventInvalidInput() {
        return getLocator().getAttribute("prevent-invalid-input") != null;
    }

    default void setPreventInvalidInput(boolean prevent) {
        getLocator().evaluate("(el, p) => el.preventInvalidInput = p", prevent);
    }
}

