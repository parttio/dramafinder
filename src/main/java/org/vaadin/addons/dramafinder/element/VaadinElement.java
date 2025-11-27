package org.vaadin.addons.dramafinder.element;

import java.util.Map;

import com.microsoft.playwright.Locator;
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
     * Click the component root.
     */
    public void click() {
        locator.click();
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
     * Set a DOM property on the underlying element (e.g. {@code value}, {@code disabled}).
     *
     * @param name  property name
     * @param value property value
     */
    public void setProperty(String name, Object value) {
        locator.evaluate("(el, args) => el[args.name] = args.value", Map.of("name", name, "value", value));
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
     * Whether the component is hidden.
     *
     * @return {@code true} when hidden
     */
    public boolean isHidden() {
        return !isVisible();
    }

}
