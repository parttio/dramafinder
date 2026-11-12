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
        Page page = candidates.page();
        Locator labelled = candidates.and(page.getByLabel(name, new Page.GetByLabelOptions().setExact(exact)));
        String ownText = exact ? ownTextIs(name) : ownTextContains(name);
        return labelled.or(candidates.and(page.locator(UNLABELLED)).locator(ownText));
    }

    private static final String UNLABELLED = ":not([aria-label]):not([aria-labelledby])";

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
