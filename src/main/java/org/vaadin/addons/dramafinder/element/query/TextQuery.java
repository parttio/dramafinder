package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for components identified by the text they show, such as {@code vaadin-badge}
 * or {@code vaadin-tab}: adds text filters.
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
}
