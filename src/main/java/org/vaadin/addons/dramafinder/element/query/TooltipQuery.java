package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for {@code vaadin-tooltip}: the text filters read the tooltip text,
 * which lives in the {@code role="tooltip"} overlay child rather than in the
 * host; it is also the tooltip's accessible name.
 *
 * @param <T> the element type the terminals return
 */
public class TooltipQuery<T extends VaadinElement> extends ElementQuery<T, TooltipQuery<T>>
        implements TextFilters<TooltipQuery<T>> {

    private static final String TOOLTIP_TEXT = ":scope > [role='tooltip']";

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public TooltipQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep tooltips whose text is exactly {@code text} (case-sensitive,
     * whitespace-normalized), whether or not the tooltip is showing.
     *
     * @param text the full tooltip text
     * @return a new, narrowed query
     */
    @Override
    public TooltipQuery<T> withText(String text) {
        return refine(QueryText.quote("with text", text), l -> l.filter(new Locator.FilterOptions()
                .setHas(l.page().locator(TOOLTIP_TEXT).locator(QueryText.ownTextIs(text)))));
    }

    /**
     * Keep tooltips whose text contains {@code text}, ignoring the case of
     * ASCII letters.
     *
     * @param text part of the tooltip text
     * @return a new, narrowed query
     */
    @Override
    public TooltipQuery<T> withTextContaining(String text) {
        return refine(QueryText.quote("with text containing", text), l -> l.filter(new Locator.FilterOptions()
                .setHas(l.page().locator(TOOLTIP_TEXT).locator(QueryText.ownTextContains(text)))));
    }

    /**
     * Keep tooltips whose accessible name — their text — is exactly
     * {@code name}; same as {@link #withText(String)}.
     *
     * @param name the full tooltip text
     * @return a new, narrowed query
     */
    @Override
    public TooltipQuery<T> withAccessibleName(String name) {
        return withText(name);
    }
}
