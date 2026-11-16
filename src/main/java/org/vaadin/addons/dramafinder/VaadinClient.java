package org.vaadin.addons.dramafinder;

import java.util.Optional;

import com.microsoft.playwright.Page;

/**
 * Reads the state of the Vaadin Flow client running in a page.
 * <p>
 * The values come from the client's own API on {@code window.Vaadin.Flow.clients},
 * the same object {@link AutoWait} asks whether a request is in flight.
 */
public final class VaadinClient {

    // The UI id of the first Flow client that reports one, or null when the page has none.
    // @formatter:off
    private static final String UI_ID_SCRIPT = "() => {"
            + "  const clients = window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.clients;"
            + "  if (!clients) return null;"
            + "  for (const id of Object.keys(clients)) {"
            + "    const client = clients[id];"
            + "    if (client && typeof client.getUIId === 'function') return client.getUIId();"
            + "  }"
            + "  return null;"
            + "}";
    // @formatter:on

    private VaadinClient() {
    }

    /**
     * Get the id of the Vaadin {@code UI} the page shows, the value of
     * {@code UI.getUIId()} on the server, e.g. to let a test-only endpoint find
     * the matching {@code UI} (fetch it with {@code page.request()}, which shares
     * the browser context's session cookie).
     * <p>
     * Reads once, without waiting: call it after the page has loaded, for
     * instance after {@link AutoWait#waitForVaadinIdle(Page)}. With several
     * Flow clients on the page (embedded applications), the first one's UI id
     * is returned.
     *
     * @param page the Playwright page
     * @return the UI id, or empty when no Flow client runs in the page
     */
    public static Optional<Integer> uiId(Page page) {
        Object id = page.evaluate(UI_ID_SCRIPT);
        return id instanceof Number number ? Optional.of(number.intValue()) : Optional.empty();
    }
}
