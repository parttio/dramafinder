package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;

/**
 * Base contract for objects that expose a Playwright {@link Locator}.
 */
public interface HasLocatorElement {
    /**
     * The root locator for the component.
     *
     * @return the root locator
     */
    Locator getLocator();
}
