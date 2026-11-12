package org.vaadin.addons.dramafinder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;

/**
 * Collects the errors the browser reports while a test drives a page: messages
 * logged with {@code console.error} and uncaught exceptions (Playwright's
 * {@code pageerror}). Start it with {@link #track(Page)} before the actions you
 * want to watch, then fail the test with {@link #assertNone()}.
 * <p>
 * Unlike checking the Vaadin dev-tools error panel, this works in production
 * mode too. Errors are recorded from the moment {@code track} is called;
 * anything the page logged earlier is not seen.
 */
public final class ClientSideErrors {

    private final Page page;
    private final List<String> errors = new ArrayList<>();
    private final Consumer<ConsoleMessage> consoleListener = this::onConsoleMessage;
    private final Consumer<String> pageErrorListener = this::onPageError;

    private ClientSideErrors(Page page) {
        this.page = page;
        page.onConsoleMessage(consoleListener);
        page.onPageError(pageErrorListener);
    }

    /**
     * Start recording the client-side errors of {@code page}.
     *
     * @param page the page to watch
     * @return the tracker, recording from now on
     */
    public static ClientSideErrors track(Page page) {
        return new ClientSideErrors(page);
    }

    /**
     * Get the errors recorded so far, oldest first: the text of each
     * {@code console.error} message and the message of each uncaught exception.
     *
     * @return a copy of the recorded errors; empty when there are none
     */
    public synchronized List<String> getErrors() {
        return List.copyOf(errors);
    }

    /**
     * Forget the errors recorded so far, e.g. after asserting an expected one;
     * recording continues.
     */
    public synchronized void clear() {
        errors.clear();
    }

    /**
     * Assert that no client-side error has been recorded.
     *
     * @throws AssertionError listing every recorded error, when there is one
     */
    public void assertNone() {
        List<String> recorded = getErrors();
        if (!recorded.isEmpty()) {
            throw new AssertionError("Expected no client-side errors, but the browser reported "
                    + recorded.size() + ":\n  " + String.join("\n  ", recorded));
        }
    }

    /**
     * Stop recording. The errors recorded so far stay available.
     */
    public void stop() {
        page.offConsoleMessage(consoleListener);
        page.offPageError(pageErrorListener);
    }

    private synchronized void onConsoleMessage(ConsoleMessage message) {
        if ("error".equals(message.type())) {
            errors.add(message.text());
        }
    }

    private synchronized void onPageError(String error) {
        errors.add(error);
    }
}
