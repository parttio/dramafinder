package org.vaadin.addons.dramafinder.mcp.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vaadin.addons.dramafinder.mcp.config.PlaywrightProperties;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Service managing Playwright browser lifecycle.
 * <p>
 * Provides thread-safe access to browser, context, and page instances.
 */
@Service
public class BrowserService {

    private static final Logger log = LoggerFactory.getLogger(BrowserService.class);

    private final PlaywrightProperties properties;
    private final ReentrantLock lock = new ReentrantLock();

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private String currentBrowserType;
    private boolean currentHeadless;

    public BrowserService(PlaywrightProperties properties) {
        this.properties = properties;
    }

    /**
     * Start the browser with the configured settings.
     *
     * @param browserType the browser type (chromium, firefox, webkit)
     * @param headless    whether to run in headless mode
     */
    public void start(String browserType, boolean headless) {
        lock.lock();
        try {
            if (browser != null && browser.isConnected()) {
                if (browserType.equals(currentBrowserType) && headless == currentHeadless) {
                    log.info("Browser already running with same settings");
                    return;
                }
                stop();
            }

            log.info("Starting {} browser (headless={})", browserType, headless);
            playwright = Playwright.create();

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless);

            browser = switch (browserType.toLowerCase()) {
                case "firefox" -> playwright.firefox().launch(launchOptions);
                case "webkit" -> playwright.webkit().launch(launchOptions);
                default -> playwright.chromium().launch(launchOptions);
            };

            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setViewportSize(properties.viewport().width(), properties.viewport().height());
            context = browser.newContext(contextOptions);
            context.setDefaultTimeout(properties.timeout().defaultTimeout());

            page = context.newPage();

            currentBrowserType = browserType;
            currentHeadless = headless;

            log.info("Browser started successfully");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Start the browser with default settings from configuration.
     */
    public void start() {
        start(properties.browser(), properties.headless());
    }

    /**
     * Stop the browser and release resources.
     */
    public void stop() {
        lock.lock();
        try {
            log.info("Stopping browser");
            if (page != null) {
                page.close();
                page = null;
            }
            if (context != null) {
                context.close();
                context = null;
            }
            if (browser != null) {
                browser.close();
                browser = null;
            }
            if (playwright != null) {
                playwright.close();
                playwright = null;
            }
            currentBrowserType = null;
            log.info("Browser stopped");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Restart the browser with current settings.
     */
    public void restart() {
        String browserType = currentBrowserType != null ? currentBrowserType : properties.browser();
        boolean headless = currentBrowserType != null ? currentHeadless : properties.headless();
        stop();
        start(browserType, headless);
    }

    /**
     * Get the current page.
     *
     * @return the page instance
     * @throws IllegalStateException if browser is not started
     */
    public Page getPage() {
        lock.lock();
        try {
            ensureBrowserStarted();
            return page;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the current browser context.
     *
     * @return the browser context
     * @throws IllegalStateException if browser is not started
     */
    public BrowserContext getContext() {
        lock.lock();
        try {
            ensureBrowserStarted();
            return context;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if the browser is currently running.
     *
     * @return true if browser is active
     */
    public boolean isRunning() {
        lock.lock();
        try {
            return browser != null && browser.isConnected();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the current browser type.
     *
     * @return the browser type or null if not started
     */
    public String getBrowserType() {
        return currentBrowserType;
    }

    /**
     * Check if running in headless mode.
     *
     * @return true if headless
     */
    public boolean isHeadless() {
        return currentHeadless;
    }

    /**
     * Get the current page URL.
     *
     * @return the URL or null if not navigated
     */
    public String getCurrentUrl() {
        lock.lock();
        try {
            if (page != null) {
                return page.url();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the current page title.
     *
     * @return the page title or null
     */
    public String getPageTitle() {
        lock.lock();
        try {
            if (page != null) {
                return page.title();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private void ensureBrowserStarted() {
        if (browser == null || !browser.isConnected()) {
            throw new IllegalStateException("Browser is not started. Call start() first.");
        }
    }

    @PreDestroy
    public void cleanup() {
        stop();
    }
}
