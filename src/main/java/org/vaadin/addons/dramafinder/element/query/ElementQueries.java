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
import org.vaadin.addons.dramafinder.element.PlaywrightElement;
import org.vaadin.addons.dramafinder.element.VaadinElement;

/**
 * Entry point of the typed query builder:
 * {@code find(page, TextFieldElement.class).withLabel("Name").single()}.
 * <p>
 * The element class decides the query type through {@link Findable}, so only
 * the filters that fit the component compile. The tag searched for comes from
 * the class's {@link PlaywrightElement} annotation. To search inside a
 * component, use {@link VaadinElement#find(Class)} or
 * {@link #find(Locator, Class)}.
 */
public final class ElementQueries {

    private static final ClassValue<Class<?>> QUERY_TYPES = new ClassValue<>() {
        @Override
        protected Class<?> computeValue(Class<?> elementType) {
            return resolveQueryType(elementType);
        }
    };

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
        return query(type, page.locator(tagOf(type)));
    }

    /**
     * Start a query for every {@code type} component inside {@code scope}.
     *
     * @param scope the locator whose subtree is searched (the scope itself is
     *              not a candidate)
     * @param type  the element class, annotated with {@link PlaywrightElement}
     * @param <T>   the element type
     * @param <Q>   the query type bound to {@code T} by {@link Findable}
     * @return a query over every component of that type inside the scope
     * @throws IllegalArgumentException if {@code type} lacks the annotation
     */
    public static <T extends VaadinElement & Findable<Q>, Q extends ElementQuery<? super T, Q>> Q find(
            Locator scope, Class<T> type) {
        return query(type, scope.locator(tagOf(type)));
    }

    @SuppressWarnings("unchecked")
    private static <Q> Q query(Class<?> type, Locator candidates) {
        return (Q) newQuery(QUERY_TYPES.get(type), type, candidates);
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
