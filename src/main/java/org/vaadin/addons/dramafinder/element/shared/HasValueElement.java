package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that expose a textual {@code value} through an input slot.
 */
public interface HasValueElement extends HasLocatorElement {

    /**
     * Locator for the native input element inside the component.
     *
     * @return the input locator
     */
    default Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]").first(); // slot="helper"
    }

    /**
     * Get the current string value.
     *
     * @return the current value
     */
    default String getValue() {
        return getLocator().evaluate("el => el.value").toString();
    }

    /**
     * Set the field value by filling the input and dispatching a change event.
     *
     * @param value the value to set
     */
    default void setValue(String value) {
        getInputLocator().fill(value);
        getLocator().dispatchEvent("change");
    }

    /**
     * Clear the input value.
     */
    default void clear() {
        setValue("");
    }

    /**
     * Assert that the input value matches the expected string.
     *
     * @param value the expected value
     */
    default void assertValue(String value) {
        assertThat(getInputLocator()).hasValue(value);
    }
}
