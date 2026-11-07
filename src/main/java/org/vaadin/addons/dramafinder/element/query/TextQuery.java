package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for components identified by the text they show, such as
 * {@code vaadin-badge} or {@code vaadin-tab}: adds text filters, and reads the
 * accessible name from the text when no {@code aria-label} or
 * {@code aria-labelledby} is set.
 *
 * @param <T> the element type the terminals return
 */
public class TextQuery<T extends VaadinElement> extends ElementQuery<T, TextQuery<T>>
        implements TextFilters<TextQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public TextQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep components whose accessible name is exactly {@code name}
     * (case-sensitive, whitespace-normalized): the {@code aria-label} or
     * {@code aria-labelledby} text when set, otherwise the component's own text.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    @Override
    public TextQuery<T> withAccessibleName(String name) {
        return refine(l -> QueryText.labelledOrOwnText(l, name, true));
    }

    /**
     * Keep components whose accessible name — as read by
     * {@link #withAccessibleName(String)} — contains {@code text}, ignoring the
     * case of ASCII letters.
     *
     * @param text part of the accessible name
     * @return a new, narrowed query
     */
    @Override
    public TextQuery<T> withAccessibleNameContaining(String text) {
        return refine(l -> QueryText.labelledOrOwnText(l, text, false));
    }
}
