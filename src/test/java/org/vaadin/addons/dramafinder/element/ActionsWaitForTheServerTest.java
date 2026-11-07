package org.vaadin.addons.dramafinder.element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every element action waits for the server round-trip it may trigger (issue
 * #202, AGENTS.md "Every element action waits"): a method that performs a raw
 * Playwright action or sets a DOM property must also call
 * {@code waitAfterAction()} (or {@code AutoWait.afterAction}, or the waiting
 * {@code setProperty}). Read from the sources, because a forgotten wait leaves
 * no trace a test against the page can see.
 */
class ActionsWaitForTheServerTest {

    private static final Path SOURCES = Path.of("src/main/java/org/vaadin/addons/dramafinder/element");

    /** A method declaration at class level and its body. */
    private static final Pattern METHOD = Pattern.compile(
            "\n    (?:public |default |protected |private |static |final |<[^>]+> )*[\\w<>\\[\\], ?]+ (\\w+)\\([^)]*\\)"
                    + "[^{;]*\\{\n(.*?)\n    }\n", Pattern.DOTALL);

    /** A Playwright action called on a locator. */
    private static final Pattern RAW_ACTION = Pattern.compile(
            "(?:Locator\\(\\)|getLocator\\(\\)|locator|input|target|button|item|cell|\\)\\s*)\\."
                    + "(?:click|fill|press|check|uncheck|selectOption|hover|focus|blur|setInputFiles"
                    + "|dispatchEvent|clear|pressSequentially)\\(");

    /** A DOM property assigned from a script. */
    private static final Pattern PROPERTY_WRITE = Pattern.compile("evaluate\\(\"\\(el, ?\\w+\\) => el\\.\\w+ = ");

    private static final List<String> WAITS = List.of("waitAfterAction()", "AutoWait.afterAction(", "setProperty(");

    /**
     * Calls that wait regardless of the opt-out, allowed only where the reason
     * is documented: the definition itself, and the context menu, which must
     * not context-click while a response can still replace the menu element.
     */
    private static final Map<String, String> UNCONDITIONAL_WAIT_ALLOWED = Map.of(
            "HasLocatorElement.java", "waitForVaadinIdle", "ContextMenuElement.java", "openOn");

    @Test
    void everyActionWaitsAfterwards() throws IOException {
        List<String> missing = new ArrayList<>();
        int actions = 0;
        for (Path file : javaFiles()) {
            Matcher m = METHOD.matcher(Files.readString(file));
            while (m.find()) {
                String body = m.group(2);
                if (!RAW_ACTION.matcher(body).find() && !PROPERTY_WRITE.matcher(body).find()) {
                    continue;
                }
                actions++;
                if (WAITS.stream().noneMatch(body::contains)) {
                    missing.add(file.getFileName() + " " + m.group(1));
                }
            }
        }
        assertTrue(missing.isEmpty(), "Actions without waitAfterAction(): " + missing);
        assertTrue(actions > 50, "only " + actions + " actions found; is the scan still matching?");
    }

    @Test
    void noActionWaitsInAWayTheOptOutCannotSkip() throws IOException {
        List<String> offending = new ArrayList<>();
        for (Path file : javaFiles()) {
            Matcher m = METHOD.matcher(Files.readString(file));
            while (m.find()) {
                String body = m.group(2);
                boolean unconditional = body.contains("waitForVaadinIdle(") || body.contains("WAIT_FOR_VAADIN_SCRIPT");
                String allowed = UNCONDITIONAL_WAIT_ALLOWED.get(file.getFileName().toString());
                if (unconditional && !m.group(1).equals(allowed)) {
                    offending.add(file.getFileName() + " " + m.group(1));
                }
            }
        }
        assertTrue(offending.isEmpty(), "Use waitAfterAction(), which honours AutoWait: " + offending);
    }

    private static List<Path> javaFiles() throws IOException {
        try (Stream<Path> files = Files.walk(SOURCES)) {
            return files.filter(f -> f.toString().endsWith(".java")).toList();
        }
    }
}
