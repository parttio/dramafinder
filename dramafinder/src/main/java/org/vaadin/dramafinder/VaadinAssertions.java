package org.vaadin.dramafinder;

import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;

public final class VaadinAssertions {

    private VaadinAssertions() {
        throw new IllegalStateException("Utility class");
    }

    public static void assertValid(HasValidationPropertiesElement element) {
        element.assertValid();
    }

    public static void assertInvalid(HasValidationPropertiesElement element) {
        element.assertInvalid();
    }
}
