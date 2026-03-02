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

    String WAIT_FOR_VAADIN_SCRIPT =
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

    protected Browser getBrowser() {
        return browser.get();
    }

    protected Playwright getPlaywright() {
        return playwright.get();
    }

    protected void event(Locator locator, String type, Object eventInit,
                         DispatchEventOptions options) {
        locator.nth(0).dispatchEvent(type, eventInit, options);
        getPage().waitForTimeout(10);
    }

    protected void event(Locator locator, String eventName) {
        locator.nth(0).dispatchEvent(eventName);
        getPage().waitForTimeout(10);
    }

    protected void click(Locator locator) {
        if (!locator.nth(0).locator("input").all().isEmpty()) {
            locator = locator.nth(0).locator("input");
        }
        locator.nth(0).click();
        getPage().waitForTimeout(10);
    }

    protected void press(Locator locator, String key) {
        locator.nth(0).press(key);
        getPage().waitForTimeout(10);
    }

    protected void fill(Locator locator, String value) {
        if (!locator.first().locator("input").all().isEmpty()) {
            locator = locator.first().locator("input");
        }
        locator.nth(0).fill(value);
        locator.nth(0).blur();
        getPage().waitForTimeout(10);
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
