package org.vaadin.dramafinder.element.common;

import com.microsoft.playwright.Locator;

public interface HasClearButtonElement extends HasLocatorElement {

    default Locator getClearButtonLocator() {
        return getLocator().locator("[part=\"clear-button\"]");
    }

    default void clickClearButton() {
        getClearButtonLocator().click();
    }

    default boolean isClearButtonVisible() {
        return getClearButtonLocator().isVisible();
    }
}
