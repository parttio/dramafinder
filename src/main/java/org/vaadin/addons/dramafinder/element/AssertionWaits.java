package org.vaadin.addons.dramafinder.element;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;

/**
 * Retrying assertion helpers built on {@link Page#waitForCondition}.
 * <p>
 * A bare {@code waitForCondition} fails with Playwright's generic
 * {@code TimeoutError}, which says neither what was expected nor what the page
 * held. These helpers poll the same way but turn the timeout into an
 * {@link AssertionError} whose message names the checked state, the expected
 * value and the last actual value, so a failing test can be fixed from its
 * output alone. The original {@code TimeoutError} is kept as the cause.
 */
final class AssertionWaits {

    private AssertionWaits() {
    }

    /**
     * Wait until {@code actual} returns a value equal to {@code expected}.
     * <p>
     * Fails with {@code "<what> mismatch. Expected: <x>, actual: <y>"}.
     *
     * @param page     the page to poll on
     * @param what     a short description of the checked state, e.g.
     *                 {@code "Grid row count"}
     * @param actual   reads the current value; re-evaluated on every poll
     * @param expected the expected value ({@code null} allowed)
     * @param <T>      the value type
     */
    static <T> void assertEventually(Page page, String what, Supplier<T> actual, T expected) {
        assertEventually(page, what, actual, value -> Objects.equals(value, expected),
                String.valueOf(expected));
    }

    /**
     * Wait until {@code actual} returns a value accepted by {@code check}.
     * <p>
     * Fails with {@code "<what> mismatch. Expected: <expected>, actual: <y>"}.
     *
     * @param page     the page to poll on
     * @param what     a short description of the checked state
     * @param actual   reads the current value; re-evaluated on every poll
     * @param check    accepts the expected value
     * @param expected a description of what {@code check} accepts, used in the
     *                 failure message
     * @param <T>      the value type
     */
    static <T> void assertEventually(Page page, String what, Supplier<T> actual,
            Predicate<T> check, String expected) {
        try {
            page.waitForCondition(() -> check.test(actual.get()));
        } catch (TimeoutError e) {
            throw new AssertionError(what + " mismatch. Expected: <" + expected
                    + ">, actual: <" + describe(actual) + ">", e);
        }
    }

    /**
     * Wait until {@code condition} holds.
     * <p>
     * Fails with {@code "Expected <expectation>, but it did not happen before the
     * timeout"}; use this only for yes/no state that has no richer actual value
     * to report.
     *
     * @param page        the page to poll on
     * @param expectation what should hold, phrased to follow "Expected", e.g.
     *                    {@code "grid row 3 to be selected"}
     * @param condition   the condition; re-evaluated on every poll
     */
    static void assertEventually(Page page, String expectation, BooleanSupplier condition) {
        try {
            page.waitForCondition(condition);
        } catch (TimeoutError e) {
            throw new AssertionError("Expected " + expectation
                    + ", but it did not happen before the timeout", e);
        }
    }

    private static String describe(Supplier<?> actual) {
        try {
            return String.valueOf(actual.get());
        } catch (RuntimeException e) {
            return "unavailable: " + e.getMessage();
        }
    }
}
