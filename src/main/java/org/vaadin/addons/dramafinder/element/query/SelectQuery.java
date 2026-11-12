package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for {@code vaadin-select}: adds the label filters, and a placeholder
 * filter that reads the placeholder where the select renders it.
 *
 * @param <T> the element type the terminals return
 */
public class SelectQuery<T extends VaadinElement> extends ElementQuery<T, SelectQuery<T>>
        implements LabelFilters<SelectQuery<T>>, PlaceholderFilters<SelectQuery<T>> {

    /** The value button while it shows the placeholder rather than a value. */
    private static final String PLACEHOLDER_SHOWN = ":scope > [slot='value'][placeholder]";

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public SelectQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep selects showing exactly {@code placeholder} (case-sensitive,
     * whitespace-normalized).
     * <p>
     * {@code vaadin-select} does not reflect its placeholder as an attribute; it
     * renders it in the value button while nothing is selected, so a select
     * matches only as long as it has no value.
     *
     * @param placeholder the full placeholder text
     * @return a new, narrowed query
     */
    @Override
    public SelectQuery<T> withPlaceholder(String placeholder) {
        return refine(QueryText.quote("with placeholder", placeholder),
                candidates -> candidates.filter(new Locator.FilterOptions()
                .setHas(candidates.page().locator(PLACEHOLDER_SHOWN)
                        .filter(new Locator.FilterOptions().setHasText(QueryText.exact(placeholder))))));
    }
}
