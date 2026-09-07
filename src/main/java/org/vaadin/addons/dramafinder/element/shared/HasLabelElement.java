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
     * <p>
     * Uses the child combinator so that composite components (a checkbox group,
     * a date-time picker, ...) resolve their own slotted label instead of the
     * label of the first nested field.
     */
    default Locator getLabelLocator() {
        return getLocator().locator("> [slot=\"label\"]").first();
    }

    /**
     * Get the label text.
     */
    default String getLabel() {
        return getLabelLocator().textContent();
    }

    /**
     * Assert that the label text matches, or is hidden when null.
     */
    default void assertLabel(String label) {
        if (label != null) {
            assertThat(getLabelLocator()).hasText(label, new LocatorAssertions.HasTextOptions().setUseInnerText(true));
        } else {
            assertThat(getLabelLocator()).isHidden();
        }
    }

}
