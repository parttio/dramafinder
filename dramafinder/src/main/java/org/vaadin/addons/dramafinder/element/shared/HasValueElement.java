package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasValueElement extends HasLocatorElement {

    default Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]").first(); // slot="helper"
    }

    default String getValue() {
        return getLocator().evaluate("el => el.value").toString();
    }

    /**
     * Set value via JavaScript (ensures the `value-changed` event is triggered)
     */
    default void setValue(String value) {
        getInputLocator().fill(value);
        getLocator().dispatchEvent("change");
    }

    /**
     * Clear the input field
     */
    default void clear() {
        setValue("");
    }

    default void assertValue(String value) {
        assertThat(getInputLocator()).hasValue(value);
    }
}
