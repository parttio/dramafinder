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
}
