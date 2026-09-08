package org.vaadin.addons.dramafinder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the agent-facing documentation bundled into the jar.
 *
 * <p>An agent working offline sees only the jar and the pom — Maven resolves no
 * sources jar and no other classifier unless asked — so these files are the only
 * documentation it can reach. When they are missing, agents fall back to
 * {@code jar xf} + {@code javap}, which loses the Javadoc and the parameter
 * names. The build wiring that puts them here is easy to break silently
 * (a renamed skill folder, a dropped maven-resources-plugin execution), hence
 * this test.
 *
 * <p>It reads from the classpath, which during the build is
 * {@code target/classes} — the exact directory that becomes the jar.
 */
class DocumentationBundledInJarTest {

    /** Copied in from {@code skills/vaadin-playwright-test} by the build. */
    @Test
    void apiReferenceIsBundled() throws IOException {
        String content = bundled("api-reference.md");
        assertTrue(content.startsWith("# DramaFinder API Reference"),
                "api-reference.md does not look like the generated reference");
        assertTrue(content.contains("## Test setup"),
                "api-reference.md is missing the test-setup section, so a reader "
                        + "cannot find out how to obtain a Page");
        assertTrue(content.contains("## Element index"),
                "api-reference.md is missing the element index");
    }

    /** Copied in from {@code skills/vaadin-playwright-screenshot} by the build. */
    @Test
    void agentApiReferenceIsBundled() throws IOException {
        assertTrue(bundled("agent-api-reference.md")
                        .startsWith("# DramaFinder Agent Helpers API Reference"),
                "agent-api-reference.md does not look like the generated reference");
    }

    /** Hand-written, lives in {@code src/main/resources}. */
    @Test
    void startHereIsBundled() throws IOException {
        String content = bundled("START-HERE.md");
        assertTrue(content.contains("AbstractBasePlaywrightIT"),
                "START-HERE.md no longer shows how to set a test up");
        assertTrue(content.contains("META-INF/dramafinder/api-reference.md"),
                "START-HERE.md no longer points at the full reference");
    }

    private static String bundled(String name) throws IOException {
        String path = "/META-INF/dramafinder/" + name;
        try (InputStream in = DocumentationBundledInJarTest.class.getResourceAsStream(path)) {
            assertNotNull(in, path + " is not on the classpath — check the "
                    + "maven-resources-plugin 'bundle-agent-docs' execution in pom.xml");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
