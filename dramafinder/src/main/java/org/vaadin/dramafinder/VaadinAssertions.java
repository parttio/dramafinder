package org.vaadin.dramafinder;

import org.vaadin.dramafinder.element.common.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class VaadinAssertions {

    public static void assertValid(HasValidationPropertiesElement element) {
        assertThat(element.getLocator()).not().hasAttribute("invalid", "");
    }
    public static void assertInvalid(HasValidationPropertiesElement element) {
        assertThat(element.getLocator()).hasAttribute("invalid", "");
    }
}
