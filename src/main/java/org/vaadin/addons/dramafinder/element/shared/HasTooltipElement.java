package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.TooltipElement;

/**
 * Utilities to interact with components implementing Vaadin's
 * {@code HasTooltip}, i.e. components hosting a slotted
 * {@code <vaadin-tooltip>}.
 * <p>
 * Every method delegates to a {@link TooltipElement}, so the mixin and the
 * standalone element stay one API: use {@link #getTooltip()} whenever more than
 * the text is needed — opened state, position, or triggering the tooltip by
 * hovering its target.
 */
public interface HasTooltipElement extends HasLocatorElement {

    /**
     * The component's tooltip.
     *
     * @return a {@link TooltipElement} for the slotted {@code <vaadin-tooltip>};
     *         its locator matches nothing when the component has no tooltip
     */
    default TooltipElement getTooltip() {
        return TooltipElement.get(getLocator());
    }

    /**
     * Locator for the tooltip content ({@code role=tooltip}).
     *
     * @return the tooltip content locator
     */
    default Locator getTooltipLocator() {
        return getTooltip().getContentLocator();
    }

    /**
     * Tooltip text content.
     *
     * @return the tooltip text, or {@code null} when there is none
     */
    default String getTooltipText() {
        return getTooltip().getText();
    }

    /**
     * Assert the tooltip text.
     *
     * @param text the expected text, or {@code null} to assert the component
     *             shows no tooltip content
     */
    default void assertTooltipHasText(String text) {
        getTooltip().assertText(text);
    }
}
