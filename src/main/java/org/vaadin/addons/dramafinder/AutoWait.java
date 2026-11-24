package org.vaadin.addons.dramafinder;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;

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

    /**
     * How long to wait for a pending navigation before giving up, so a
     * navigation that never completes (one a {@code BeforeLeaveEvent}
     * postponed) does not stall every action.
     */
    private static final int NAVIGATION_TIMEOUT_MS = 2000;

    /**
     * How long to wait for Flow to go idle before failing. The script runs in
     * the page, where Playwright's default timeout does not apply.
     */
    private static final int IDLE_TIMEOUT_MS = 30_000;

    /**
     * How long to wait for a document load the page has started to replace
     * the page, before treating the load as abandoned (a {@code 204} response,
     * a cancelled load) and going on with the idle check.
     */
    private static final int LEAVING_TIMEOUT_MS = 5000;

    /** How many times in a row the wait starts again on a page that replaced the previous one. */
    private static final int MAX_PAGE_REPLACEMENTS = 5;

    /**
     * Resolves once Flow is idle and no navigation is pending, in one browser
     * call. {@code window.Vaadin.Flow.navigation} is true while Flow hands a
     * server-side {@code ui.navigate(..)} to React Router; Flow deletes it once
     * the address bar shows the new location (Flow internal, not an API). A
     * navigation pending for {@code navigationTimeout} ms is given up on. A
     * settled state is checked again one task later, so a navigation that
     * starts right after the client goes idle is not missed.
     * <p>
     * It also installs, once per page, a Navigation API listener that flags a
     * load of another document (not same-document, not a download). While the
     * flag is set the script keeps waiting, so the new document replaces the
     * page's script context and the evaluation fails with "Execution context
     * was destroyed"; after {@code leavingTimeout} ms the flag is cleared and
     * the idle check goes on. Browsers without the Navigation API never set
     * it. A page that has not loaded yet is not settled either: Flow starts
     * during the load, and Playwright can run the script in the new document
     * before its load state has caught up. Resolves to {@code false} after {@code timeout} ms.
     */
    private static final String SETTLE_SCRIPT =
            // @formatter:off
            "async ([timeout, navigationTimeout, leavingTimeout]) => {"
            + "if (window.navigation && !window.__dramafinderLeavingListener) {"
            + "  window.__dramafinderLeavingListener = e => {"
            + "    if (!e.destination.sameDocument && e.downloadRequest === null) {"
            + "      window.__dramafinderLeavingSince = performance.now();"
            + "    }"
            + "  };"
            + "  window.navigation.addEventListener('navigate', window.__dramafinderLeavingListener);"
            + "}"
            + "const leaving = () => {"
            + "  const since = window.__dramafinderLeavingSince;"
            + "  if (since === undefined) {"
            + "    return false;"
            + "  }"
            + "  if (performance.now() - since < leavingTimeout) {"
            + "    return true;"
            + "  }"
            + "  delete window.__dramafinderLeavingSince;"
            + "  return false;"
            + "};"
            + "const idle = " + AbstractBasePlaywrightIT.WAIT_FOR_VAADIN_SCRIPT + ";"
            + "const start = performance.now();"
            + "let navigationSeenAt;"
            + "const navigationSettled = () => {"
            + "  if (!(window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.navigation)) {"
            + "    navigationSeenAt = undefined;"
            + "    return true;"
            + "  }"
            + "  if (navigationSeenAt === undefined) {"
            + "    navigationSeenAt = performance.now();"
            + "  }"
            + "  return performance.now() - navigationSeenAt >= navigationTimeout;"
            + "};"
            + "const settled = () => document.readyState === 'complete' && !leaving() && navigationSettled() && idle();"
            + "const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));"
            + "while (performance.now() - start < timeout) {"
            + "  if (settled()) {"
            + "    await sleep(0);"
            + "    if (settled()) {"
            + "      return true;"
            + "    }"
            + "  }"
            + "  await sleep(5);"
            + "}"
            + "return false;"
            + "}";
    // @formatter:on

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
     * When the server navigated with {@code ui.navigate(..)}, it also waits,
     * up to two seconds, for the address bar to show the new location, which
     * Flow (in React mode, Vaadin's default) updates only after it has handled
     * the response. When the action started loading another document (a
     * {@code setLocation(..)} from the server, a plain link, a form post), it
     * waits for that document to replace the page, then starts again on the
     * new page, where it also waits for Flow to start its UI. A page with no
     * Flow is idle once loaded. This needs the browser's Navigation API
     * (Chromium); elsewhere a load the action started is only waited for when
     * it replaces the page during the wait. Only loads that start after a
     * first wait on the page are seen.
     * <p>
     * The wait is one browser call when nothing is pending. It runs in the
     * page, so Playwright's default timeout does not apply: it has its own
     * limit of 30 seconds.
     *
     * @param page the page to wait on
     * @throws TimeoutError when Flow is still busy after 30 seconds
     */
    public static void waitForVaadinIdle(Page page) {
        for (int attempt = 1; ; attempt++) {
            try {
                waitForIdleAndNavigation(page);
                return;
            } catch (PlaywrightException e) {
                // Playwright can report the old context destroyed before the
                // frame has switched to the new document, so the wait right
                // after a load may land on the old one too.
                if (!isNavigation(e) || attempt == MAX_PAGE_REPLACEMENTS) {
                    throw e;
                }
                page.waitForLoadState();
            }
        }
    }

    private static void waitForIdleAndNavigation(Page page) {
        Object settled = page.evaluate(SETTLE_SCRIPT, List.of(IDLE_TIMEOUT_MS, NAVIGATION_TIMEOUT_MS, LEAVING_TIMEOUT_MS));
        if (!Boolean.TRUE.equals(settled)) {
            throw new TimeoutError("Vaadin Flow did not become idle within " + IDLE_TIMEOUT_MS + " ms");
        }
    }

    private static boolean isNavigation(PlaywrightException e) {
        String message = String.valueOf(e.getMessage());
        return message.contains("Execution context was destroyed")
                || message.contains("navigation");
    }
}
