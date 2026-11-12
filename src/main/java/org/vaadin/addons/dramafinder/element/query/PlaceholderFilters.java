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
    default Q withPlaceholder(String placeholder) {
        return refine(QueryText.quote("with placeholder", placeholder), candidates -> candidates.and(
                candidates.page().locator(QueryText.attributeSelector("placeholder", placeholder))));
    }
}
