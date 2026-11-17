package org.vaadin.addons.dramafinder.element.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.GenericElement;
import org.vaadin.addons.dramafinder.element.PlaywrightElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;
import org.vaadin.addons.dramafinder.element.utils.TagNames;

/**
 * Entry point of the typed query builder:
 * {@code find(page, TextFieldElement.class).withLabel("Name").single()}.
 * <p>
 * The element class decides the query type through {@link Findable}, so only
 * the filters that fit the component compile. The tag searched for comes from
 * the class's {@link PlaywrightElement} annotation. To search inside a
 * component, use {@link VaadinElement#find(Class)} or
 * {@link #find(Locator, Class)}.
 * <p>
 * Candidates are searched in the light DOM: an element inside a shadow root —
 * the internals a Vaadin component renders itself, such as the
 * {@code vaadin-input-container} of every text field — is left out, so a
 * query counts the components the application created. Flow puts a
 * component's children in the light DOM, inside overlays too. Content rendered
 * in a shadow root by the application (a Lit template) needs
 * {@link ElementQuery#includingShadowDom()}.
 */
public final class ElementQueries {

    private static final ClassValue<Class<?>> QUERY_TYPES = new ClassValue<>() {
        @Override
        protected Class<?> computeValue(Class<?> elementType) {
            return resolveQueryType(elementType);
        }
    };

    // Playwright's CSS engine without shadow-root piercing: candidates are light-DOM elements.
    static final String LIGHT_DOM = "css:light=";

    private ElementQueries() {
    }

    /**
     * Start a query for every {@code type} component on the page.
     *
     * @param page the Playwright page
     * @param type the element class, annotated with {@link PlaywrightElement}
     * @param <T>  the element type
     * @param <Q>  the query type bound to {@code T} by {@link Findable}
     * @return a query over every component of that type on the page
     * @throws IllegalArgumentException if {@code type} lacks the annotation
     */
    public static <T extends VaadinElement & Findable<Q>, Q extends ElementQuery<? super T, Q>> Q find(
            Page page, Class<T> type) {
        String tag = tagOf(type);
        return query(type, new ElementQuery.CssSelector(page::locator, LIGHT_DOM + tag, tag));
    }

    /**
     * Start a query for every {@code type} component inside {@code scope}.
     *
     * @param scope the locator whose light-DOM subtree is searched (the scope
     *              itself is not a candidate)
     * @param type  the element class, annotated with {@link PlaywrightElement}
     * @param <T>   the element type
     * @param <Q>   the query type bound to {@code T} by {@link Findable}
     * @return a query over every component of that type inside the scope
     * @throws IllegalArgumentException if {@code type} lacks the annotation
     */
    public static <T extends VaadinElement & Findable<Q>, Q extends ElementQuery<? super T, Q>> Q find(
            Locator scope, Class<T> type) {
        String tag = tagOf(type);
        String scopeDescription = scope.description();
        return query(type, new ElementQuery.CssSelector(scope::locator, LIGHT_DOM + tag,
                scopeDescription == null ? tag : tag + " inside " + scopeDescription));
    }

    /**
     * Start a query for every component with the tag {@code tag} on the page,
     * for components that have no element class: an add-on, a custom component,
     * a layout or a plain HTML element.
     * <p>
     * The query offers the label, text and placeholder filters next to the ones
     * every query has, and returns {@link GenericElement}s. Like
     * {@link #find(Page, Class)}, it searches the light DOM. Prefer the
     * element-class overload when the component has one.
     * <p>
     * {@link VaadinElement#ANY_TAG} ({@code "*"}) searches every element,
     * whatever its tag, e.g. {@code find(page, ANY_TAG).withTestId("billing")}.
     * It accepts the same tag names as
     * {@link VaadinElement#getLightDomChildren(String)}.
     *
     * @param page the Playwright page
     * @param tag  the tag name, e.g. {@code my-component} (case-insensitive),
     *             or {@link VaadinElement#ANY_TAG} ({@code "*"}) for any element
     * @return a query over every element with that tag on the page
     * @throws IllegalArgumentException if {@code tag} is not a valid tag name
     */
    public static TagQuery<GenericElement> find(Page page, String tag) {
        String name = TagNames.normalize(tag);
        return tagQuery(new ElementQuery.CssSelector(page::locator, LIGHT_DOM + TagNames.toCss(name),
                describe(name)));
    }

