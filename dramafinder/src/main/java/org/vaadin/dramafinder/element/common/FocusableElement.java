package org.vaadin.dramafinder.element.common;

public interface FocusableElement extends HasLocatorElement {

    /** Focus the field */
    default void focus() {
        getLocator().focus();
    }

    /** Blur the field */
    default void blur() {
        getLocator().evaluate("el => el.blur()");
    }

    default String getTabIndex() {
        return getLocator().getAttribute("tabIndex");
    }
}
