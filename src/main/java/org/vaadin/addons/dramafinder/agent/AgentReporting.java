package org.vaadin.addons.dramafinder.agent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Optional;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that, on test failure, writes an agent-oriented report to
 * {@code target/agent-report/} (Maven) or {@code build/agent-report/} (Gradle):
 * a {@code failure.txt} with the assertion message and a trimmed stack trace, a
 * screenshot of the page at failure time, and a semantic
 * {@link ComponentSnapshot component snapshot}.
 * <p>
 * The capture runs in {@link AfterTestExecutionCallback}, i.e. <em>before</em>
 * {@code @AfterEach} closes the page, so the screenshot and snapshot reflect the
 * exact failing state.
 * <p>
 * Register it either by extending {@link VisualVerificationTest} (which already carries
 * {@code @ExtendWith(AgentReporting.class)} and implements
 * {@link AgentReportProvider}) or by annotating any Playwright test with
 * {@code @ExtendWith(AgentReporting.class)}. In the latter case the extension
 * locates the page/report by implementing {@link AgentReportProvider}, or, as a
 * fallback, by reflecting an {@link AgentReport} or {@link Page} field off the
 * test instance. {@code AbstractBasePlaywrightIT} registers it too, so every
 * integration test built on it gets a failure bundle without extra setup.
 * <p>
 * When the extension builds the report itself (the test supplies a page but no
 * {@link AgentReport}), it writes into a per-test subdirectory,
 * {@code agent-report/<TestClass>/<testMethod>/}, so several failing tests in one
 * run do not overwrite each other. A report supplied by the test, such as the one
 * {@link VisualVerificationTest} creates, keeps its own directory.
 * <p>
 * Set the {@code dramafinder.agentReport} system property or the
 * {@code DRAMAFINDER_AGENT_REPORT} environment variable to {@code false} to turn
 * the capture off.
 */
public class AgentReporting implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> failure = context.getExecutionException();
        if (failure.isEmpty() || !isEnabled()) {
            return;
        }
        Object test = context.getTestInstance().orElse(null);
        if (test == null) {
            return;
        }
        Path directory = reportDirectory(context);
        locateReport(test, directory)
                .ifPresent(report -> report.captureFailure(failure.get()));
    }

    /**
     * Whether failure capture is on: it is unless the
     * {@code dramafinder.agentReport} system property or, when that is unset, the
     * {@code DRAMAFINDER_AGENT_REPORT} environment variable is {@code false}.
     *
     * @return {@code true} if failures should be captured
     */
    static boolean isEnabled() {
        String value = System.getProperty("dramafinder.agentReport");
        if (value == null || value.isBlank()) {
            value = System.getenv("DRAMAFINDER_AGENT_REPORT");
        }
        return value == null || value.isBlank() || !"false".equalsIgnoreCase(value.trim());
    }

    private Optional<AgentReport> locateReport(Object test, Path directory) {
        if (test instanceof AgentReportProvider provider) {
            AgentReport report = provider.agentReport();
            if (report != null) {
                return Optional.of(report);
            }
            Page page = provider.agentPage();
            if (page != null) {
                return Optional.of(new AgentReport(page, directory));
            }
        }
        return reflectReport(test, directory);
    }

    // Fallback for @ExtendWith users who don't implement AgentReportProvider:
    // find an AgentReport field, or build one from a Page field.
    private Optional<AgentReport> reflectReport(Object test, Path directory) {
        AgentReport report = firstFieldValue(test, AgentReport.class);
        if (report != null) {
            return Optional.of(report);
        }
        Page page = firstFieldValue(test, Page.class);
        if (page != null) {
            return Optional.of(new AgentReport(page, directory));
        }
        return Optional.empty();
    }

    // agent-report/<TestClass>/<testMethod>[-<invocation>]/, or agent-report/
    // itself when the context does not name a test method.
    private static Path reportDirectory(ExtensionContext context) {
        Path base = AgentReport.defaultReportDirectory();
        Optional<Class<?>> testClass = context.getTestClass();
        Optional<Method> testMethod = context.getTestMethod();
        if (testClass.isEmpty() || testMethod.isEmpty()) {
            return base;
        }
        String methodName = testMethod.get().getName();
        String displayName = context.getDisplayName();
        String leaf = methodName;
        // Parameterized and repeated invocations get a display name of their
        // own ("[1] foo", "repetition 2 of 3"); keep them apart.
        if (displayName != null && !displayName.startsWith(methodName + "(")) {
            leaf = methodName + "-" + displayName;
        }
        return base.resolve(sanitize(testClass.get().getSimpleName())).resolve(sanitize(leaf));
    }

    private static String sanitize(String name) {
        String cleaned = name.trim().replaceAll("[^A-Za-z0-9._-]+", "-");
        return cleaned.length() > 100 ? cleaned.substring(0, 100) : cleaned;
    }

    @SuppressWarnings("unchecked")
    private <T> T firstFieldValue(Object test, Class<T> type) {
        for (Class<?> c = test.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (type.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(test);
                        if (value != null) {
                            return (T) value;
                        }
                    } catch (ReflectiveOperationException | RuntimeException ignore) {
                        // Skip inaccessible fields.
                    }
                }
            }
        }
        return null;
    }
}
