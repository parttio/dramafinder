package org.vaadin.addons.dramafinder.element.shared;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components exposing styling: CSS classes, and the inline and
 * computed value of a style property.
 */
public interface HasStyleElement extends HasLocatorElement {

    /** Get the raw {@code class} attribute value. */
    default String getCssClass() {
        return getLocator().getAttribute("class");
    }

    /**
     * Get a property of the component's inline style, the {@code style}
     * attribute that Flow's {@code Component.getStyle()} writes.
     *
     * @param cssProperty the property name in kebab case, e.g.
     *                    {@code background-color} or a custom property such as
     *                    {@code --my-color}
     * @return the value as the browser serializes it, or {@code null} when the
     *         inline style does not set the property
     */
    default String getInlineStyle(String cssProperty) {
        String value = (String) getLocator().evaluate("(el, p) => el.style.getPropertyValue(p)", cssProperty);
        return value == null || value.isEmpty() ? null : value;
    }

    /**
     * Get the computed value of a style property: what the browser applies
     * after every stylesheet, theme and inline style, as
     * {@code getComputedStyle} reports it.
     *
     * @param cssProperty the property name in kebab case, e.g. {@code display}
     *                    or {@code --lumo-primary-color}
     * @return the computed value, trimmed; an empty string when the property is
     *         unknown or, for a custom property, not set
     */
    default String getComputedStyle(String cssProperty) {
        String value = (String) getLocator().evaluate(
                "(el, p) => getComputedStyle(el).getPropertyValue(p)", cssProperty);
        return value == null ? "" : value.trim();
    }

    /**
     * Assert the component has exactly the provided class names, or no classes when null.
     */
    default void assertCssClass(String... classnames) {
        if (classnames != null) {
            assertThat(getLocator()).hasClass(classnames);
        } else {
            assertThat(getLocator()).not().hasClass(Pattern.compile(".*"));
        }
    }

}
