package org.vaadin.addons.dramafinder.element.query;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ElementQueriesTest {

    /**
     * Public element classes deliberately left out of {@code find(page, Class)}: overlay or
     * page-bound wrappers, and the tagless GenericElement, which {@code find(page, tag)} returns.
     */
    private static final Set<String> NOT_FINDABLE = Set.of(
            "ContextMenuElement", "GenericElement");

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
            when(candidates.describe(anyString())).thenReturn(candidates);

            Object query = findRaw(page, type);

            verify(page).locator("css:light=" + type.getAnnotation(PlaywrightElement.class).value());
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
        Map<String, Locator> locators = selfDescribingLocators(page);

        ButtonQuery<ButtonElement> all = ElementQueries.find(page, ButtonElement.class);
        ButtonQuery<ButtonElement> byId = all.withId("save");

        assertSame(locators.get("css:light=vaadin-button"), all.getLocator());
        assertSame(locators.get("css:light=vaadin-button[id=\"save\"]"), byId.getLocator());
        verify(byId.getLocator()).describe("vaadin-button with id \"save\"");
    }

    @Test
    void cssFiltersCompileIntoOneSelector() {
        Page page = mock(Page.class);
        Map<String, Locator> locators = selfDescribingLocators(page);

        var query = ElementQueries.find(page, ButtonElement.class)
                .withId("users").withClassName("x").withAttribute("theme", "primary").withTheme("small");

        String selector = "css:light=vaadin-button[id=\"users\"][class~=\"x\"][theme=\"primary\"]"
                + "[theme~=\"small\"]";
        assertSame(locators.get(selector), query.getLocator());
        verify(query.getLocator()).describe("vaadin-button with id \"users\" with class \"x\""
                + " with attribute theme = \"primary\" with theme \"small\"");
        locators.values().forEach(l -> verify(l, never()).and(any()));
    }

    @Test
    void placeholderContainingIsACaseInsensitiveSubstring() {
        Page page = mock(Page.class);
        Map<String, Locator> locators = selfDescribingLocators(page);

        var query = ElementQueries.find(page, TextFieldElement.class).withPlaceholderContaining("str\"eet");

        assertSame(locators.get("css:light=vaadin-text-field[placeholder*=\"str\\\"eet\" i]"), query.getLocator());
    }

    @Test
    void includingShadowDomDropsTheLightDomEngine() {
        Page page = mock(Page.class);
        Map<String, Locator> locators = selfDescribingLocators(page);

        var query = ElementQueries.find(page, ButtonElement.class).withId("x").includingShadowDom();

        assertSame(locators.get("vaadin-button[id=\"x\"]"), query.getLocator());
        verify(query.getLocator()).describe("vaadin-button with id \"x\" including shadow DOM");
        // later CSS filters keep extending the piercing selector
        Locator narrowed = query.withClassName("y").getLocator();
        assertSame(locators.get("vaadin-button[id=\"x\"][class~=\"y\"]"), narrowed);
    }

    @Test
    void includingShadowDomAfterANonCssFilterIsRejected() {
        Page page = mock(Page.class);
        selfDescribingLocators(page);
        var query = ElementQueries.find(page, ButtonElement.class).refine(l -> l);

        assertThrows(IllegalStateException.class, query::includingShadowDom);
    }

    @Test
    void cssFiltersAfterOtherFiltersIntersect() {
        Page page = mock(Page.class);
        Map<String, Locator> locators = selfDescribingLocators(page);
        Locator textFiltered = mock(Locator.class, RETURNS_SELF);
        Locator intersected = mock(Locator.class, RETURNS_SELF);
        when(textFiltered.page()).thenReturn(page);
        when(textFiltered.and(any())).thenReturn(intersected);

        var query = ElementQueries.find(page, ButtonElement.class)
                .refine(l -> textFiltered)
                .withId("save");

        // A filter CSS cannot express ends the single selector: later ones intersect
        assertSame(intersected, query.getLocator());
        verify(textFiltered).and(locators.get("[id=\"save\"]"));
    }

    @Test
    void onlyPlainCssSelectorsAreExtended() {
        assertTrue(ElementQuery.CssSelector.canExtend("vaadin-button"));
        assertTrue(ElementQuery.CssSelector.canExtend("css:light=vaadin-button"));
        assertFalse(ElementQuery.CssSelector.canExtend("css:light=vaadin-button, vaadin-icon"));
        assertTrue(ElementQuery.CssSelector.canExtend("vaadin-dialog[opened]"));
        assertTrue(ElementQuery.CssSelector.canExtend(":is(vaadin-menu-item:not([hidden]), x[role='a,b'])"));
        assertFalse(ElementQuery.CssSelector.canExtend("vaadin-button, vaadin-icon"));
        assertFalse(ElementQuery.CssSelector.canExtend("xpath=//vaadin-button"));
        assertFalse(ElementQuery.CssSelector.canExtend("vaadin-dialog >> vaadin-button"));
    }

    /** Make {@code page.locator(selector)} return one mock per selector, describing to itself. */
    private static Map<String, Locator> selfDescribingLocators(Page page) {
        Map<String, Locator> locators = new HashMap<>();
        when(page.locator(anyString())).thenAnswer(invocation -> locators.computeIfAbsent(
                invocation.getArgument(0), selector -> {
                    Locator locator = mock(Locator.class);
                    when(locator.describe(anyString())).thenReturn(locator);
                    when(locator.page()).thenReturn(page);
                    return locator;
                }));
        return locators;
    }

    @Test
    void scopedFindSearchesInsideScope() {
        Locator scope = mock(Locator.class);
        Locator candidates = mock(Locator.class);
        when(scope.locator("css:light=vaadin-button")).thenReturn(candidates);
        when(candidates.describe("vaadin-button")).thenReturn(candidates);

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
