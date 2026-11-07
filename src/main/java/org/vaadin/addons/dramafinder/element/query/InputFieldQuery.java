package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for input fields, such as {@code vaadin-text-field} or {@code vaadin-combo-box}:
 * adds label, accessible-name and placeholder filters.
 *
 * @param <T> the element type the terminals return
 */
public class InputFieldQuery<T extends VaadinElement> extends ElementQuery<T, InputFieldQuery<T>>
        implements LabelFilters<InputFieldQuery<T>>, AccessibleNameFilters<InputFieldQuery<T>>,
        PlaceholderFilters<InputFieldQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public InputFieldQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }
}
