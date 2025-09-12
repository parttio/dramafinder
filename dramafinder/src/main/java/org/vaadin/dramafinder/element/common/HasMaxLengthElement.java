package org.vaadin.dramafinder.element.common;

/**
 * Support for maxLength on input components.
 * Maps to the DOM attribute "maxlength" and property "maxLength".
 */
public interface HasMaxLengthElement extends HasLocatorElement {

    default Integer getMaxLength() {
        String v = getLocator().getAttribute("maxlength");
        return v == null ? null : Integer.valueOf(v);
    }

    default void setMaxLength(int max) {
        getLocator().evaluate("(el, v) => el.maxLength = v", max);
    }
}

