package org.vaadin.addons.dramafinder.element;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertionWaitsTest {

    // A Page whose waitForCondition polls the condition a few times, then times
    // out the way Playwright does.
    private static Page pollingPage() {
        return (Page) Proxy.newProxyInstance(AssertionWaitsTest.class.getClassLoader(),
                new Class<?>[] { Page.class }, (proxy, method, args) -> {
                    if (!method.getName().equals("waitForCondition")) {
                        throw new UnsupportedOperationException(method.getName());
                    }
                    BooleanSupplier condition = (BooleanSupplier) args[0];
                    for (int i = 0; i < 3; i++) {
                        if (condition.getAsBoolean()) {
                            return null;
                        }
                    }
                    throw new TimeoutError("Timeout 15000ms exceeded.");
                });
    }

    @Test
    void passesOnceTheValueMatches() {
        AtomicInteger count = new AtomicInteger();
        assertDoesNotThrow(() -> AssertionWaits.assertEventually(pollingPage(), "Row count",
                count::incrementAndGet, 3));
    }

    @Test
    void reportsExpectedAndActualOnMismatch() {
        AssertionError error = assertThrows(AssertionError.class,
                () -> AssertionWaits.assertEventually(pollingPage(), "Row count", () -> 100, 12));
        assertEquals("Row count mismatch. Expected: <12>, actual: <100>", error.getMessage());
        assertInstanceOf(TimeoutError.class, error.getCause());
    }

    @Test
    void reportsExpectedDescriptionForPredicate() {
        AssertionError error = assertThrows(AssertionError.class,
                () -> AssertionWaits.assertEventually(pollingPage(), "Headers",
                        () -> List.of("Name"), headers -> headers.contains("Email"),
                        "a header \"Email\""));
        assertEquals("Headers mismatch. Expected: <a header \"Email\">, actual: <[Name]>",
                error.getMessage());
    }

    @Test
    void reportsUnreadableActualValue() {
        AtomicInteger calls = new AtomicInteger();
        AssertionError error = assertThrows(AssertionError.class,
                () -> AssertionWaits.assertEventually(pollingPage(), "Text", () -> {
                    if (calls.incrementAndGet() > 3) {
                        throw new IllegalStateException("element detached");
                    }
                    return "a";
                }, "b"));
        assertEquals("Text mismatch. Expected: <b>, actual: <unavailable: element detached>",
                error.getMessage());
    }

    @Test
    void reportsExpectationForBooleanCondition() {
        AssertionError error = assertThrows(AssertionError.class,
                () -> AssertionWaits.assertEventually(pollingPage(), "grid row 3 to be selected",
                        () -> false));
        assertEquals("Expected grid row 3 to be selected, but it did not happen before the timeout",
                error.getMessage());
    }

    @Test
    void doesNotWrapOtherFailures() {
        assertThrows(IllegalStateException.class,
                () -> AssertionWaits.assertEventually(pollingPage(), "Text", () -> {
                    throw new IllegalStateException("strict mode violation");
                }, "b"));
    }
}
