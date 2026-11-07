package org.vaadin.addons.dramafinder.element.query;

/**
 * Marker that ties an element class to the query type returned by
 * {@link ElementQueries#find(com.microsoft.playwright.Page, Class) find}.
 * <p>
 * The type argument names the query whose filters fit the component, so
 * {@code find(page, TextFieldElement.class)} offers {@code withLabel} and
 * {@code find(page, ButtonElement.class)} offers {@code withText} instead — a
 * filter that cannot apply to a type does not compile. The element class must
 * also carry {@link org.vaadin.addons.dramafinder.element.PlaywrightElement}
 * (it supplies the tag) and a public {@code (Locator)} constructor; the query
 * class needs a public {@code (Class, Locator)} constructor.
 * <p>
 * A subclass inherits its parent's binding, because Java does not let a class
 * implement two parameterizations of one interface: {@code find(page,
 * TreeGridElement.class)} matches only tree grids and returns
 * {@code TreeGridElement} instances, but their static type is
 * {@code GridElement}.
 *
 * @param <Q> the query type used to look up this element
 */
public interface Findable<Q extends ElementQuery<?, Q>> {
}
