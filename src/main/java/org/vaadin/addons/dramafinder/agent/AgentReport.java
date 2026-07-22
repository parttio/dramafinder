package org.vaadin.addons.dramafinder.agent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import com.microsoft.playwright.Page;

/**
 * Writes agent-oriented verification artifacts for a single test into a report
 * directory ({@code target/agent-report/} for Maven, {@code build/agent-report/}
 * for Gradle).
 * <p>
 * It produces two kinds of output:
 * <ul>
 *   <li>deterministic, numbered screenshots via {@link #shot(String)} at key
 *       interaction points ({@code 01-login.png}, {@code 02-order-list.png}, …);</li>
 *   <li>a failure bundle via {@link #captureFailure(Throwable)} — the assertion
 *       message and a trimmed stack trace ({@code failure.txt}), a screenshot of
 *       the page at failure time ({@code failure.png}), and a semantic
 *       {@link ComponentSnapshot component snapshot}
 *       ({@code component-snapshot.txt}).</li>
 * </ul>
 * A far cheaper agent loop than driving Playwright MCP interactively: one run
 * batches every action and screenshot, then the agent reads a terse report.
 */
public class AgentReport {

    private static final int MAX_STACK_FRAMES = 15;

    private final Page page;
    private final Path directory;
    private int shotCounter = 0;

    /**
     * Create a report writing into the build-tool default directory.
     *
     * @param page the Playwright page to screenshot and inspect
     */
    public AgentReport(Page page) {
        this(page, defaultReportDirectory());
    }

    /**
     * Create a report writing into the given directory.
     *
     * @param page      the Playwright page to screenshot and inspect
     * @param directory the report output directory
     */
    public AgentReport(Page page, Path directory) {
        this.page = page;
        this.directory = directory;
    }

    /**
     * Resolve the default report directory for the current build tool:
     * {@code build/agent-report} when a Gradle build file is present, otherwise
     * {@code target/agent-report} (Maven).
     *
     * @return the resolved report directory (not created)
     */
    public static Path defaultReportDirectory() {
        boolean gradle = Files.exists(Path.of("build.gradle"))
                || Files.exists(Path.of("build.gradle.kts"))
                || Files.exists(Path.of("settings.gradle"))
                || Files.exists(Path.of("settings.gradle.kts"));
        Path base = gradle ? Path.of("build") : Path.of("target");
        return base.resolve("agent-report");
    }

    /**
     * The directory this report writes to.
     *
     * @return the report directory
     */
    public Path directory() {
        return directory;
    }

    /**
     * Capture a numbered screenshot of the current page state.
     * <p>
     * The file name is {@code NN-<name>.png}, where {@code NN} is a
     * zero-padded, incrementing counter, giving deterministic ordering that
     * matches the sequence of interaction points in the test.
     *
     * @param name a short descriptive name for the interaction point
     * @return the written screenshot path
     */
    public synchronized Path shot(String name) {
        ensureDirectory();
        String fileName = String.format(Locale.ROOT, "%02d-%s.png",
                ++shotCounter, sanitize(name));
        Path file = directory.resolve(fileName);
        page.screenshot(new Page.ScreenshotOptions().setPath(file));
        return file;
    }

    /**
     * Write the failure bundle for a failed test: {@code failure.txt} (message
     * + trimmed stack trace + page URL), {@code failure.png} (full-page
     * screenshot), and {@code component-snapshot.txt} (semantic snapshot).
     * <p>
     * Best-effort: a failure to capture the screenshot or snapshot (e.g. the
     * page is already closed) never masks the original test failure.
     *
     * @param error the failure cause (may be {@code null})
     */
    public void captureFailure(Throwable error) {
        ensureDirectory();

        StringBuilder txt = new StringBuilder();
        txt.append("URL: ").append(safeUrl()).append(System.lineSeparator());
        txt.append(System.lineSeparator());
        txt.append(error == null ? "Test failed (no throwable)" : error.toString());
        txt.append(System.lineSeparator()).append(System.lineSeparator());
        txt.append(trimmedStackTrace(error));
        write(directory.resolve("failure.txt"), txt.toString());

        try {
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(directory.resolve("failure.png"))
                    .setFullPage(true));
        } catch (RuntimeException e) {
            write(directory.resolve("failure.png.txt"),
                    "Screenshot capture failed: " + e.getMessage());
        }

        try {
            String snapshot = ComponentSnapshot.capture(page);
            write(directory.resolve("component-snapshot.txt"),
                    snapshot.isEmpty() ? "(no Vaadin components found)" : snapshot);
        } catch (RuntimeException e) {
            write(directory.resolve("component-snapshot.txt"),
                    "Component snapshot failed: " + e.getMessage());
        }
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Could not create report directory " + directory, e);
        }
    }

    private void write(Path file, String content) {
        try {
            Files.writeString(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file, e);
        }
    }

    private String safeUrl() {
        try {
            return page.url();
        } catch (RuntimeException e) {
            return "(unknown)";
        }
    }

    private static String trimmedStackTrace(Throwable error) {
        if (error == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] frames = error.getStackTrace();
        int shown = 0;
        for (StackTraceElement frame : frames) {
            if (shown >= MAX_STACK_FRAMES) {
                sb.append("\t... ").append(frames.length - shown)
                        .append(" more").append(System.lineSeparator());
                break;
            }
            String cls = frame.getClassName();
            // Drop reflective/JUnit framework noise; keep test + library frames.
            if (cls.startsWith("java.")
                    || cls.startsWith("jdk.")
                    || cls.startsWith("sun.")
                    || cls.startsWith("org.junit.")
                    || cls.startsWith("org.springframework.")) {
                continue;
            }
            sb.append("\tat ").append(frame).append(System.lineSeparator());
            shown++;
        }
        Throwable cause = error.getCause();
        if (cause != null && cause != error) {
            sb.append("Caused by: ").append(cause).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "shot";
        }
        return name.trim().replaceAll("[^A-Za-z0-9._-]+", "-");
    }
}
