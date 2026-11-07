package org.vaadin.addons.dramafinder;

import java.util.Objects;
import java.util.function.Supplier;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

/**
 * Waits for the Vaadin server round-trip that an element action triggers.
 * <p>
 * Every element action that can reach the server — {@code click},
 * {@code setValue}, {@code clear}, {@code check}, {@code select…}, key presses
 * and the like — returns only once Vaadin Flow has no request in flight, so a
 * getter called right after it reads the state the server produced. Playwright
 * already waits for the element to be actionable before the action; this is the
 * other half.
 * <p>
 * Turn it off when a test has to observe the in-flight state, for instance a
 * loading indicator: for one block with {@link #withoutWaiting(Runnable)}, or
 * for a whole run with {@code -Ddramafinder.autoWait=false}. An explicit
 * {@link #waitForVaadinIdle(Page)} always waits.
 * <p>
 * Actions on a raw Playwright {@code Locator} (e.g.
 * {@code element.getLocator().click()}) bypass the element API and are not
 * followed by a wait; call {@link #waitForVaadinIdle(Page)} after them when it
 * matters.
 */
public final class AutoWait {

    /** System property that turns automatic waiting off for the whole run when {@code false}. */
    public static final String PROPERTY = "dramafinder.autoWait";

    private static final ThreadLocal<Integer> SUSPENDED = ThreadLocal.withInitial(() -> 0);

    private AutoWait() {
    }

    /**
     * Whether element actions currently wait for the server round-trip: not
     * switched off with {@code -Ddramafinder.autoWait=false}, and not inside
     * {@link #withoutWaiting(Runnable)} on this thread.
     *
     * @return {@code true} when actions wait
     */
    public static boolean isEnabled() {
        return SUSPENDED.get() == 0
                && !"false".equalsIgnoreCase(System.getProperty(PROPERTY, "true").trim());
    }

    /**
     * Run {@code actions} without waiting for the server round-trip after each
     * element action, so the test can observe the state while a request is in
     * flight. Calls nest; waiting resumes when the outermost call returns.
     *
     * @param actions the element actions to run
     */
    public static void withoutWaiting(Runnable actions) {
        Objects.requireNonNull(actions, "actions");
        withoutWaiting(() -> {
            actions.run();
            return null;
        });
    }

    /**
     * Run {@code actions} without waiting for the server round-trip after each
     * element action, and return their result.
     *
     * @param actions the element actions to run
     * @param <T>     the result type
     * @return what {@code actions} returned
     */
    public static <T> T withoutWaiting(Supplier<T> actions) {
        Objects.requireNonNull(actions, "actions");
        SUSPENDED.set(SUSPENDED.get() + 1);
        try {
            return actions.get();
        } finally {
            SUSPENDED.set(SUSPENDED.get() - 1);
        }
    }

    /**
     * Wait for the round-trip an element action just triggered, unless
     * automatic waiting is off (see {@link #isEnabled()}). Element classes call
     * this after each action; a custom element should too.
     *
     * @param page the page the action ran on
     */
    public static void afterAction(Page page) {
        if (isEnabled()) {
            waitForVaadinIdle(page);
        }
    }

    /**
     * Block until Vaadin Flow has no client-server exchange in flight,
     * regardless of {@link #isEnabled()}.
     * <p>
     * When the action navigated and the page's script context was replaced
     * while waiting, the wait starts again on the new page.
     *
     * @param page the page to wait on
     */
    public static void waitForVaadinIdle(Page page) {
        try {
            page.waitForFunction(AbstractBasePlaywrightIT.WAIT_FOR_VAADIN_SCRIPT);
        } catch (PlaywrightException e) {
            if (!isNavigation(e)) {
                throw e;
            }
            page.waitForLoadState();
            page.waitForFunction(AbstractBasePlaywrightIT.WAIT_FOR_VAADIN_SCRIPT);
        }
    }

    private static boolean isNavigation(PlaywrightException e) {
        String message = String.valueOf(e.getMessage());
        return message.contains("Execution context was destroyed")
                || message.contains("navigation");
    }
}
