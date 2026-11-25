package org.vaadin.addons.dramafinder;

import java.nio.file.Path;

import com.microsoft.playwright.BrowserType.LaunchOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Which browser {@link AbstractBasePlaywrightIT} launches, as configured by
 * system properties (issue #326).
 */
class BrowserLaunchOptionsTest {

    @AfterEach
    void clearProperties() {
        System.clearProperty("playwright.executable");
        System.clearProperty("playwright.channel");
        System.clearProperty("headless");
    }

    @Test
    void executablePropertyLaunchesThatBinary() {
        System.setProperty("playwright.executable", "/usr/bin/google-chrome");

        LaunchOptions options = AbstractBasePlaywrightIT.launchOptions();

        assertEquals(Path.of("/usr/bin/google-chrome"), options.executablePath);
    }

    @Test
    void channelPropertyLaunchesThatChannel() {
        System.setProperty("playwright.channel", "chrome");

        LaunchOptions options = AbstractBasePlaywrightIT.launchOptions();

        assertEquals("chrome", options.channel);
    }

    @Test
    void blankPropertiesKeepPlaywrightsOwnBrowser() {
        System.setProperty("playwright.executable", " ");
        System.setProperty("playwright.channel", "");

        LaunchOptions options = AbstractBasePlaywrightIT.launchOptions();

        assertNull(options.executablePath);
        assertNull(options.channel);
    }

    @Test
    void headlessPropertyStillApplies() {
        System.setProperty("headless", "false");

        LaunchOptions options = AbstractBasePlaywrightIT.launchOptions();

        assertFalse(options.headless);
    }
}
