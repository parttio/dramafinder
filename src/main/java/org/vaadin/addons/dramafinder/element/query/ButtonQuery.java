package org.vaadin.addons.dramafinder.element.query;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Query for {@code vaadin-button}: adds text filters and an accessible-name
 * filter that, unlike the field one, also reads a button's name from its text.
 *
 * @param <T> the element type the terminals return
 */
public class ButtonQuery<T extends VaadinElement> extends ElementQuery<T, ButtonQuery<T>>
        implements TextFilters<ButtonQuery<T>>, AccessibleNameFilters<ButtonQuery<T>> {

    /**
     * Create a query over the given candidates; called by
     * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
     *
     * @param type       the element class to wrap each match in
     * @param candidates the components the filters narrow down
     */
    public ButtonQuery(Class<T> type, Locator candidates) {
        super(type, candidates);
    }

    /**
     * Keep buttons whose accessible name is exactly {@code name}
     * (case-sensitive, whitespace-normalized): the {@code aria-label} when set,
     * otherwise the button text — so it finds icon-only buttons too.
     *
     * @param name the full accessible name
     * @return a new, narrowed query
     */
    @Override
    public ButtonQuery<T> withAccessibleName(String name) {
        return refine(l -> l.and(l.page().getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(name).setExact(true))));
    }
}
