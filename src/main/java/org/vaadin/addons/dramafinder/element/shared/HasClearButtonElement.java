package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components with a clear button part.
 */
public interface HasClearButtonElement extends HasLocatorElement {

    /** Locator for the clear button ({@code part~=clear-button}). */
    default Locator getClearButtonLocator() {
        return getLocator().locator("[part=\"clear-button\"]");
    }

    /** Click the clear button. */
    default void clickClearButton() {
        getClearButtonLocator().click();
    }

    /** Whether the clear button is visible. */
    default boolean isClearButtonVisible() {
        return getClearButtonLocator().isVisible();
    }

    /** Assert that the clear button is visible. */
    default void assertClearButtonVisible() {
        assertThat(getClearButtonLocator()).isVisible();
    }

    /** Assert that the clear button is not visible. */
    default void assertClearButtonNotVisible() {
        assertThat(getClearButtonLocator()).not().isVisible();
    }

}
