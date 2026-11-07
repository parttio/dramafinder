package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query offering only the filters every component supports: id, test id,
 * attributes, class name and accessible name.
 *
 * @param <T> the element type the terminals return
 */
public class ComponentQuery<T extends VaadinElement> extends ElementQuery<T, ComponentQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public ComponentQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }
}
