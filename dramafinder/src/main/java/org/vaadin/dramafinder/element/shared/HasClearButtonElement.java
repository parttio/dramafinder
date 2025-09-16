package org.vaadin.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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

    default void assertClearButtonVisible() {
        assertThat(getClearButtonLocator()).isVisible();
    }

    default void assertClearButtonNotVisible() {
        assertThat(getClearButtonLocator()).not().isVisible();
    }

}
