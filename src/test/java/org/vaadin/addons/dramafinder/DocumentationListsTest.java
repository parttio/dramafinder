package org.vaadin.addons.dramafinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Keeps the hand-maintained lists of elements and mixins in step with the code (issue #147).
 * Agents read {@code element-mapping.md} and {@code llms.txt} to find out what exists, and the
 * generated {@code api-reference.md} does not cover them, so a new element or mixin missing
 * from them stays invisible until someone notices.
 */
class DocumentationListsTest {

    private static final Path ELEMENTS = Path.of("src/main/java/org/vaadin/addons/dramafinder/element");
    private static final Path MIXINS = ELEMENTS.resolve("shared");

    // A public, concrete element class: what a test can use directly
    private static final Pattern PUBLIC_CONCRETE_CLASS =
            Pattern.compile("^public (final )?class \\w+Element\\b", Pattern.MULTILINE);
    private static final Pattern PUBLIC_INTERFACE = Pattern.compile("^public interface \\w+", Pattern.MULTILINE);

    @Test
    void everyPublicElementIsInTheElementMapping() throws IOException {
        String mapping = Files.readString(Path.of("skills/vaadin-playwright-test/element-mapping.md"));
        assertEquals(List.of(), missing(declaring(ELEMENTS, PUBLIC_CONCRETE_CLASS), mapping),
                "public elements missing from skills/vaadin-playwright-test/element-mapping.md");
    }

    @Test
    void everyMixinIsInLlmsTxt() throws IOException {
        String llms = Files.readString(Path.of("llms.txt"));
        assertEquals(List.of(), missing(declaring(MIXINS, PUBLIC_INTERFACE), llms),
                "mixins missing from the Mixin Interfaces section of llms.txt");
    }

    @Test
    void everyMixinIsInTodo() throws IOException {
        String todo = Files.readString(Path.of("TODO.md"));
        List<String> missing = new ArrayList<>();
        for (String mixin : declaring(MIXINS, PUBLIC_INTERFACE)) {
            if (!todo.contains("- " + mixin + "\n")) {
                missing.add(mixin);
            }
        }
        assertEquals(List.of(), missing, "mixins missing from 'Shared interfaces implemented' in TODO.md");
    }

    private static List<String> missing(List<String> names, String document) {
        return names.stream().filter(name -> !document.contains("`" + name + "`")).toList();
    }

    // The type names of the Java files directly in dir whose source matches declaration
    private static List<String> declaring(Path dir, Pattern declaration) throws IOException {
        List<String> names = new ArrayList<>();
        try (Stream<Path> files = Files.list(dir)) {
            for (Path file : files.filter(f -> f.toString().endsWith(".java")).sorted().toList()) {
                if (declaration.matcher(Files.readString(file)).find()) {
                    names.add(file.getFileName().toString().replace(".java", ""));
                }
            }
        }
        return names;
    }
}
