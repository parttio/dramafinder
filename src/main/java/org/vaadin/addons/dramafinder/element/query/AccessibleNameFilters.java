package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Filter by accessible name, available on every query.
 * <p>
 * By default the name is the one given by {@code aria-label},
 * {@code aria-labelledby} or an associated {@code <label>}, read on the
 * component itself or on its own input (the {@code input}, {@code textarea} or
 * {@code value} slotted directly into a field). Components nested inside it are
 * not considered, so a dialog is not matched by the name of a field it holds.
 * Queries for components named by their content (buttons, tabs) refine this.
 *
 * @param <Q> the query type returned by the filter
 */
public interface AccessibleNameFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose accessible name is exactly {@code name}
     * (case-sensitive, whitespace-normalized).
     * <p>
     * Unlike {@code withLabel} this also finds a component named only by
     * {@code aria-label}; a field's placeholder is not its name.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    default Q withAccessibleName(String name) {
        return refine(candidates -> {
            Locator named = candidates.page().getByLabel(name, new Page.GetByLabelOptions().setExact(true));
            return candidates.and(named).or(candidates.filter(new Locator.FilterOptions()
                    .setHas(candidates.page().locator(QueryText.OWN_INPUT).and(named))));
        });
    }
}
