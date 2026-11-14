package org.vaadin.addons.dramafinder.element.query;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Text-matching helpers shared by the query filters.
 */
final class QueryText {

    /** The component's own label: a direct child, so nested fields' labels do not count. */
    static final String OWN_LABEL = ":scope > [slot='label']";

    /**
     * The field's own focusable element — the input, textarea or select value
     * button slotted directly into it — which is where a field's accessible
     * name lives. Fields nested in a group or custom field sit deeper.
     */
    static final String OWN_INPUT = ":scope > :is([slot='input'], [slot='textarea'], [slot='value'])";

    private QueryText() {
    }

    /**
     * Pattern matching {@code text} exactly, ignoring surrounding whitespace and
     * treating inner whitespace runs as equivalent.
     * <p>
     * Escaped by hand because Playwright evaluates the pattern as a JavaScript
     * regular expression, which does not understand {@link Pattern#quote}.
     */
    static Pattern exact(String text) {
        StringBuilder regex = new StringBuilder("^\\s*");
        boolean inWhitespace = false;
        for (char c : text.strip().toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (!inWhitespace) {
                    regex.append("\\s+");
                }
                inWhitespace = true;
                continue;
            }
            inWhitespace = false;
            if ("\\^$.|?*+()[]{}".indexOf(c) >= 0) {
                regex.append('\\');
            }
            regex.append(c);
        }
        return Pattern.compile(regex.append("\\s*$").toString());
    }

    /**
     * Selector keeping a candidate whose own text is exactly {@code text},
     * whitespace-normalized and case-sensitive.
     * <p>
     * The own text is the candidate's light-DOM text without its named-slot
     * children, the way {@code BadgeElement.getText()} reads it: XPath does not
     * enter shadow roots, so a shadow-rendered badge number is never part of
     * it, and children carrying a {@code slot} attribute (a prefix icon, a
     * tooltip, nested items) are left out. Several own text chunks are
     * compared as a whole when no slotted child holds text; otherwise each
     * chunk (a text node or a non-slotted child element) is compared on its own.
     */
    static String ownTextIs(String text) {
        String value = xpathLiteral(normalize(text));
        return "xpath=self::*[(not(*[@slot][normalize-space()]) and normalize-space()=" + value + ")"
                + " or (count(text()[normalize-space()]) = 1 and text()[normalize-space()=" + value + "]"
                + " and not(*[not(@slot)][normalize-space()]))"
                + " or (not(text()[normalize-space()]) and count(*[not(@slot)][normalize-space()]) = 1"
                + " and *[not(@slot)][normalize-space()=" + value + "])]";
    }

    /**
     * Selector keeping a candidate whose own text (see {@link #ownTextIs})
     * contains {@code text}, ignoring the case of ASCII letters.
     */
    static String ownTextContains(String text) {
        String value = xpathLiteral(normalize(text).toLowerCase(java.util.Locale.ROOT));
        String lower = "translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";
        return "xpath=self::*[(not(*[@slot][normalize-space()]) and contains(" + lower + ", " + value + "))"
                + " or text()[contains(" + lower + ", " + value + ")]"
                + " or *[not(@slot)][contains(" + lower + ", " + value + ")]]";
    }

    /**
     * Narrow {@code candidates} to components named {@code name} the way
     * name-from-content roles (button, tab, menu item) are: by
     * {@code aria-label} or {@code aria-labelledby} when set, otherwise by their
     * own text (see {@link #ownTextIs}). Unlike Playwright's role name, this
     * ignores a tooltip slotted into the component, whose text joins the role
     * name while the tooltip is open.
     *
     * @param exact {@code true} for a whole, case-sensitive match; {@code false}
     *              for a case-insensitive substring
     */
    static Locator labelledOrOwnText(Locator candidates, String name, boolean exact) {
        Locator labelled = candidates.locator(labelledBy(name, exact));
        String ownText = exact ? ownTextIs(name) : ownTextContains(name);
        return labelled.or(candidates.locator(UNLABELLED).locator(ownText));
    }

    // Self-scoped: keeps the candidate when it has neither attribute
    private static final String UNLABELLED = "xpath=self::*[not(@aria-label) and not(@aria-labelledby)]";

    /**
     * Selector keeping a candidate that is itself labelled {@code name}, the way
     * Playwright's {@code getByLabel} reads an element that is not a native form
     * control: by the text of any element its {@code aria-labelledby} refers to,
     * or, when it refers to none, by a non-blank {@code aria-label}.
     * <p>
     * Evaluated on the candidate alone ({@code self::}), unlike
     * {@code candidates.and(page.getByLabel(..))}, which computes the labels of
     * every element on the page each time the locator resolves. The referenced
     * elements are looked up in the document, so ids inside another component's
     * shadow root are not followed.
     *
     * @param exact {@code true} for a whole, case-sensitive, whitespace-normalized
     *              match; {@code false} for a substring ignoring the case of ASCII letters
     */
    static String labelledBy(String name, boolean exact) {
        return "xpath=self::*[" + ariaLabelled(name, exact) + "]";
    }

    /**
     * Selector keeping a candidate whose accessible name is {@code name}, read on
     * the candidate itself (see {@link #labelledBy}) or on its own input: an
     * element slotted directly into it as {@code input}, {@code textarea} or
     * {@code value} (see {@link #OWN_INPUT}). For that input, when it has neither
     * a resolving {@code aria-labelledby} nor a non-blank {@code aria-label}, a
     * native control ({@code input} other than hidden, {@code textarea},
     * {@code select}) is also named by a {@code <label for>} pointing at it or a
     * {@code <label>} around it, as Playwright reads {@code element.labels}.
     * <p>
     * One XPath evaluated on each candidate alone: neither the page nor the
     * candidate's shadow DOM is searched, so it costs the same as an id lookup.
     */
    static String accessiblyNamed(String name, boolean exact) {
        String label = "label[" + textMatches("normalize-space()", name, exact) + "]";
        String nativeLabel = "(not(id(@aria-labelledby)) and normalize-space(@aria-label) = ''"
                + " and ((local-name() = 'input' and not(@type = 'hidden'))"
                + " or local-name() = 'textarea' or local-name() = 'select')"
                + " and ((@id and //" + label + "/@for = @id) or ancestor::" + label + "))";
        return "xpath=self::*[" + ariaLabelled(name, exact)
                + " or *[@slot = 'input' or @slot = 'textarea' or @slot = 'value']["
                + ariaLabelled(name, exact) + " or " + nativeLabel + "]]";
    }

    /** XPath condition: the context element is named {@code name} by its ARIA attributes. */
    private static String ariaLabelled(String name, boolean exact) {
        return "id(@aria-labelledby)[" + textMatches("normalize-space()", name, exact) + "]"
                + " or (not(id(@aria-labelledby)) and normalize-space(@aria-label) != ''"
                + " and " + textMatches("normalize-space(@aria-label)", name, exact) + ")";
    }

    private static String textMatches(String normalizedText, String name, boolean exact) {
        if (exact) {
            return normalizedText + "=" + xpathLiteral(normalize(name));
        }
        return "contains(translate(" + normalizedText + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), "
                + xpathLiteral(normalize(name).toLowerCase(java.util.Locale.ROOT)) + ")";
    }

    /** Collapse whitespace runs the way XPath's {@code normalize-space} does. */
    private static String normalize(String text) {
        return text.strip().replaceAll("\\s+", " ");
    }

    /** XPath 1.0 string literal for {@code value}; XPath has no escape character. */
    private static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "', \"'\", '") + "')";
    }

    /**
     * Describe {@code refined} as the description of {@code candidates}
     * followed by {@code description}; {@code candidates} without a description
     * contribute nothing.
     */
    static Locator describe(Locator refined, Locator candidates, String description) {
        String previous = candidates.description();
        return refined.describe(previous == null ? description : previous + " " + description);
    }

    /** Readable filter description: {@code what "value"}. */
    static String quote(String what, String value) {
        return what + " \"" + value + "\"";
    }

    /** CSS attribute selector {@code [name="value"]} with the value escaped. */
    static String attributeSelector(String name, String value) {
        return "[" + name + "=" + quoted(value) + "]";
    }

    /** CSS attribute selector {@code [name~="word"]}: {@code word} among the space-separated values. */
    static String attributeWordSelector(String name, String word) {
        return "[" + name + "~=" + quoted(word) + "]";
    }

    private static String quoted(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
