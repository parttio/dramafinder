package org.vaadin.addons.dramafinder.element.query;

import java.util.function.UnaryOperator;

import com.microsoft.playwright.Locator;

/**
 * Base contract of the query filter mixins: a query that can be narrowed by
 * transforming its candidate locator.
 *
 * @param <Q> the query type returned by each filter
 */
public interface Refinable<Q> {

    /**
     * Return a query whose candidates are narrowed by {@code refinement}.
     * <p>
     * Escape hatch for conditions no named filter covers; the function receives
     * the current candidates and must return a locator over a subset of them
     * (typically via {@link Locator#filter(Locator.FilterOptions)} or
     * {@link Locator#and(Locator)}). The query this is called on is left
     * unchanged.
     *
     * @param refinement transformation applied to the candidate locator
     * @return a new query over the narrowed candidates
     */
    Q refine(UnaryOperator<Locator> refinement);

    /**
     * Like {@link #refine(UnaryOperator)}, and also add {@code description} to
     * the query's readable description, e.g. {@code with text "Save"}.
     * <p>
     * The description is set with {@link Locator#describe(String)}, so a
     * failing assertion's call log and the trace viewer show
     * {@code vaadin-button with text "Save"} instead of the raw selector.
     * Named filters use this; a custom filter should too.
     *
     * @param description what the filter keeps, appended to the description
     *                    of the candidates
     * @param refinement  transformation applied to the candidate locator
     * @return a new query over the narrowed candidates
     */
    default Q refine(String description, UnaryOperator<Locator> refinement) {
        return refine(candidates -> QueryText.describe(refinement.apply(candidates), candidates, description));
    }
}
