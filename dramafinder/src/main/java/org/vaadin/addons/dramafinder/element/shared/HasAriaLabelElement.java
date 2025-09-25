package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasAriaLabelElement extends HasLocatorElement {

    default Locator getAriaLabelLocator() {
        return getLocator();
    }

    default String getAriaLabel() {
        return getAriaLabelLocator().getAttribute("aria-label");
    }

    default void assertAriaLabel(String ariaLabel) {
        if (ariaLabel != null) {
            assertThat(getAriaLabelLocator()).hasAttribute("aria-label", ariaLabel);
        } else {
            assertThat(getAriaLabelLocator()).not().hasAttribute("aria-label", Pattern.compile(".*"));
        }
    }

}