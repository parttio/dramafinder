package org.vaadin.addons.dramafinder.element.utils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The tag names the lookups by tag accept, shared so that
 * {@code VaadinElement.getLightDomChildren(..)} and
 * {@code ElementQueries.find(page, tag)} agree on them: a letter followed by
 * letters, digits, {@code .}, {@code _} or {@code -} (what Flow's
 * {@code Element} accepts), or {@link #ANY_TAG} for any element.
 */
public final class TagNames {

    /** The tag argument that matches any element; the same as {@code VaadinElement.ANY_TAG}. */
    public static final String ANY_TAG = "*";

    private static final Pattern TAG_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9._-]*");

    private TagNames() {
    }

    /**
     * Validate a tag name and lower-case it.
     *
     * @param tag the tag name, case-insensitive, or {@link #ANY_TAG}
     * @return the lower-case tag name, or {@link #ANY_TAG}
     * @throws IllegalArgumentException if {@code tag} is {@code null} or not a
     *                                  valid tag name
     */
    public static String normalize(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag must not be null; use \"*\" for any element");
        }
        if (ANY_TAG.equals(tag)) {
            return ANY_TAG;
        }
        if (!TAG_NAME.matcher(tag).matches()) {
            throw new IllegalArgumentException("Not a tag name: '" + tag + "'");
        }
        return tag.toLowerCase(Locale.ROOT);
    }

    /**
     * The CSS type selector for a tag name returned by {@link #normalize}:
     * {@code .} is escaped, since CSS would read it as a class selector.
     *
     * @param normalizedTag a tag name returned by {@link #normalize}
     * @return the CSS selector matching that tag
     */
    public static String toCss(String normalizedTag) {
        return normalizedTag.replace(".", "\\.");
    }
}
