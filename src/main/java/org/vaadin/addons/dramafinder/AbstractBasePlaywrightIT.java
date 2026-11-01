package org.vaadin.addons.dramafinder;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.vaadin.addons.dramafinder.agent.AgentReportProvider;
import org.vaadin.addons.dramafinder.agent.AgentReporting;

/**
 * Base class for Playwright integration tests of a Vaadin view.
 * <p>
 * Launches one Chromium browser per test class (headless unless the
 * {@code headless} system property or {@code HEADLESS} environment variable says
 * otherwise), opens a fresh {@link #page} before each test, navigates it to
 * {@code getUrl() + getView()} and waits for Vaadin to go idle.
 * <p>
 * Every failing test writes an {@link AgentReporting agent report} to
 * {@code target/agent-report/<TestClass>/<testMethod>/} ({@code build/} for
 * Gradle): {@code failure.txt} with the assertion message and a trimmed stack
 * trace, {@code failure.png} with the page at failure time, and
 * {@code component-snapshot.txt} describing the Vaadin components on the page.
 * Set {@code -Ddramafinder.agentReport=false} to turn it off.
 */
@ExtendWith(AgentReporting.class)
public abstract class AbstractBasePlaywrightIT implements HasTestView, AgentReportProvider {

    public static final String WAIT_FOR_VAADIN_SCRIPT =
            // @formatter:off
            "() => {"
            + "if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.clients) {"
            + "  var clients = window.Vaadin.Flow.clients;"
            + "  for (var client in clients) {"
            + "    if (clients[client].isActive()) {"
            + "      return false;"
            + "    }"
            + "  }"
            + "  return true;"
            + "} else if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.devServerIsNotLoaded) {"
            + "  return false;"
            + "} else {"
            + "  return true;"
            + "}"
            + "}";
    // @formatter:on

    protected Page page;
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browser = new ThreadLocal<>();

    @BeforeEach
    public void setupTest() throws Exception {
        page = browser.get().newPage();
        page.navigate(getUrl() + getView());
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
        page.setDefaultNavigationTimeout(4000);
        page.setDefaultTimeout(15000);
    }

    @AfterEach
    public void cleanupTest() {
        page.close();
    }

    @AfterAll
    public static void cleanup() {
        Browser b = browser.get();
        Playwright p = playwright.get();
        if (b != null) {
            b.close();
            browser.remove();
        }
        if (p != null) {
            p.close();
            playwright.remove();
        }
    }

    @BeforeAll
    public static void setup() {
        Playwright p = Playwright.create();
        playwright.set(p);
        browser.set(p.chromium().launch(new LaunchOptions()
                .setHeadless(isHeadless())));
    }

    protected Page getPage() {
        return page;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the test's {@code page}, so a failing test is captured in the
     * state it failed in.
     */
    @Override
    public Page agentPage() {
        return page;
    }

    protected Browser getBrowser() {
        return browser.get();
    }

    protected Playwright getPlaywright() {
        return playwright.get();
    }

    protected static boolean isHeadless() {
        String propertyValue = System.getProperty("headless");
        if (propertyValue == null || propertyValue.isBlank()) {
            propertyValue = System.getenv("HEADLESS");
        }
        if (propertyValue == null || propertyValue.isBlank()) {
            return true;
        }
        return Boolean.parseBoolean(propertyValue);
    }
}
