package org.vaadin.addons.dramafinder.agent;

import java.lang.reflect.Field;
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
 * test instance.
 */
public class AgentReporting implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> failure = context.getExecutionException();
        if (failure.isEmpty()) {
            return;
        }
        Object test = context.getTestInstance().orElse(null);
        if (test == null) {
            return;
        }
        locateReport(test).ifPresent(report -> report.captureFailure(failure.get()));
    }

    private Optional<AgentReport> locateReport(Object test) {
        if (test instanceof AgentReportProvider provider) {
            AgentReport report = provider.agentReport();
            if (report != null) {
                return Optional.of(report);
            }
            Page page = provider.agentPage();
            if (page != null) {
                return Optional.of(new AgentReport(page));
            }
        }
        return reflectReport(test);
    }

    // Fallback for @ExtendWith users who don't implement AgentReportProvider:
    // find an AgentReport field, or build one from a Page field.
    private Optional<AgentReport> reflectReport(Object test) {
        AgentReport report = firstFieldValue(test, AgentReport.class);
        if (report != null) {
            return Optional.of(report);
        }
        Page page = firstFieldValue(test, Page.class);
        if (page != null) {
            return Optional.of(new AgentReport(page));
        }
        return Optional.empty();
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
