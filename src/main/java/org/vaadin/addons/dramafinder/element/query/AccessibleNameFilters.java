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
     * {@code aria-label}. A field's placeholder is not its name, with one
     * exception: a {@code vaadin-select} with no label announces its
     * placeholder as its name, so it matches it here.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    default Q withAccessibleName(String name) {
        return refine(QueryText.quote("with accessible name", name), candidates -> named(candidates, name, true));
    }

    /**
     * Keep components whose accessible name — as read by
     * {@link #withAccessibleName(String)} — contains {@code text}, matched
     * case-insensitively as a substring.
     * <p>
     * The closest equivalent of the 1.x {@code getByLabel}, which matched the
     * label or {@code aria-label} this way; unlike it, several matches are not
     * reduced to the first one.
     *
     * @param text part of the accessible name
     * @return a new, narrowed query
     */
    default Q withAccessibleNameContaining(String text) {
        return refine(QueryText.quote("with accessible name containing", text),
                candidates -> named(candidates, text, false));
    }

    private static Locator named(Locator candidates, String name, boolean exact) {
        Locator named = candidates.page().getByLabel(name, new Page.GetByLabelOptions().setExact(exact));
        return candidates.and(named).or(candidates.filter(new Locator.FilterOptions()
                .setHas(candidates.page().locator(QueryText.OWN_INPUT).and(named))));
    }
}
