package org.vaadin.addons.dramafinder;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.DispatchEventOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractBasePlaywrightIT implements HasTestView {

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
        // Browser and Playwright are kept alive across test classes and closed by shutdown hook
    }

    @BeforeAll
    public static void setup() {
        if (browser.get() == null) {
            Playwright p = Playwright.create();
            playwright.set(p);
            Browser b = p.chromium().launch(new LaunchOptions().setHeadless(isHeadless()));
            browser.set(b);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                b.close();
                p.close();
            }));
        }
    }

    protected Page getPage() {
        return page;
    }

    protected Browser getBrowser() {
        return browser.get();
    }

    protected Playwright getPlaywright() {
        return playwright.get();
    }

    protected void event(Locator locator, String type, Object eventInit,
                         DispatchEventOptions options) {
        locator.nth(0).dispatchEvent(type, eventInit, options);
    }

    protected void event(Locator locator, String eventName) {
        locator.nth(0).dispatchEvent(eventName);
    }

    protected void click(Locator locator) {
        if (!locator.nth(0).locator("input").all().isEmpty()) {
            locator = locator.nth(0).locator("input");
        }
        locator.nth(0).click();
    }

    protected void press(Locator locator, String key) {
        locator.nth(0).press(key);
    }

    protected void fill(Locator locator, String value) {
        if (!locator.first().locator("input").all().isEmpty()) {
            locator = locator.first().locator("input");
        }
        locator.nth(0).fill(value);
        locator.nth(0).blur();
    }

    protected void select(Locator locator, String val) {
        fill(locator, val);
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
