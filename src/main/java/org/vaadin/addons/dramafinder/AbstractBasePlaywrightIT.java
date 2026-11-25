package org.vaadin.addons.dramafinder;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
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
 * Launches one Chromium browser per test class with {@link #launchOptions()},
 * opens a fresh {@link #page} before each test, navigates it to
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

    // How long WAIT_FOR_VAADIN_SCRIPT waits for Flow to register its UI's client
    // once Flow has started in a page (issue #305).
    private static final int FLOW_UI_STARTUP_TIMEOUT_MS = 3000;

    /**
     * A {@code page.waitForFunction} script that is true once Vaadin Flow has no
     * client-server exchange in flight. In a page where Flow has started but
     * not yet registered its UI's client (only the {@code TypeScript} client
     * exists, before {@code flowInit} has run), it first waits for that client,
     * for at most 3 seconds counted from the first wait in that page: a page
     * that runs Flow but shows no Flow UI (a Hilla view) never registers one,
     * and pays that once per page load. A page without Flow is idle at once.
     */
    public static final String WAIT_FOR_VAADIN_SCRIPT =
            // @formatter:off
            "() => {"
            + "if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.clients) {"
            + "  var clients = window.Vaadin.Flow.clients;"
            + "  if (!Object.keys(clients).some(function (id) { return id !== 'TypeScript'; })) {"
            + "    if (window.__dramafinderFlowStartSeenAt === undefined) {"
            + "      window.__dramafinderFlowStartSeenAt = performance.now();"
            + "    }"
            + "    if (performance.now() - window.__dramafinderFlowStartSeenAt < " + FLOW_UI_STARTUP_TIMEOUT_MS + ") {"
            + "      return false;"
            + "    }"
            + "  }"
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
        prepareBrowserContext(page.context());
        page.navigate(getUrl() + getView());
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
        page.setDefaultNavigationTimeout(4000);
        page.setDefaultTimeout(15000);
    }

    /**
     * Called before each test opens its view, with the fresh browser context
     * of {@link #page}: the place to add cookies or sign in. Does nothing by
     * default; {@code SpringPlaywrightIT} signs in the test's Spring Security
     * user here.
     *
     * @param context the browser context the test's page runs in
     */
    protected void prepareBrowserContext(BrowserContext context) {
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
        LaunchOptions options = launchOptions();
        Playwright.CreateOptions createOptions = new Playwright.CreateOptions();
        if (options.executablePath != null) {
            // The configured binary replaces Playwright's Chromium: don't download it.
            createOptions.setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1"));
        }
        Playwright p = Playwright.create(createOptions);
        playwright.set(p);
        browser.set(p.chromium().launch(options));
    }

    /**
     * Build the launch options of each test class's browser from the
     * {@code headless}, {@code playwright.executable} and
     * {@code playwright.channel} settings.
     * <p>
     * Each setting is a system property, else an environment variable
     * ({@code HEADLESS}, {@code PLAYWRIGHT_EXECUTABLE},
     * {@code PLAYWRIGHT_CHANNEL}); blank counts as unset. {@code headless} is
     * read by {@link #isHeadless()}. {@code playwright.executable} names a
     * Chromium-based binary to launch instead of Playwright's own Chromium,
     * e.g. {@code /usr/bin/google-chrome}, and makes Playwright skip
     * downloading its browsers. {@code playwright.channel} names an installed
     * browser channel such as {@code chrome} or {@code msedge}. A test that
     * launches its own browser can start from these options.
     *
     * @return new launch options; executable path and channel are {@code null}
     *         when not configured
     */
    protected static LaunchOptions launchOptions() {
        LaunchOptions options = new LaunchOptions().setHeadless(isHeadless());
        String executable = setting("playwright.executable", "PLAYWRIGHT_EXECUTABLE");
        if (executable != null) {
            options.setExecutablePath(Path.of(executable));
        }
        String channel = setting("playwright.channel", "PLAYWRIGHT_CHANNEL");
        if (channel != null) {
            options.setChannel(channel);
        }
        return options;
    }

    private static String setting(String property, String environmentVariable) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        return value == null || value.isBlank() ? null : value.strip();
    }

    protected Page getPage() {
        return page;
    }

    // ── Navigation by route class ──────────────────────────────────────
    // Signatures stay free of Vaadin types: JUnit reflects over this class's
    // methods, and a test classpath without flow-server must still load it.

    /**
     * Open {@code view} — its path resolved from {@code @Route} and the layouts'
     * {@code @RoutePrefix} — under {@link #getUrl()}, and wait for Vaadin to
     * finish loading. See {@link RouteNavigation}.
     *
     * @param view a class annotated with {@code @Route}
     */
    protected void navigate(Class<?> view) {
        RouteNavigation.navigate(page, getUrl(), view);
    }

    /**
     * Open a parameterized view under {@link #getUrl()} and wait for Vaadin to
     * finish loading.
     *
     * @param view      a class annotated with {@code @Route}
     * @param parameter the {@code HasUrlParameter} value, or a
     *                  {@code RouteParameters} for a template route such as
     *                  {@code "user/:userId/edit"}
     */
    protected void navigate(Class<?> view, Object parameter) {
        RouteNavigation.navigate(page, getUrl(), view, parameter);
    }

    /**
     * Assert, retrying until the timeout, that the browser is on {@code view},
     * e.g. after a login redirect or a navigation guard. The path may match the
     * view's {@code @Route} or any of its {@code @RouteAlias} paths.
     *
     * @param view a class annotated with {@code @Route}
     */
    protected void assertAt(Class<?> view) {
        RouteNavigation.assertAt(page, getUrl(), view);
    }

    /**
     * Assert, retrying until the timeout, that the browser's path under
     * {@link #getUrl()} is exactly {@code path}, whatever the query string.
     *
     * @param path the expected path, e.g. {@code "profile"}
     */
    protected void assertPath(String path) {
        RouteNavigation.assertPath(page, getUrl(), path);
    }

    /**
     * Assert, retrying until the timeout, that the URL holds the query
     * parameter {@code name} with {@code value}, or no such parameter when
     * {@code value} is {@code null}.
     *
     * @param name  the query parameter name
     * @param value the expected decoded value; {@code null} to assert absence
     */
    protected void assertQueryParameter(String name, String value) {
        RouteNavigation.assertQueryParameter(page, name, value);
    }

    /**
     * Get the path the browser is on under {@link #getUrl()}, without leading
     * or trailing slash or query string; reads once, without waiting. See
     * {@link RouteNavigation#currentPath(Page, String)}.
     *
     * @return the path, or {@code null} when the browser is outside the
     *         application
     */
    protected String currentPath() {
        return RouteNavigation.currentPath(page, getUrl());
    }

    /**
     * Whether the browser is on {@code view} right now, without waiting; the
     * non-asserting form of {@link #assertAt(Class)}.
     *
     * @param view a class annotated with {@code @Route}
     * @return {@code true} when the path matches the view's route or one of
     *         its aliases
     */
    protected boolean isAt(Class<?> view) {
        return RouteNavigation.isAt(page, getUrl(), view);
    }

    /**
     * Get the decoded value of a query parameter of the current URL, without
     * waiting.
     *
     * @param name the query parameter name
     * @return the value, or empty when the URL has no such parameter
     */
    protected Optional<String> queryParameter(String name) {
        return RouteNavigation.queryParameter(page, name);
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

    /**
     * Whether the browser runs headless: the {@code headless} system property,
     * else the {@code HEADLESS} environment variable, else {@code true}.
     *
     * @return {@code false} only when the setting is {@code false}
     */
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
