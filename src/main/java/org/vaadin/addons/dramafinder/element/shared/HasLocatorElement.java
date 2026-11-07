package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.AutoWait;

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
     * <p>
     * Element actions already do this (see {@link AutoWait}); call it yourself
     * after an action on a raw {@link Locator}. It waits even when automatic
     * waiting is off.
     */
    default void waitForVaadinIdle() {
        AutoWait.waitForVaadinIdle(getLocator().page());
    }

    /**
     * Wait for the server round-trip the action just performed may have
     * triggered, unless automatic waiting is off (see {@link AutoWait}). Every
     * element action calls this last; a custom element's actions should too.
     */
    default void waitAfterAction() {
        AutoWait.afterAction(getLocator().page());
    }
}
