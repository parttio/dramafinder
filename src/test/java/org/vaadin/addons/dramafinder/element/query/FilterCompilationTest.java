package org.vaadin.addons.dramafinder.element.query;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks that the filters on a query follow the component type: a filter that
 * cannot apply to a component is a compile error, not an empty result.
 */
class FilterCompilationTest {

    @Test
    void filtersThatFitTheComponentCompile() {
        String diagnostics = compile("""
                TextFieldElement name = find(page, TextFieldElement.class)
                        .withLabel("Name").withPlaceholder("Ada").withTestId("name").single();
                ButtonElement save = find(page, ButtonElement.class)
                        .withText("Save").withAccessibleName("Save").withId("save").single();
                CheckboxElement accept = find(page, CheckboxElement.class).withLabel("Accept").single();
                GridElement grid = find(page, GridElement.class).withAccessibleName("People").single();
                TooltipElement tip = find(page, TooltipElement.class).withText("Saves").single();
                ButtonElement delete = new DialogElement(page).find(ButtonElement.class).withText("Delete").single();
                """);
        assertTrue(diagnostics.isEmpty(), diagnostics);
    }

    @Test
    void labelFilterOnButtonDoesNotCompile() {
        assertRejected("find(page, ButtonElement.class).withLabel(\"Save\");", "withLabel");
    }

    @Test
    void textFilterOnTextFieldDoesNotCompile() {
        assertRejected("find(page, TextFieldElement.class).withText(\"Name\");", "withText");
    }

    @Test
    void placeholderFilterOnCheckboxDoesNotCompile() {
        assertRejected("find(page, CheckboxElement.class).withPlaceholder(\"x\");", "withPlaceholder");
    }

    @Test
    void wrongResultTypeDoesNotCompile() {
        assertRejected("ButtonElement b = find(page, TextFieldElement.class).single();", "incompatible types");
    }

    private static void assertRejected(String statement, String expected) {
        String diagnostics = compile(statement);
        assertFalse(diagnostics.isEmpty(), "compiled: " + statement);
        assertTrue(diagnostics.contains(expected), diagnostics);
    }

    private static String compile(String body) {
        String source = """
                import com.microsoft.playwright.Page;
                import org.vaadin.addons.dramafinder.element.*;
                import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

                class Snippet {
                    void run(Page page) {
                %s
                    }
                }
                """.formatted(body);
        JavaFileObject file = new SimpleJavaFileObject(URI.create("string:///Snippet.java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StringWriter out = new StringWriter();
        Path classes = createTempDirectory();
        try {
            // -Xlint:path: javac silently skips a classpath entry it cannot
            // find, which otherwise surfaces only as "package does not exist"
            boolean ok = compiler.getTask(out, null, null,
                    List.of("-proc:none", "-Xlint:path", "-d", classes.toString(),
                            "-classpath", classPath()),
                    null, List.of(file)).call();
            return ok ? "" : out.toString();
        } finally {
            deleteRecursively(classes);
        }
    }

    /**
     * The test classpath, plus the locations the snippet's imports were
     * actually loaded from: {@code java.class.path} alone has been seen to
     * miss Playwright on a parallel CI build, which then failed every snippet
     * with "package com.microsoft.playwright does not exist".
     */
    private static String classPath() {
        Set<String> entries = new LinkedHashSet<>();
        for (Class<?> type : List.of(Page.class, ElementQueries.class)) {
            try {
                entries.add(Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
            } catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        }
        for (String entry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (!entry.isEmpty()) {
                entries.add(entry);
            }
        }
        return String.join(File.pathSeparator, entries);
    }

    /** A directory of its own, so concurrent builds never share class output. */
    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("dramafinder-snippet");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void deleteRecursively(Path dir) {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
