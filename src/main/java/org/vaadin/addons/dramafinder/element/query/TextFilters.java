package org.vaadin.addons.dramafinder.element.query;

/**
 * Filters for components identified by the text they show, such as
 * {@code vaadin-button} or {@code vaadin-tab}.
 * <p>
 * The text is the component's own text: its light-DOM content without the
 * children assigned to a named slot, the way {@code BadgeElement.getText()}
 * reads it. A prefix icon, a tooltip or a badge's shadow-rendered number is
 * therefore not part of it.
 *
 * @param <Q> the query type returned by each filter
 */
public interface TextFilters<Q> extends Refinable<Q> {

    /**
     * Keep components whose own text is exactly {@code text}.
     * <p>
     * The match is case-sensitive and whitespace-normalized: surrounding
     * whitespace is ignored and inner whitespace runs match any whitespace run.
     *
     * @param text the full text
     * @return a new, narrowed query
     */
    default Q withText(String text) {
        return refine(candidates -> candidates.locator(QueryText.ownTextIs(text)));
    }

    /**
     * Keep components whose own text contains {@code text}, ignoring the case
     * of ASCII letters.
     *
     * @param text part of the text
     * @return a new, narrowed query
     */
    default Q withTextContaining(String text) {
        return refine(candidates -> candidates.locator(QueryText.ownTextContains(text)));
    }
}
