package org.vaadin.addons.dramafinder.element.query;

import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

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
        boolean ok = compiler.getTask(out, null, null,
                List.of("-proc:none", "-d", System.getProperty("java.io.tmpdir") + "/dramafinder-snippet",
                        "-classpath", System.getProperty("java.class.path")),
                null, List.of(file)).call();
        return ok ? "" : out.toString();
    }
}
