package org.vaadin.dramafinder.element.common;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasValidationPropertiesElement extends HasLocatorElement {

    default Locator getErrorMessageLocator() {
        return getLocator().locator("*[slot=\"error-message\"]").first(); // slot="helper"
    }

    default void assertValid() {
        assertThat(getLocator()).not().hasAttribute("invalid", "");
    }
    default void assertInvalid() {
        assertThat(getLocator()).hasAttribute("invalid", "");
    }

    default void assertErrorMessage(String errorMessage) {
        assertThat(getErrorMessageLocator()).hasText(errorMessage);
    }
}
