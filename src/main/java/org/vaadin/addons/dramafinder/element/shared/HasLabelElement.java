package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that render a visible label.
 */
public interface HasLabelElement extends HasLocatorElement {

    /** Locator for the visible label element. */
    default Locator getLabelLocator() {
        return getLocator().locator("label").first();
    }

    /** Get the label text. */
    default String getLabel() {
        return getLabelLocator().textContent();
    }

    /** Assert that the label text matches, or is hidden when null. */
    default void assertLabel(String label) {
        if (label != null) {
            assertThat(getLabelLocator()).hasText(label);
        } else {
            assertThat(getLabelLocator()).isHidden();
        }
    }

}
