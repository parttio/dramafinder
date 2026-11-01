package org.vaadin.addons.dramafinder.agent;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Keeps {@link ViewOutline}'s wrapper list in step with the element package: a
 * new public element class must be added to it (or excluded here, with a reason).
 */
class ViewOutlineRegistryTest {

    // Tooltips attach to almost every component and would drown the outline.
    private static final Set<String> EXCLUDED = Set.of("TooltipElement");

    @Test
    void everyPublicElementIsKnown() throws Exception {
        Path dir = Path.of("target/classes/org/vaadin/addons/dramafinder/element");
        List<String> missing;
        try (Stream<Path> files = Files.list(dir)) {
            missing = files.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith("Element.class") && !n.contains("$"))
                    .map(n -> n.substring(0, n.length() - ".class".length()))
                    .filter(n -> !EXCLUDED.contains(n))
                    .map(ViewOutlineRegistryTest::load)
                    .filter(c -> Modifier.isPublic(c.getModifiers())
                            && !Modifier.isAbstract(c.getModifiers())
                            && ViewOutline.tagOf(c) != null)
                    .filter(c -> !ViewOutline.elements().contains(c))
                    .map(Class::getSimpleName)
                    .sorted()
                    .toList();
        }
        assertEquals(List.of(), missing, "Add these to ViewOutline.ELEMENTS");
    }

    private static Class<?> load(String simpleName) {
        try {
            return Class.forName("org.vaadin.addons.dramafinder.element." + simpleName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
