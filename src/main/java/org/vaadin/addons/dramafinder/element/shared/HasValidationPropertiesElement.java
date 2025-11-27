package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components exposing validation state and error messages.
 */
public interface HasValidationPropertiesElement extends HasLocatorElement {

    /** Locator for the error message slot. */
    default Locator getErrorMessageLocator() {
        return getLocator().locator("> [slot=\"error-message\"]").first(); // slot="helper"
    }

    /** Assert that the component is valid (not {@code invalid}). */
    default void assertValid() {
        assertThat(getLocator()).not().hasAttribute("invalid", "");
    }

    /** Assert that the component is invalid. */
    default void assertInvalid() {
        assertThat(getLocator()).hasAttribute("invalid", "");
    }

    /** Assert that the error message equals the expected text. */
    default void assertErrorMessage(String errorMessage) {
        assertThat(getErrorMessageLocator()).hasText(errorMessage);
    }
}
