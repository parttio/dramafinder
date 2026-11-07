package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Filter by accessible name for components named through a label,
 * {@code aria-label} or {@code aria-labelledby}.
 *
 * @param <Q> the query type returned by the filter
 */
public interface AccessibleNameFilters<Q> extends Refinable<Q> {

    /**
     * Keep components that are, or contain, an element whose label,
     * {@code aria-label} or {@code aria-labelledby} text is exactly
     * {@code name} (case-sensitive, whitespace-normalized).
     * <p>
     * Unlike {@code withLabel} this also finds a field named only by
     * {@code aria-label}; a field's placeholder is not its name.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    default Q withAccessibleName(String name) {
        return refine(candidates -> {
            Locator named = candidates.page().getByLabel(name, new Page.GetByLabelOptions().setExact(true));
            return candidates.and(named)
                    .or(candidates.filter(new Locator.FilterOptions().setHas(named)));
        });
    }
}