    /**
     * Start a query for every component with the tag {@code tag} inside
     * {@code scope}; see {@link #find(Page, String)}.
     *
     * @param scope the locator whose light-DOM subtree is searched (the scope
     *              itself is not a candidate)
     * @param tag   the tag name, e.g. {@code my-component} (case-insensitive),
     *              or {@link VaadinElement#ANY_TAG} ({@code "*"}) for any element
     * @return a query over every element with that tag inside the scope
     * @throws IllegalArgumentException if {@code tag} is not a valid tag name
     */
    public static TagQuery<GenericElement> find(Locator scope, String tag) {
        String name = TagNames.normalize(tag);
        String scopeDescription = scope.description();
        return tagQuery(new ElementQuery.CssSelector(scope::locator, LIGHT_DOM + TagNames.toCss(name),
                scopeDescription == null ? describe(name) : describe(name) + " inside " + scopeDescription));
    }

    private static TagQuery<GenericElement> tagQuery(ElementQuery.CssSelector candidates) {
        TagQuery<GenericElement> query = new TagQuery<>(GenericElement.class, candidates.locator());
        query.startCss(candidates);
        return query;
    }

    private static String describe(String tag) {
        return TagNames.ANY_TAG.equals(tag) ? "any element" : tag;
    }

    @SuppressWarnings("unchecked")
    private static <Q> Q query(Class<?> type, ElementQuery.CssSelector candidates) {
        var query = (ElementQuery<?, ?>) newQuery(QUERY_TYPES.get(type), type, candidates.locator());
        if (ElementQuery.CssSelector.canExtend(candidates.selector())) {
            query.startCss(candidates);
        }
        return (Q) query;
    }

    private static String tagOf(Class<?> type) {
        PlaywrightElement annotation = type.getAnnotation(PlaywrightElement.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    type.getSimpleName() + " is not annotated with @PlaywrightElement");
        }
        return annotation.value();
    }

    static Object newQuery(Class<?> queryType, Class<?> elementType, Locator candidates) {
        try {
            Constructor<?> constructor = queryType.getConstructor(Class.class, Locator.class);
            return constructor.newInstance(elementType, candidates);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + queryType.getSimpleName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(queryType.getSimpleName()
                    + " needs a public (Class, Locator) constructor", e);
        }
    }

    static <T> T newElement(Class<T> type, Locator locator) {
        try {
            return type.getConstructor(Locator.class).newInstance(locator);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type.getSimpleName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(type.getSimpleName()
                    + " needs a public (Locator) constructor", e);
        }
    }

    /** Find the raw class of {@code Q} in the nearest {@code Findable<Q>} supertype. */
    private static Class<?> resolveQueryType(Class<?> elementType) {
        for (Class<?> c = elementType; c != null; c = c.getSuperclass()) {
            Deque<Type> pending = new ArrayDeque<>(List.of(c.getGenericInterfaces()));
            while (!pending.isEmpty()) {
                Type candidate = pending.poll();
                if (candidate instanceof ParameterizedType parameterized
                        && parameterized.getRawType() == Findable.class) {
                    Type argument = parameterized.getActualTypeArguments()[0];
                    if (argument instanceof ParameterizedType p) {
                        return (Class<?>) p.getRawType();
                    }
                    if (argument instanceof Class<?> queryClass) {
                        return queryClass;
                    }
                    throw new IllegalArgumentException(elementType.getSimpleName()
                            + " must bind Findable to a concrete query class, not " + argument);
                }
                Class<?> raw = candidate instanceof ParameterizedType p
                        ? (Class<?>) p.getRawType() : (Class<?>) candidate;
                pending.addAll(List.of(raw.getGenericInterfaces()));
            }
        }
        throw new IllegalArgumentException(elementType.getSimpleName() + " does not implement Findable");
    }
}
