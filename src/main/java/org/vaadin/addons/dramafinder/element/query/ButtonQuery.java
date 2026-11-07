package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for {@code vaadin-button}: adds text filters, and reads the accessible
 * name from the button's text when no {@code aria-label} or
 * {@code aria-labelledby} is set.
 *
 * @param <T> the element type the terminals return
 */
public class ButtonQuery<T extends VaadinElement> extends ElementQuery<T, ButtonQuery<T>>
        implements TextFilters<ButtonQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public ButtonQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep buttons whose accessible name is exactly {@code name}
     * (case-sensitive, whitespace-normalized): the {@code aria-label} or
     * {@code aria-labelledby} text when set — so it finds icon-only buttons —
     * otherwise the button's own text. A tooltip on the button is never part of
     * the name, open or not.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    @Override
    public ButtonQuery<T> withAccessibleName(String name) {
        return refine(l -> QueryText.labelledOrOwnText(l, name, true));
    }

    /**
     * Keep buttons whose accessible name — as read by
     * {@link #withAccessibleName(String)} — contains {@code text}, ignoring the
     * case of ASCII letters.
     *
     * @param text part of the accessible name
     * @return a new, narrowed query
     */
    public ButtonQuery<T> withAccessibleNameContaining(String text) {
        return refine(l -> QueryText.labelledOrOwnText(l, text, false));
    }
}
