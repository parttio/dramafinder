package org.vaadin.addons.dramafinder.tests.it;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.agent.AgentReport;
import org.vaadin.addons.dramafinder.agent.AgentReportProvider;
import org.vaadin.addons.dramafinder.agent.AgentReporting;
import org.vaadin.addons.dramafinder.agent.ComponentSnapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the agent reporting utilities ({@link ComponentSnapshot} and
 * {@link AgentReport}) against a real rendered page.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AgentReportingIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-basic";
    }

    @Test
    public void snapshotSummarisesGrids() {
        String snapshot = ComponentSnapshot.capture(page);

        assertFalse(snapshot.isBlank(), "Snapshot should not be empty");
        assertTrue(snapshot.contains("vaadin-grid"),
                "Snapshot should mention grids: " + snapshot);
        assertTrue(snapshot.contains("columns [First Name, Last Name, Email]"),
                "Snapshot should list the grid columns: " + snapshot);
        assertTrue(snapshot.contains("100 rows"),
                "Snapshot should report the basic grid row count: " + snapshot);
    }

    @Test
    public void snapshotOmitsInternalCellElements() {
        String snapshot = ComponentSnapshot.capture(page);
        assertFalse(snapshot.contains("vaadin-grid-cell-content"),
                "Snapshot should not include internal cell elements: " + snapshot);
    }

    @Test
    public void shotWritesNumberedScreenshot(@org.junit.jupiter.api.io.TempDir Path tempDir)
            throws Exception {
        Path dir = tempDir.resolve("agent-report");
        AgentReport report = new AgentReport(page, dir);

        Path first = report.shot("login");
        Path second = report.shot("order list");

        assertTrue(Files.exists(first), "First screenshot should exist");
        assertTrue(Files.exists(second), "Second screenshot should exist");
        assertEquals("01-login.png", first.getFileName().toString());
        assertEquals("02-order-list.png", second.getFileName().toString());
        assertTrue(Files.size(first) > 0, "Screenshot should not be empty");
    }

    @Test
    public void captureFailureWritesBundle(@org.junit.jupiter.api.io.TempDir Path tempDir)
            throws Exception {
        Path dir = tempDir.resolve("agent-report");
        AgentReport report = new AgentReport(page, dir);

        report.captureFailure(new AssertionError("expected 12 rows but was 100"));

        Path failureTxt = dir.resolve("failure.txt");
        assertTrue(Files.exists(failureTxt), "failure.txt should exist");
        assertTrue(Files.readString(failureTxt).contains("expected 12 rows but was 100"),
                "failure.txt should contain the assertion message");
        assertTrue(Files.exists(dir.resolve("failure.png")),
                "failure.png should exist");

        Path snapshot = dir.resolve("component-snapshot.txt");
        assertTrue(Files.exists(snapshot), "component-snapshot.txt should exist");
        assertTrue(Files.readString(snapshot).contains("vaadin-grid"),
                "component snapshot should describe the page");
    }

    @Test
    public void extensionCapturesFailureFromProvider(
            @org.junit.jupiter.api.io.TempDir Path tempDir) throws Exception {
        Path dir = tempDir.resolve("agent-report");
        AgentReport report = new AgentReport(page, dir);

        // A test instance the extension can resolve the page/report from.
        AgentReportProvider testInstance = new AgentReportProvider() {
            @Override
            public Page agentPage() {
                return page;
            }

            @Override
            public AgentReport agentReport() {
                return report;
            }
        };

        AssertionError failure = new AssertionError("row count mismatch");
        ExtensionContext context = failingContext(testInstance, failure);

        new AgentReporting().afterTestExecution(context);

        assertTrue(Files.exists(dir.resolve("failure.txt")),
                "extension should have written the failure bundle");
        assertTrue(Files.readString(dir.resolve("failure.txt")).contains("row count mismatch"));
    }

    @Test
    public void extensionSkipsWhenTestPassed(
            @org.junit.jupiter.api.io.TempDir Path tempDir) {
        Path dir = tempDir.resolve("agent-report");
        AgentReportProvider testInstance = () -> page;
        ExtensionContext context = failingContext(testInstance, null);

        new AgentReporting().afterTestExecution(context);

        assertFalse(Files.exists(dir), "no report should be written on success");
    }

    @Test
    public void extensionWritesEachFailingTestToItsOwnDirectory() throws Exception {
        Method testMethod = AgentReportingIT.class
                .getMethod("extensionWritesEachFailingTestToItsOwnDirectory");
        Path dir = AgentReport.defaultReportDirectory()
                .resolve("AgentReportingIT")
                .resolve("extensionWritesEachFailingTestToItsOwnDirectory");
        deleteRecursively(dir);
        try {
            // Built-in provider: AbstractBasePlaywrightIT exposes the page.
            ExtensionContext context = failingContext(this,
                    new AssertionError("Grid row count mismatch"), testMethod,
                    "extensionWritesEachFailingTestToItsOwnDirectory()");

            new AgentReporting().afterTestExecution(context);

            Path failureTxt = dir.resolve("failure.txt");
            assertTrue(Files.exists(failureTxt), "failure.txt should be under " + dir);
            assertTrue(Files.readString(failureTxt).contains("Grid row count mismatch"));
            assertTrue(Files.exists(dir.resolve("component-snapshot.txt")));
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void extensionCanBeTurnedOff() throws Exception {
        Method testMethod = AgentReportingIT.class.getMethod("extensionCanBeTurnedOff");
        Path dir = AgentReport.defaultReportDirectory()
                .resolve("AgentReportingIT").resolve("extensionCanBeTurnedOff");
        deleteRecursively(dir);
        System.setProperty("dramafinder.agentReport", "false");
        try {
            ExtensionContext context = failingContext(this, new AssertionError("boom"),
                    testMethod, "extensionCanBeTurnedOff()");

            new AgentReporting().afterTestExecution(context);

            assertFalse(Files.exists(dir), "no report should be written when turned off");
        } finally {
            System.clearProperty("dramafinder.agentReport");
            deleteRecursively(dir);
        }
    }

    private static void deleteRecursively(Path dir) throws Exception {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(p);
            }
        }
    }

    // Minimal ExtensionContext exposing only the execution exception and the
    // test instance, which is all AgentReporting reads.
    private static ExtensionContext failingContext(Object testInstance, Throwable error) {
        return failingContext(testInstance, error, null, null);
    }

    // As above, plus the test class, method and display name AgentReporting
    // uses to pick a per-test report directory.
    private static ExtensionContext failingContext(Object testInstance, Throwable error,
            Method testMethod, String displayName) {
        InvocationHandler handler = (proxy, method, args) -> switch (method.getName()) {
            case "getExecutionException" -> Optional.ofNullable(error);
            case "getTestInstance" -> Optional.ofNullable(testInstance);
            case "getTestClass" -> Optional.ofNullable(
                    testMethod == null ? null : testMethod.getDeclaringClass());
            case "getTestMethod" -> Optional.ofNullable(testMethod);
            case "getDisplayName" -> displayName;
            default -> method.getReturnType() == Optional.class ? Optional.empty() : null;
        };
        return (ExtensionContext) Proxy.newProxyInstance(
                AgentReportingIT.class.getClassLoader(),
                new Class[] { ExtensionContext.class }, handler);
    }
}
