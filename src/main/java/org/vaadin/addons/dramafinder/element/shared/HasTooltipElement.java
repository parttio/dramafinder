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

    /**
     * Locator for the tooltip content (role=tooltip).
     *
     * @return the tooltip locator
     */
    default Locator getTooltipLocator() {
        return getLocator().getByRole(AriaRole.TOOLTIP,
                new Locator.GetByRoleOptions()
                        .setIncludeHidden(true)).first();
    }

    /**
     * Tooltip text content.
     *
     * @return the tooltip text content
     */
    default String getTooltipText() {
        return getTooltipLocator().textContent();
    }

    /**
     * Assert that the tooltip has the expected text, or is hidden when null.
     *
     * @param text the expected tooltip text, or {@code null} to assert hidden
     */
    default void assertTooltipHasText(String text) {
        if (text != null) {
            assertThat(getTooltipLocator()).hasText(text, new LocatorAssertions.HasTextOptions().setUseInnerText(true));
        } else {
            assertThat(getTooltipLocator()).not().isVisible();
        }
    }
}
