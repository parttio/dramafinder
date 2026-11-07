package org.vaadin.addons.dramafinder.element.query;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.EmailFieldElement;
import org.vaadin.addons.dramafinder.element.PlaywrightElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.element.TreeGridElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ElementQueriesTest {

    /** Public element classes deliberately left out of {@code find}: overlay or page-bound wrappers. */
    private static final Set<String> NOT_FINDABLE = Set.of(
            "ContextMenuElement", "NotificationElement");

    @Test
    void everyPublicElementIsFindable() throws Exception {
        List<String> missing = new ArrayList<>();
        int checked = 0;
        for (Class<?> type : elementClasses()) {
            if (NOT_FINDABLE.contains(type.getSimpleName())) {
                continue;
            }
            if (!Findable.class.isAssignableFrom(type) || type.getAnnotation(PlaywrightElement.class) == null) {
                missing.add(type.getSimpleName());
                continue;
            }
            Page page = mock(Page.class);
            Locator candidates = mock(Locator.class, RETURNS_DEEP_STUBS);
            when(page.locator(anyString())).thenReturn(candidates);

            Object query = findRaw(page, type);

            verify(page).locator(type.getAnnotation(PlaywrightElement.class).value());
            ElementQuery<?, ?> elementQuery = (ElementQuery<?, ?>) query;
            assertSame(candidates, elementQuery.getLocator());
            assertInstanceOf(type, elementQuery.single(), type.getSimpleName());
            checked++;
        }
        assertTrue(missing.isEmpty(), "Not findable: " + missing);
        assertTrue(checked > 40, "only " + checked + " element classes found");
    }

    @Test
    void subclassReturnsInstanceOfRequestedClass() {
        Page page = mock(Page.class);
        when(page.locator(anyString())).thenReturn(mock(Locator.class, RETURNS_DEEP_STUBS));

        TextFieldElement email = ElementQueries.find(page, EmailFieldElement.class).single();
        assertInstanceOf(EmailFieldElement.class, email);
        assertInstanceOf(TreeGridElement.class, ElementQueries.find(page, TreeGridElement.class).single());
    }

    @Test
    void filtersReturnNewQueries() {
        Page page = mock(Page.class);
        Locator candidates = mock(Locator.class, RETURNS_SELF);
        Locator narrowed = mock(Locator.class);
        when(page.locator(anyString())).thenReturn(candidates);
        when(candidates.page()).thenReturn(page);
        when(candidates.and(any())).thenReturn(narrowed);

        ButtonQuery<ButtonElement> all = ElementQueries.find(page, ButtonElement.class);
        ButtonQuery<ButtonElement> byId = all.withId("save");

        assertSame(candidates, all.getLocator());
        assertSame(narrowed, byId.getLocator());
        verify(page).locator("[id=\"save\"]");
    }

    @Test
    void scopedFindSearchesInsideScope() {
        Locator scope = mock(Locator.class);
        Locator candidates = mock(Locator.class);
        when(scope.locator("vaadin-button")).thenReturn(candidates);

        assertSame(candidates, ElementQueries.find(scope, ButtonElement.class).getLocator());
    }

    @Test
    void elementWithoutAnnotationIsRejected() {
        Page page = mock(Page.class);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> findRaw(page, Unannotated.class));
        assertTrue(e.getMessage().contains("@PlaywrightElement"), e.getMessage());
    }

    @Test
    void exactTextEscapesRegexAndNormalizesWhitespace() {
        assertEquals("^\\s*Retry\\s+\\(2\\)\\?\\s*$", QueryText.exact("  Retry   (2)? ").pattern());
        assertTrue(QueryText.exact("a.b").matcher("a.b").find());
        assertFalse(QueryText.exact("a.b").matcher("axb").find());
        assertFalse(QueryText.exact("Name").matcher("Nickname").find());
        assertTrue(QueryText.exact("Save draft").matcher(" Save \n draft ").find());
    }

    @Test
    void ownTextSelectorQuotesAndNormalizesText() {
        assertTrue(QueryText.ownTextIs("  Save   draft ").contains("normalize-space()='Save draft'"));
        assertTrue(QueryText.ownTextIs("It's").contains("\"It's\""));
        assertTrue(QueryText.ownTextIs("It's \"x\"").contains("concat('It', \"'\", 's \"x\"')"));
        assertTrue(QueryText.ownTextContains("New").contains("'new'"));
    }

    @Test
    void attributeSelectorEscapesQuotes() {
        assertEquals("[title=\"say \\\"hi\\\"\"]", QueryText.attributeSelector("title", "say \"hi\""));
        assertEquals("[class~=\"primary\"]", QueryText.attributeWordSelector("class", "primary"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object findRaw(Page page, Class<?> type) {
        return ElementQueries.find(page, (Class) type);
    }

    private static List<Class<?>> elementClasses() throws URISyntaxException, ClassNotFoundException {
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

    @SuppressWarnings("unused")
    public static class Unannotated extends VaadinElement implements Findable<ComponentQuery<Unannotated>> {
        public Unannotated(Locator locator) {
            super(locator);
        }
    }
}
