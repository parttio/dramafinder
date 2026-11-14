package org.vaadin.addons.dramafinder.element.query;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
    // While every filter so far can be written in CSS: the selector they add up to, so the
    // candidates stay one CSS selector instead of a chain of page-wide intersections.
    // Null once a filter CSS cannot express has been applied, or for a query built directly.
    private CssSelector cssSelector;

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
        Locator refined = refinement.apply(candidates);
        if (refined.description() == null && candidates.description() != null) {
            // An undescribed custom filter keeps what the query matched so far readable.
            refined = QueryText.describe(refined, candidates, "with a custom filter");
        }
        return (Q) ElementQueries.newQuery(getClass(), type, refined);
    }

    /**
     * Narrow the candidates with CSS that applies to the component itself, e.g.
     * {@code [id="x"]}. While the query is still a single CSS selector, the CSS is
     * appended to it, so the candidates stay one selector; otherwise it is
     * intersected with {@link Locator#and(Locator)}.
     */
    @SuppressWarnings("unchecked")
    Q refineCss(String description, String css) {
        if (cssSelector == null) {
            return refine(description, l -> l.and(l.page().locator(css)));
        }
        CssSelector next = cssSelector.append(css, description);
        Q query = (Q) ElementQueries.newQuery(getClass(), type, next.locator());
        ((ElementQuery<?, ?>) query).cssSelector = next;
        return query;
    }

    /** Let later CSS filters extend {@code selector} instead of intersecting. */
    void startCss(CssSelector selector) {
        this.cssSelector = selector;
    }

    // ── Filters available on every component ───────────────────────────

    /**
     * Keep components whose {@code id} is {@code id}.
     *
     * @param id the exact id
     * @return a new, narrowed query
     */
    public Q withId(String id) {
        return refineCss(QueryText.quote("with id", id), QueryText.attributeSelector("id", id));
    }

    /**
     * Keep components whose test id is {@code testId}.
     * <p>
     * The test id is the {@code data-testid} attribute, which Flow's
     * {@code Component.setTestId(String)} sets.
     *
     * @param testId the exact test id
     * @return a new, narrowed query
     */
    public Q withTestId(String testId) {
        return refineCss(QueryText.quote("with test id", testId),
                QueryText.attributeSelector("data-testid", testId));
    }

    /**
     * Keep components that carry the attribute {@code name}, whatever its value.
     *
     * @param name the attribute name
     * @return a new, narrowed query
     */
    public Q withAttribute(String name) {
        return refineCss("with attribute " + name, "[" + name + "]");
    }

    /**
     * Keep components whose attribute {@code name} is exactly {@code value}.
     *
     * @param name  the attribute name
     * @param value the exact attribute value
     * @return a new, narrowed query
     */
    public Q withAttribute(String name, String value) {
        return refineCss(QueryText.quote("with attribute " + name + " =", value),
                QueryText.attributeSelector(name, value));
    }

    /**
     * Keep components that have {@code className} among their CSS classes.
     *
     * @param className a single class name
     * @return a new, narrowed query
     */
    public Q withClassName(String className) {
        return refineCss(QueryText.quote("with class", className),
                QueryText.attributeWordSelector("class", className));
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
        return wrap(QueryText.describe(candidates.first(), candidates, "(first)"));
    }

    /**
     * The matching element at {@code index} in DOM order.
     *
     * @param index the 0-based position among the matches
     * @return the element; lazy like {@link #single()}
     */
    public T get(int index) {
        return wrap(QueryText.describe(candidates.nth(index), candidates,
                "(index " + index + ")"));
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

    /**
     * A query's candidates as one CSS selector searched from {@code root} (the
     * page or a scope locator), with the readable description they carry.
     */
    record CssSelector(Function<String, Locator> root, String selector, String description) {

        Locator locator() {
            return root.apply(selector).describe(description);
        }

        CssSelector append(String css, String filterDescription) {
            return new CssSelector(root, selector + css, description + " " + filterDescription);
        }

        /**
         * Whether CSS for the component itself can be appended to {@code selector}:
         * plain CSS, with no Playwright engine prefix, no {@code >>} chain and no
         * top-level comma (appending to {@code a, b} would only narrow {@code b}).
         */
        static boolean canExtend(String selector) {
            if (selector.contains(">>") || selector.matches("^[a-zA-Z_-]+=.*")) {
                return false;
            }
            int depth = 0;
            char quote = 0;
            for (char c : selector.toCharArray()) {
                if (quote != 0) {
                    quote = c == quote ? 0 : quote;
                } else if (c == '"' || c == '\'') {
                    quote = c;
                } else if (c == '(' || c == '[') {
                    depth++;
                } else if (c == ')' || c == ']') {
                    depth--;
                } else if (c == ',' && depth == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
