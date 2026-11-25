package org.vaadin.addons.dramafinder.tests.it;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code -Dplaywright.executable} makes the base class launch that binary
 * instead of Playwright's own Chromium (issue #326).
 * <p>
 * The binary is a shell script that records its launch and then runs
 * Playwright's Chromium, so the test needs no other browser installed. The
 * property is set only while this class's browser launches, and the class is
 * isolated from the others, which run in parallel and launch theirs from the
 * same JVM-wide properties.
 */
@Isolated
@DisabledOnOs(OS.WINDOWS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BrowserExecutableIT extends SpringPlaywrightIT {

    private static Path launchMarker;

    /**
     * Hides {@link AbstractBasePlaywrightIT#setup()}, so JUnit runs this one
     * instead: the property has to be set before the browser launches.
     */
    @BeforeAll
    public static void setup() {
        Path executable = recordingBrowser();
        System.setProperty("playwright.executable", executable.toString());
        try {
            AbstractBasePlaywrightIT.setup();
        } finally {
            System.clearProperty("playwright.executable");
        }
    }

    /** A script that touches {@link #launchMarker}, then runs Playwright's Chromium. */
    private static Path recordingBrowser() {
        try {
            Path dir = Files.createTempDirectory("dramafinder-browser");
            launchMarker = dir.resolve("launched");
            Path executable = dir.resolve("browser");
            Files.writeString(executable, "#!/bin/sh\n"
                    + "touch '" + launchMarker + "'\n"
                    + "exec '" + playwrightChromium() + "' \"$@\"\n");
            assertTrue(executable.toFile().setExecutable(true));
            return executable;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path playwrightChromium() {
        try (Playwright playwright = Playwright.create()) {
            return Path.of(playwright.chromium().executablePath());
        }
    }

    @Override
    public String getView() {
        return "checkbox";
    }

    @Test
    public void launchesTheConfiguredExecutable() {
        assertTrue(Files.exists(launchMarker),
                "the browser was not launched through " + launchMarker.getParent());
        assertThat(page).hasTitle("Checkbox Demo");
    }
}
