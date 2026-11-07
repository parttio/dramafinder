package org.vaadin.addons.dramafinder.element;

import java.util.Map;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.ElementQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasLocatorElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Base class for typed Playwright wrappers around Vaadin components.
 * <p>
 * Exposes common helpers such as clicking, visibility assertions, text
 * retrieval and generic DOM property access. Concrete components add
 * component-specific APIs on top of this.
 */
public abstract class VaadinElement implements HasLocatorElement {
    protected final Locator locator;

    /**
     * Create a new VaadinElement wrapper.
     *
     * @param locator the locator pointing to the component root element
     */
    public VaadinElement(Locator locator) {
        this.locator = locator;
    }

    /**
     * Click the component root, then wait for the server round-trip it
     * triggers (see {@link org.vaadin.addons.dramafinder.AutoWait}).
     */
    public void click() {
        locator.click();
        waitAfterAction();
    }

    /**
     * Get the textual content of the component root.
     *
     * @return the text content, or {@code null} if none
     */
    public String getText() {
        return locator.textContent();
    }

    @Override
    /** {@inheritDoc} */
    public Locator getLocator() {
        return locator;
    }

    /**
     * Start a typed query for {@code type} components inside this component,
     * e.g. {@code dialog.find(ButtonElement.class).withText("Delete").single()}.
     * <p>
     * Searches this component's subtree; the component itself is not a
     * candidate. See {@link ElementQueries#find(com.microsoft.playwright.Page, Class)}.
     *
     * @param type the element class, annotated with {@link PlaywrightElement}
     * @param <T>  the element type
     * @param <Q>  the query type bound to {@code T} by {@link Findable}
     * @return a query over every component of that type inside this one
     */
    public <T extends VaadinElement & Findable<Q>, Q extends ElementQuery<? super T, Q>> Q find(Class<T> type) {
        return ElementQueries.find(locator, type);
    }

    /**
     * Set a DOM property on the underlying element (e.g. {@code value}, {@code disabled}),
     * then wait for the server round-trip a synchronized property triggers.
     *
     * @param name  property name
     * @param value property value
     */
    public void setProperty(String name, Object value) {
        locator.evaluate("(el, args) => el[args.name] = args.value", Map.of("name", name, "value", value));
        waitAfterAction();
    }

    /**
     * Get a DOM property from the underlying element.
     *
     * @param name property name
     * @return the property value or {@code null} if absent
     */
    public Object getProperty(String name) {
        return locator.evaluate("(el, args) => el[args.name]", Map.of("name", name));
    }

    /**
     * Whether the component is visible.
     *
     * @return {@code true} when visible
     */
    public boolean isVisible() {
        return locator.isVisible();
    }

    /**
     * Assert that the component is visible.
     */
    public void assertVisible() {
        assertThat(getLocator()).isVisible();
    }

    /**
     * Assert that the component is hidden.
     */
    public void assertHidden() {
        assertThat(getLocator()).isHidden();
    }

    /**
     * Whether the component's visibility matches the expected state.
     *
     * @param visible expected visibility state
     * @return {@code true} if the actual state equals {@code visible}
     */
    public boolean isVisible(boolean visible) {
        return isVisible() == visible;
    }

    /**
     * Assert the component's visibility state.
     *
     * @param visible expected visibility state
     */
    public void assertVisible(boolean visible) {
        if (visible) {
            assertVisible();
        } else {
            assertHidden();
        }
    }

    /**
     * Whether the component is hidden.
     *
     * @return {@code true} when hidden
     */
    public boolean isHidden() {
        return !isVisible();
    }

}
