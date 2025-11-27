package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasTooltip (slot="tooltip").
 */
public interface HasTooltipElement extends HasLocatorElement {

    /** Locator for the tooltip content (role=tooltip). */
    default Locator getTooltipLocator() {
        return getLocator().locator("*[slot=\"tooltip\"]").first();
    }

    /** Tooltip text content. */
    default String getTooltipText() {
        return getTooltipLocator().textContent();
    }

    default void assertTooltipHasText(String text) {
        if (text != null) {
            assertThat(getTooltipLocator()).hasText(text, new LocatorAssertions.HasTextOptions().setUseInnerText(true));
        } else {
            assertThat(getTooltipLocator()).not().isVisible();
        }
    }
}
