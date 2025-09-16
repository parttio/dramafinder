package org.vaadin.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasHelperElement extends HasLocatorElement {

    default Locator getHelperLocator() {
        return getLocator().locator("*[slot=\"helper\"]").first(); // slot="helper"
    }

    default String getHelperText() {
        return getHelperLocator().textContent(); // slot="helper"
    }

    default void assertHelperHasText(String helperText) {
        assertThat(getHelperLocator()).hasText(helperText);
    }
}
