package org.vaadin.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public interface HasLabelElement extends HasLocatorElement {

    default Locator getLabelLocator() {
        return getLocator().locator("label").first();
    }

    default String getLabel() {
        return getLabelLocator().textContent();
    }

    default void assertLabel(String label) {
        if (label != null) {
            assertThat(getLabelLocator()).hasText(label);
        } else {
            assertThat(getLabelLocator()).isHidden();
        }
    }

}