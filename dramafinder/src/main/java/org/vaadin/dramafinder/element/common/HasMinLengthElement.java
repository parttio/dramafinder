package org.vaadin.dramafinder.element.common;

/**
 * Support for minLength on input components.
 * Maps to the DOM attribute "minlength" and property "minLength".
 */
public interface HasMinLengthElement extends HasLocatorElement {

    default Integer getMinLength() {
        String v = getLocator().getAttribute("minlength");
        return v == null ? null : Integer.valueOf(v);
    }

    default void setMinLength(int min) {
        getLocator().evaluate("(el, v) => el.minLength = v", min);
    }
}

