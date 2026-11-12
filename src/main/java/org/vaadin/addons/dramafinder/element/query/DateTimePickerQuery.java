package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for {@code vaadin-date-time-picker}: adds the label filters, and a
 * placeholder filter that reads the date picker's placeholder.
 *
 * @param <T> the element type the terminals return
 */
public class DateTimePickerQuery<T extends VaadinElement> extends ElementQuery<T, DateTimePickerQuery<T>>
        implements LabelFilters<DateTimePickerQuery<T>>, PlaceholderFilters<DateTimePickerQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public DateTimePickerQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep pickers whose date placeholder is exactly {@code placeholder}
     * (case-sensitive).
     * <p>
     * A date-time picker has no placeholder of its own: Flow's
     * {@code setDatePlaceholder} sets it on the {@code vaadin-date-picker} the
     * component is made of, which is where this reads it.
     *
     * @param placeholder the full date placeholder
     * @return a new, narrowed query
     */
    @Override
    public DateTimePickerQuery<T> withPlaceholder(String placeholder) {
        return refine(QueryText.quote("with placeholder", placeholder),
                candidates -> candidates.filter(new Locator.FilterOptions()
                .setHas(candidates.page().locator(":scope > [slot='date-picker']"
                        + QueryText.attributeSelector("placeholder", placeholder)))));
    }
}
