package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for components found by tag rather than by element class, returned by
 * {@link ElementQueries#find(com.microsoft.playwright.Page, String)}: a
 * component with no element class of its own may have a label, text or a
 * placeholder, so all of those filters are offered, next to the ones every
 * query has.
 * <p>
 * Each filter reads the component the way the element-class queries do: the
 * label is the {@code slot="label"} child, the text is the component's own
 * light-DOM text, the placeholder is the host's {@code placeholder} attribute.
 *
 * @param <T> the element type the terminals return
 */
public class TagQuery<T extends VaadinElement> extends ElementQuery<T, TagQuery<T>>
        implements LabelFilters<TagQuery<T>>, TextFilters<TagQuery<T>>, PlaceholderFilters<TagQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, String) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public TagQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }
}
