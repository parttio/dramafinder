package org.vaadin.addons.dramafinder.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.microsoft.playwright.Locator;
import org.vaadin.addons.dramafinder.element.query.AccessibleNames;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.query.ElementQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasLocatorElement;
import org.vaadin.addons.dramafinder.element.utils.TagNames;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Base class for typed Playwright wrappers around Vaadin components.
 * <p>
 * Exposes common helpers such as clicking, visibility assertions, text
 * retrieval and generic DOM property access. Concrete components add
 * component-specific APIs on top of this.
 */
public abstract class VaadinElement implements HasLocatorElement {

    /** The tag argument of the light-DOM lookups that matches any element. */
    public static final String ANY_TAG = "*";

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
     * Wrap any element in a {@link GenericElement}, for a component that has no
     * element class of its own (an add-on, a layout, a plain {@code div}).
     * <p>
     * The element offers the {@code VaadinElement} API plus CSS class, style and
     * theme helpers. When the component has an element class, use that instead:
     * {@code new ButtonElement(locator)}.
     *
     * @param locator the locator for the element; not {@code null}
     * @return a generic element for it
     */
    public static GenericElement of(Locator locator) {
        return new GenericElement(Objects.requireNonNull(locator, "locator"));
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
     * Get the element's tag name, in lower case as the DOM's {@code localName}
     * reports it, e.g. {@code vaadin-button} or {@code input}.
     *
     * @return the tag name
     */
    public String getTagName() {
        return (String) locator.evaluate("el => el.localName");
    }

    /**
     * Get the textual content of the component root.
     *
     * @return the text content, or {@code null} if none
     */
    public String getText() {
        return locator.textContent();
    }

    /**
     * Get the component's accessible name, as
     * {@link org.vaadin.addons.dramafinder.element.query.AccessibleNameFilters#withAccessibleName(String)
     * withAccessibleName} matches it.
     * <p>
     * By default the name comes from {@code aria-labelledby}, {@code aria-label}
     * or a native {@code <label>}, read on the host and then on the field's own
     * input — so a field's label, its {@code accessibleName} and its
     * {@code accessibleNameRef} all count. A placeholder does not. Components
     * named by their content (buttons, tabs, ...) fall back to their own text.
     * Unlike {@code getAriaLabel()}, this is not limited to the
     * {@code aria-label} attribute. Read without waiting.
     *
     * @return the whitespace-normalized name, or {@code null} when the
     *         component has none
     */
    public String getAccessibleName() {
        return AccessibleNames.labelled(locator);
    }

    /**
     * Assert the component's accessible name as read by
     * {@link #getAccessibleName()}, retrying until it matches or the timeout
     * expires. The comparison is exact and case-sensitive, with whitespace
     * normalized as {@code withAccessibleName} normalizes it; the failure
     * message shows the last name read.
     *
     * @param name the expected full name, or {@code null} to assert that the
     *             component has no accessible name
     */
    public void assertAccessibleName(String name) {
        String expected = name == null ? null : name.strip().replaceAll("\\s+", " ");
        AssertionWaits.assertEventually(locator.page(), "Accessible name", this::getAccessibleName, expected);
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
     * Get this component's light-DOM children with the given tag.
     * <p>
     * Only the host's own child elements are returned — never an element of
     * its shadow root, which a CSS lookup such as
     * {@code getLocator().locator("vaadin-button")} would also match. This is
     * the DOM counterpart of a server-side component's children. Slotted
     * children (e.g. a dialog's header content) are included.
     * <p>
     * The list is a snapshot in document order taken without waiting; each
     * entry addresses its element by position, so assert on the expected
     * count first when children are still being added.
     *
     * @param tag the tag name (e.g. {@code vaadin-button}, case-insensitive),
     *            or {@link #ANY_TAG} ({@code "*"}) for any element; not {@code null}
     * @return the matching children, each a {@link GenericElement}; empty when
     *         there are none
     * @throws IllegalArgumentException if {@code tag} is not a valid tag name
     */
    public List<VaadinElement> getLightDomChildren(String tag) {
        return lightDom("./", tag);
    }

    /**
     * Get this component's light-DOM descendants with the given tag.
     * <p>
     * Walks the host's child elements and their children, at any depth,
     * without entering any shadow root — neither this component's nor a
     * nested component's. A button inside a layout inside this component is
     * found; the internals of a nested date picker are not.
     * <p>
     * The list is a snapshot in document order taken without waiting; each
     * entry addresses its element by position.
     *
     * @param tag the tag name (e.g. {@code vaadin-button}, case-insensitive),
     *            or {@link #ANY_TAG} ({@code "*"}) for any element; not {@code null}
     * @return the matching descendants, each a {@link GenericElement}; empty
     *         when there are none
     * @throws IllegalArgumentException if {@code tag} is not a valid tag name
     */
    public List<VaadinElement> getLightDomDescendants(String tag) {
        return lightDom(".//", tag);
    }

    private List<VaadinElement> lightDom(String axis, String tag) {
        // XPath does not pierce shadow roots, unlike Playwright's CSS engine
        Locator matches = locator.locator("xpath=" + axis + TagNames.normalize(tag));
        List<VaadinElement> elements = new ArrayList<>();
        for (Locator match : matches.all()) {
            elements.add(new GenericElement(match));
        }
        return elements;
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
