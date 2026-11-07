package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for labelled fields without a placeholder, such as {@code vaadin-checkbox} or
 * {@code vaadin-radio-group}: adds label and accessible-name filters.
 *
 * @param <T> the element type the terminals return
 */
public class FieldQuery<T extends VaadinElement> extends ElementQuery<T, FieldQuery<T>>
        implements LabelFilters<FieldQuery<T>>, AccessibleNameFilters<FieldQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public FieldQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }
}
