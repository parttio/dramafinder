package org.vaadin.addons.dramafinder.element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads an element's accessible name from Playwright's ARIA snapshot of it,
 * for elements whose name the browser computes from content the library does
 * not own, such as a grid header cell. Playwright has no call that returns an
 * accessible name; the snapshot is the standard API that shows it.
 */
final class AriaSnapshots {

    // The first line of a snapshot: "- role" optionally followed by the quoted name.
    private static final Pattern FIRST_NODE = Pattern.compile("^- [\\w-]+(?: \"((?:[^\"\\\\]|\\\\.)*)\")?");

    private AriaSnapshots() {
    }

    /**
     * The accessible name of the snapshot's root node.
     *
     * @param snapshot the result of {@code Locator.ariaSnapshot()}
     * @return the whitespace-normalized name, or {@code null} when the node has none
     */
    static String name(String snapshot) {
        Matcher matcher = FIRST_NODE.matcher(snapshot);
        if (!matcher.find() || matcher.group(1) == null) {
            return null;
        }
        String name = unescape(matcher.group(1)).strip().replaceAll("\\s+", " ");
        return name.isEmpty() ? null : name;
    }

    // Playwright writes the name as a JSON string literal.
    private static String unescape(String quoted) {
        StringBuilder out = new StringBuilder(quoted.length());
        for (int i = 0; i < quoted.length(); i++) {
            char c = quoted.charAt(i);
            if (c != '\\' || i + 1 == quoted.length()) {
                out.append(c);
                continue;
            }
            char next = quoted.charAt(++i);
            switch (next) {
                case 'n' -> out.append('\n');
                case 't' -> out.append('\t');
                case 'r' -> out.append('\r');
                case 'b' -> out.append('\b');
                case 'f' -> out.append('\f');
                case 'u' -> {
                    out.append((char) Integer.parseInt(quoted.substring(i + 1, i + 5), 16));
                    i += 4;
                }
                default -> out.append(next);
            }
        }
        return out.toString();
    }
}
