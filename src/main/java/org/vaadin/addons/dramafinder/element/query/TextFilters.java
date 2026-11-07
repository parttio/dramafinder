package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;

/**
 * Filters for components identified by the text they show, such as
 * {@code vaadin-button} or {@code vaadin-tab}.
 * <p>
 * The text is the component's whole text content, slotted children included,
 * as Playwright's {@code hasText} reads it.
 *
 * @param <Q> the query type returned by each filter
 */
public interface TextFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose text is exactly {@code text}.
     * <p>
     * The match is case-sensitive and whitespace-normalized: surrounding
     * whitespace is ignored and inner whitespace runs match any whitespace run.
     *
     * @param text the full text
     * @return a new, narrowed query
     */
    default Q withText(String text) {
        return refine(candidates -> candidates.filter(
                new Locator.FilterOptions().setHasText(QueryText.exact(text))));
    }

    /**
     * Keep components whose text contains {@code text}, matched
     * case-insensitively as a substring.
     *
     * @param text part of the text
     * @return a new, narrowed query
     */
    default Q withTextContaining(String text) {
        return refine(candidates -> candidates.filter(
                new Locator.FilterOptions().setHasText(text)));
    }
}
