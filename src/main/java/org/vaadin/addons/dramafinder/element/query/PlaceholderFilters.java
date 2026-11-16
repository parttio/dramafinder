package org.vaadin.addons.dramafinder.element.query;

/**
 * Filter for components with a {@code placeholder} attribute on their host,
 * such as {@code vaadin-text-field} or {@code vaadin-combo-box}.
 *
 * @param <Q> the query type returned by the filter
 */
public interface PlaceholderFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose {@code placeholder} is exactly {@code placeholder}
     * (case-sensitive).
     *
     * @param placeholder the full placeholder text
     * @return a new, narrowed query
     */
    @SuppressWarnings("unchecked")
    default Q withPlaceholder(String placeholder) {
        String description = QueryText.quote("with placeholder", placeholder);
        String css = QueryText.attributeSelector("placeholder", placeholder);
        if (this instanceof ElementQuery<?, ?> query) {
            return (Q) query.refineCss(description, css);
        }
        return refine(description, candidates -> candidates.and(candidates.page().locator(css)));
    }

    /**
     * Keep components whose {@code placeholder} contains {@code text}, matched
     * case-insensitively as a substring.
     *
     * @param text part of the placeholder text
     * @return a new, narrowed query
     */
    @SuppressWarnings("unchecked")
    default Q withPlaceholderContaining(String text) {
        String description = QueryText.quote("with placeholder containing", text);
        String css = QueryText.attributeContainsSelector("placeholder", text);
        if (this instanceof ElementQuery<?, ?> query) {
            return (Q) query.refineCss(description, css);
        }
        return refine(description, candidates -> candidates.and(candidates.page().locator(css)));
    }
}
