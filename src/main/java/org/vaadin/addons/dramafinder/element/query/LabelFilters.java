package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;

/**
 * Filters for components that render a visible label in their
 * {@code slot="label"} child, such as {@code vaadin-text-field} or
 * {@code vaadin-checkbox}.
 *
 * @param <Q> the query type returned by each filter
 */
public interface LabelFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose visible label is exactly {@code label}.
     * <p>
     * The match is case-sensitive and ignores surrounding whitespace; an
     * {@code aria-label} or a placeholder does not count as a label (see
     * {@code withAccessibleName} and {@code withPlaceholder}).
     *
     * @param label the full label text
     * @return a new, narrowed query
     */
    default Q withLabel(String label) {
        return refine(candidates -> candidates.filter(new Locator.FilterOptions()
                .setHas(candidates.page().locator(QueryText.OWN_LABEL)
                        .filter(new Locator.FilterOptions().setHasText(QueryText.exact(label))))));
    }

    /**
     * Keep components whose visible label contains {@code text}, matched
     * case-insensitively as a substring.
     *
     * @param text part of the label text
     * @return a new, narrowed query
     */
    default Q withLabelContaining(String text) {
        return refine(candidates -> candidates.filter(new Locator.FilterOptions()
                .setHas(candidates.page().locator(QueryText.OWN_LABEL)
                        .filter(new Locator.FilterOptions().setHasText(text)))));
    }
}
