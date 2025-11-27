package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components exposing an ARIA label.
 */
public interface HasAriaLabelElement extends HasLocatorElement {

    /** Locator where the {@code aria-label} is applied. Defaults to root. */
    default Locator getAriaLabelLocator() {
        return getLocator();
    }

    /** Get the current {@code aria-label} value. */
    default String getAriaLabel() {
        return getAriaLabelLocator().getAttribute("aria-label");
    }

    /**
     * Assert that the {@code aria-label} matches the expected text, or is absent when null.
     */
    default void assertAriaLabel(String ariaLabel) {
        if (ariaLabel != null) {
            assertThat(getAriaLabelLocator()).hasAttribute("aria-label", ariaLabel);
        } else {
            assertThat(getAriaLabelLocator()).not().hasAttribute("aria-label", Pattern.compile(".*"));
        }
    }

}
