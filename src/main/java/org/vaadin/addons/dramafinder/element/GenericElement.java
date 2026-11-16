package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

/**
 * Element for a component of any tag that has no element class of its own:
 * an add-on, a custom component, a layout or a plain HTML element.
 * <p>
 * It offers the {@link VaadinElement} API (text, DOM properties, tag name,
 * click, visibility) plus CSS classes, inline and computed style
 * ({@link HasStyleElement}) and theme variants ({@link HasThemeElement}).
 * Get one with {@link VaadinElement#of(Locator)}, or find components by tag
 * with {@code ElementQueries.find(page, "my-component")}. Prefer the
 * component's own element class when there is one.
 */
public class GenericElement extends VaadinElement implements HasStyleElement, HasThemeElement {

    /**
     * Create a new {@code GenericElement}.
     *
     * @param locator the locator for the element
     */
    public GenericElement(Locator locator) {
        super(locator);
    }
}
