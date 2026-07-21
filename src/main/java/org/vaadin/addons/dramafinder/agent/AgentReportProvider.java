package org.vaadin.addons.dramafinder.agent;

import com.microsoft.playwright.Page;

/**
 * Implemented by tests that want the {@link AgentReporting} extension to write a
 * failure bundle using a specific page and report instance.
 * <p>
 * {@link VisualVerificationTest} implements this for you. Tests that use
 * {@code @ExtendWith(AgentReporting.class)} directly may implement it to control
 * exactly which page is captured; otherwise the extension falls back to
 * reflecting a {@link Page} or {@link AgentReport} field off the test instance.
 */
public interface AgentReportProvider {

    /**
     * The page the extension should screenshot and inspect on failure.
     *
     * @return the active Playwright page, or {@code null} if none is available
     */
    Page agentPage();

    /**
     * The report the extension should write the failure bundle into.
     *
     * @return the report instance, or {@code null} to let the extension create
     *         one from {@link #agentPage()}
     */
    default AgentReport agentReport() {
        return null;
    }
}
