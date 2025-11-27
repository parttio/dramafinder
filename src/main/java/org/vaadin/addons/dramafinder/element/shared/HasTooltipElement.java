package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Utilities to interact with components implementing
 * Vaadin's HasTooltip the first child with role tooltip
 */
public interface HasTooltipElement extends HasLocatorElement {

    /** Locator for the tooltip content (role=tooltip). */
    default Locator getTooltipLocator() {
        return getLocator().getByRole(AriaRole.TOOLTIP,
                new Locator.GetByRoleOptions()
                        .setIncludeHidden(true)).first();
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
