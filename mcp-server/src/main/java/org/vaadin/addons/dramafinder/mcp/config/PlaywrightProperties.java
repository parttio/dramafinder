package org.vaadin.addons.dramafinder.mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Playwright browser settings.
 */
@ConfigurationProperties(prefix = "playwright")
public record PlaywrightProperties(
        boolean headless,
        String browser,
        TimeoutProperties timeout,
        ViewportProperties viewport
) {
    public PlaywrightProperties {
        if (timeout == null) {
            timeout = new TimeoutProperties(30000, 5000);
        }
        if (viewport == null) {
            viewport = new ViewportProperties(1280, 720);
        }
        if (browser == null) {
            browser = "chromium";
        }
    }

    public record TimeoutProperties(int defaultTimeout, int assertion) {
        public TimeoutProperties {
            if (defaultTimeout <= 0) defaultTimeout = 30000;
            if (assertion <= 0) assertion = 5000;
        }
    }

    public record ViewportProperties(int width, int height) {
        public ViewportProperties {
            if (width <= 0) width = 1280;
            if (height <= 0) height = 720;
        }
    }
}
