package org.vaadin.addons.dramafinder.agent;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;

/**
 * Base class for temporary, agent-driven visual verification tests that run
 * against an <strong>already running</strong> application.
 * <p>
 * Unlike the Spring-based integration test base, this class does not boot the
 * application — keeping the app out of the test run is what makes iterations
 * cheap. It launches a Playwright browser, opens a fresh {@link Page} per test
 * at a 1920×1080 viewport, and wires in {@link AgentReporting} so a failure
 * automatically produces a screenshot, semantic snapshot, and stack trace.
 * <p>
 * Typical use (a throwaway {@code AgentVerifyIT}):
 * <pre>{@code
 * class AgentVerifyIT extends VisualVerificationTest {
 *     @Test
 *     void verifyOrders() {
 *         open("orders");
 *         shot("01-order-list");
 *         GridElement.get(page).assertRowCount(12);
 *     }
 * }
 * }</pre>
 * The base URL defaults to {@code http://localhost:8080} and can be overridden
 * with the {@code dramafinder.agent.baseUrl} system property or the
 * {@code DRAMAFINDER_BASE_URL} environment variable. Headless mode follows the
 * same {@code headless} / {@code HEADLESS} convention as the rest of the
 * library.
 */
@ExtendWith(AgentReporting.class)
public abstract class VisualVerificationTest implements AgentReportProvider {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private static Playwright playwright;
    private static Browser browser;

    /** The active page for the current test. */
    protected Page page;

    /** The report writer for the current test. */
    protected AgentReport report;

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new LaunchOptions().setHeadless(isHeadless()));
    }

    @AfterAll
    static void stopBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    @BeforeEach
    void openPage() {
        page = browser.newPage();
        page.setViewportSize(1920, 1080);
        page.setDefaultTimeout(15000);
        report = new AgentReport(page);
    }

    @AfterEach
    void closePage() {
        if (page != null) {
            page.close();
            page = null;
        }
    }

    /**
     * Navigate to a path relative to the configured base URL and wait for
     * Vaadin to finish loading.
     *
     * @param path the route path (with or without a leading slash)
     */
    protected void open(String path) {
        String normalized = path.startsWith("/") ? path.substring(1) : path;
        page.navigate(baseUrl() + "/" + normalized);
        page.waitForFunction(AbstractBasePlaywrightIT.WAIT_FOR_VAADIN_SCRIPT);
    }

    /**
     * Capture a numbered screenshot into the report directory.
     *
     * @param name a short descriptive name for the interaction point
     */
    protected void shot(String name) {
        report.shot(name);
    }

    /**
     * The base URL the test connects to.
     *
     * @return the configured base URL (default {@code http://localhost:8080})
     */
    protected String baseUrl() {
        String property = System.getProperty("dramafinder.agent.baseUrl");
        if (property != null && !property.isBlank()) {
            return trimTrailingSlash(property);
        }
        String env = System.getenv("DRAMAFINDER_BASE_URL");
        if (env != null && !env.isBlank()) {
            return trimTrailingSlash(env);
        }
        return DEFAULT_BASE_URL;
    }

    @Override
    public Page agentPage() {
        return page;
    }

    @Override
    public AgentReport agentReport() {
        return report;
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static boolean isHeadless() {
        String value = System.getProperty("headless");
        if (value == null || value.isBlank()) {
            value = System.getenv("HEADLESS");
        }
        if (value == null || value.isBlank()) {
            return true;
        }
        return Boolean.parseBoolean(value);
    }
}
