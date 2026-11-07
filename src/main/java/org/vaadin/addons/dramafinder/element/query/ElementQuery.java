package org.vaadin.addons.dramafinder.element.query;

import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Base class of the typed queries returned by
 * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
 * <p>
 * A query starts from every component of one tag inside its scope and narrows
 * them with filters; it never touches the browser until a terminal such as
 * {@link #exists()} needs a count. Queries are immutable — each filter returns
 * a new query — so one can be kept and branched. The filters every component
 * supports live here, including {@link AccessibleNameFilters#withAccessibleName};
 * component-specific ones come from the mixins a subclass implements
 * ({@link LabelFilters}, {@link TextFilters}, ...).
 * <p>
 * To support a custom component, subclass this (or reuse
 * {@link ComponentQuery}), give it a public {@code (Class, Locator)}
 * constructor, and let the element implement {@link Findable}.
 *
 * @param <T> the element type the terminals return
 * @param <Q> the concrete query type, returned by every filter
 */
public abstract class ElementQuery<T extends VaadinElement, Q extends ElementQuery<T, Q>>
        implements Refinable<Q>, AccessibleNameFilters<Q> {

    private final Class<T> type;
    private final Locator candidates;

    /**
     * Create a query over the given candidates.
     *
     * @param type       the element class to wrap each match in; it must have
     *                   a public {@code (Locator)} constructor
     * @param candidates the components the filters narrow down
     */
    protected ElementQuery(Class<T> type, Locator candidates) {
        this.type = Objects.requireNonNull(type, "type");
        this.candidates = Objects.requireNonNull(candidates, "candidates");
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Q refine(UnaryOperator<Locator> refinement) {
        return (Q) ElementQueries.newQuery(getClass(), type, refinement.apply(candidates));
    }

    // ── Filters available on every component ───────────────────────────

    /**
     * Keep components whose {@code id} is {@code id}.
     *
     * @param id the exact id
     * @return a new, narrowed query
     */
    public Q withId(String id) {
        return withAttribute("id", id);
    }

    /**
     * Keep components whose test id is {@code testId}.
     * <p>
     * The attribute is Playwright's test id attribute, {@code data-testid}
     * unless changed with {@code Selectors.setTestIdAttribute}.
     *
     * @param testId the exact test id
     * @return a new, narrowed query
     */
    public Q withTestId(String testId) {
        return refine(l -> l.and(l.page().getByTestId(testId)));
    }

    /**
     * Keep components that carry the attribute {@code name}, whatever its value.
     *
     * @param name the attribute name
     * @return a new, narrowed query
     */
    public Q withAttribute(String name) {
        return refine(l -> l.and(l.page().locator("[" + name + "]")));
    }

    /**
     * Keep components whose attribute {@code name} is exactly {@code value}.
     *
     * @param name  the attribute name
     * @param value the exact attribute value
     * @return a new, narrowed query
     */
    public Q withAttribute(String name, String value) {
        return refine(l -> l.and(l.page().locator(QueryText.attributeSelector(name, value))));
    }

    /**
     * Keep components that have {@code className} among their CSS classes.
     *
     * @param className a single class name
     * @return a new, narrowed query
     */
    public Q withClassName(String className) {
        return refine(l -> l.and(l.page().locator(QueryText.attributeWordSelector("class", className))));
    }

    // ── Terminals ──────────────────────────────────────────────────────

    /**
     * The element matched by this query, which must match exactly one component.
     * <p>
     * Stays lazy: nothing is resolved until the element is used, and every use
     * re-runs the query. Using it fails with a Playwright strict-mode error when
     * several components match, and times out when none does.
     *
     * @return the element
     */
    public T single() {
        return wrap(candidates);
    }

    /**
     * The first matching element in DOM order, without checking for others.
     *
     * @return the element; lazy like {@link #single()}
     */
    public T first() {
        return wrap(candidates.first());
    }

    /**
     * The matching element at {@code index} in DOM order.
     *
     * @param index the 0-based position among the matches
     * @return the element; lazy like {@link #single()}
     */
    public T get(int index) {
        return wrap(candidates.nth(index));
    }

    /**
     * Every element matching right now, in DOM order.
     * <p>
     * The number of elements is read once, without waiting; each element then
     * points at its position, so it follows the DOM if the list changes.
     *
     * @return the elements, empty when nothing matches
     */
    public List<T> all() {
        return candidates.all().stream().map(this::wrap).toList();
    }

    /**
     * Whether at least one component matches right now. Reads the DOM once,
     * without waiting; to wait for a component, assert on {@link #single()}.
     *
     * @return {@code true} when at least one component matches
     */
    public boolean exists() {
        return count() > 0;
    }

    /**
     * The number of components matching right now, read without waiting.
     *
     * @return the match count
     */
    public int count() {
        return candidates.count();
    }

    /**
     * The locator over every component matching this query, e.g. for
     * {@code assertThat(query.getLocator()).hasCount(2)}.
     *
     * @return the candidate locator
     */
    public Locator getLocator() {
        return candidates;
    }

    private T wrap(Locator locator) {
        return ElementQueries.newElement(type, locator);
    }
}
