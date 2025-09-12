package org.vaadin.dramafinder.element.common;

import com.microsoft.playwright.Locator;

public interface HasValidationPropertiesElement extends HasLocatorElement {

    default Locator getErrorMessageLocator() {
        return getLocator().locator("*[slot=\"error-message\"]").first(); // slot="helper"
    }
}
