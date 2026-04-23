package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;

/**
 * Base contract for objects that expose a Playwright {@link Locator}.
 */
public interface HasLocatorElement {
    /** The root locator for the component. */
    Locator getLocator();

    /**
     * Block until Vaadin Flow has no active client-server exchange in flight.
     * Call this after operations that trigger a server roundtrip (e.g. a value
     * change) to ensure the DOM and accessibility tree have settled before the
     * next interaction runs.
     */
    default void waitForVaadinIdle() {
        getLocator().page().waitForFunction(AbstractBasePlaywrightIT.WAIT_FOR_VAADIN_SCRIPT);
    }
}
