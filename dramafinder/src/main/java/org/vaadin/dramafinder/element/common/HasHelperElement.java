package org.vaadin.dramafinder.element.common;

import com.microsoft.playwright.Locator;

public interface HasHelperElement extends HasLocatorElement {

    default Locator getHelperTextLocator() {
        return getLocator().locator("*[slot=\"helper\"]").first(); // slot="helper"
    }

    default String getHelperText() {
        return getHelperTextLocator().textContent(); // slot="helper"
    }
}
