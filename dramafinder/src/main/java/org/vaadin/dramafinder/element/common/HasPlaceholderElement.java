package org.vaadin.dramafinder.element.common;

public interface HasPlaceholderElement extends HasLocatorElement {

    default void setPlaceholder(String placeholder) {
        getLocator().evaluate("(el, placeholder) => el.placeholder = placeholder", placeholder);
    }

    default String getPlaceholder() {
        return getLocator().evaluate("el => el.placeholder").toString();
    }
}
