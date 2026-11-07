package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for components named by {@code aria-label} or {@code aria-labelledby}, such as
 * {@code vaadin-dialog} or {@code vaadin-menu-bar}: adds the accessible-name filter.
 *
 * @param <T> the element type the terminals return
 */
public class NamedQuery<T extends VaadinElement> extends ElementQuery<T, NamedQuery<T>>
        implements AccessibleNameFilters<NamedQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public NamedQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }
}
