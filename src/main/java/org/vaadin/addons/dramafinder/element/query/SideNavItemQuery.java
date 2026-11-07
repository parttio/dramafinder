package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for {@code vaadin-side-nav-item}: the label filters read the item's own
 * text, without its prefix, suffix or nested child items.
 *
 * @param <T> the element type the terminals return
 */
public class SideNavItemQuery<T extends VaadinElement> extends ElementQuery<T, SideNavItemQuery<T>>
        implements LabelFilters<SideNavItemQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public SideNavItemQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep items whose own label text is exactly {@code label} (case-sensitive,
     * whitespace-normalized); a parent item does not match its children's labels.
     *
     * @param label the full item label
     * @return a new, narrowed query
     */
    @Override
    public SideNavItemQuery<T> withLabel(String label) {
        return refine(l -> l.locator(QueryText.ownTextIs(label)));
    }

    /**
     * Keep items whose own label text contains {@code text}, ignoring the case
     * of ASCII letters.
     *
     * @param text part of the item label
     * @return a new, narrowed query
     */
    @Override
    public SideNavItemQuery<T> withLabelContaining(String text) {
        return refine(l -> l.locator(QueryText.ownTextContains(text)));
    }
}
