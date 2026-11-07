package org.vaadin.addons.dramafinder.element.query;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.VaadinElement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A label is the visible label, and both ways of finding a component by it
 * agree: an element has a (non-deprecated) {@code getByLabel} factory exactly
 * when its query offers {@code withLabel}.
 * <p>
 * Elements without a visible label keep their pre-2.0 {@code getByLabel}, which
 * matched the accessible name, only as a deprecated method.
 */
class LabelLookupParityTest {

    private static final List<String> FACTORIES = List.of("getByLabel", "getByLabelContaining");

    @Test
    void getByLabelExistsExactlyWhenTheQueryHasWithLabel() throws Exception {
        List<String> problems = new ArrayList<>();
        int labelled = 0;
        for (Class<?> type : elementClasses()) {
            boolean withLabel = queryHasLabelFilters(type);
            for (String factory : FACTORIES) {
                for (Class<?> scope : List.of(Page.class, Locator.class)) {
                    Method m = declaredStatic(type, factory, scope);
                    String name = type.getSimpleName() + "." + factory + "(" + scope.getSimpleName() + ")";
                    boolean live = m != null && !m.isAnnotationPresent(Deprecated.class);
                    if (withLabel && !live) {
                        problems.add(name + " is missing although the query has withLabel");
                    } else if (!withLabel && live) {
                        problems.add(name + " exists but the query has no withLabel: deprecate it");
                    }
                }
            }
            if (withLabel) {
                labelled++;
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
        assertTrue(labelled >= 20, "only " + labelled + " labelled elements found");
    }

    private static boolean queryHasLabelFilters(Class<?> type) {
        if (!Findable.class.isAssignableFrom(type)) {
            return false;
        }
        Page page = mock(Page.class);
        when(page.locator(anyString())).thenReturn(mock(Locator.class, RETURNS_DEEP_STUBS));
        return findRaw(page, type) instanceof LabelFilters<?>;
    }

    private static Method declaredStatic(Class<?> type, String name, Class<?> scope) {
        try {
            Method m = type.getDeclaredMethod(name, scope, String.class);
            return Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) ? m : null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object findRaw(Page page, Class<?> type) {
        return ElementQueries.find(page, (Class) type);
    }

    private static List<Class<?>> elementClasses() throws Exception {
        String pkg = VaadinElement.class.getPackageName();
        URL url = VaadinElement.class.getResource(VaadinElement.class.getSimpleName() + ".class");
        File dir = new File(url.toURI()).getParentFile();
        List<Class<?>> types = new ArrayList<>();
        for (String file : dir.list()) {
            if (!file.endsWith(".class") || file.contains("$")) {
                continue;
            }
            Class<?> type = Class.forName(pkg + "." + file.substring(0, file.length() - ".class".length()));
            if (VaadinElement.class.isAssignableFrom(type) && Modifier.isPublic(type.getModifiers())
                    && !Modifier.isAbstract(type.getModifiers())) {
                types.add(type);
            }
        }
        return types;
    }
}
