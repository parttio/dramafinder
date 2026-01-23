package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that render a visible label.
 */
public interface HasLabelElement extends HasLocatorElement {

    /**
     * Locator for the visible label element.
     *
     * @return the label locator
     */
    default Locator getLabelLocator() {
        return getLocator().locator("label").first();
    }

    /**
     * Get the label text.
     *
     * @return the label text content
     */
    default String getLabel() {
        return getLabelLocator().textContent();
    }

    /**
     * Assert that the label text matches, or is hidden when null.
     *
     * @param label the expected label text, or {@code null} to assert hidden
     */
    default void assertLabel(String label) {
        if (label != null) {
            assertThat(getLabelLocator()).hasText(label, new LocatorAssertions.HasTextOptions().setUseInnerText(true));
        } else {
            assertThat(getLabelLocator()).isHidden();
        }
    }

}
