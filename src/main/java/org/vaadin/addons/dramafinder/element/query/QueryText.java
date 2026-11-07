package org.vaadin.addons.dramafinder.element.query;

import java.util.regex.Pattern;

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
