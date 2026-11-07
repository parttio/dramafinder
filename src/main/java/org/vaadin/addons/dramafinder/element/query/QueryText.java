package org.vaadin.addons.dramafinder.element.query;

import java.util.regex.Pattern;

/**
 * Text-matching helpers shared by the query filters.
 */
final class QueryText {

    /** The component's own label: a direct child, so nested fields' labels do not count. */
    static final String OWN_LABEL = ":scope > [slot='label']";

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
