package org.vaadin.addons.dramafinder.element;

import java.lang.reflect.InvocationTargetException;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-virtual-list>}.
 * <p>
 * A virtualized scrollable list that lazily renders items as the user scrolls.
 * Provides helpers for scrolling, querying visible rows, and accessing
 * rendered item content.
 */
@PlaywrightElement(VirtualListElement.FIELD_TAG_NAME)
public class VirtualListElement extends VaadinElement
        implements FocusableElement, HasStyleElement {

    public static final String FIELD_TAG_NAME = "vaadin-virtual-list";

    /**
     * Create a new {@code VirtualListElement}.
     *
     * @param locator the locator for the {@code <vaadin-virtual-list>} element
     */
    public VirtualListElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the first {@code VirtualListElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code VirtualListElement}
     */
    public static VirtualListElement get(Page page) {
        return new VirtualListElement(page.locator(FIELD_TAG_NAME).first());
    }

    // ── Properties / Getters ────────────────────────────────────────────

    /**
     * Get the total number of items in the list.
     *
     * @return total item count (including items not yet rendered)
     */
    public int getRowCount() {
        return ((Number) locator.evaluate(
                "el => el.items ? el.items.length : 0")).intValue();
    }

    /**
     * Get the index of the first row that is at least partially visible.
     * Flushes the connector debouncer and reads the property in a single
     * browser round-trip.
     *
     * @return 0-based index of the first visible row
     */
    public int getFirstVisibleRowIndex() {
        return ((Number) locator.evaluate(
                "el => { if (el.__requestDebounce) el.__requestDebounce.flush();"
                        + " return el.firstVisibleIndex; }")).intValue();
    }

    /**
     * Get the index of the last row that is at least partially visible.
     * Flushes the connector debouncer and reads the property in a single
     * browser round-trip.
     *
     * @return 0-based index of the last visible row
     */
    public int getLastVisibleRowIndex() {
        return ((Number) locator.evaluate(
                "el => { if (el.__requestDebounce) el.__requestDebounce.flush();"
                        + " return el.lastVisibleIndex; }")).intValue();
    }

    /**
     * Get the number of currently visible rows in a single browser
     * round-trip.
     *
     * @return count of rows visible in the viewport
     */
    public int getVisibleRowCount() {
        return ((Number) locator.evaluate(
                "el => { if (el.__requestDebounce) el.__requestDebounce.flush();"
                        + " return el.lastVisibleIndex - el.firstVisibleIndex + 1; }"
        )).intValue();
    }

    /**
     * Whether the given row index is currently visible in the viewport.
     * Performs the flush and range check in a single browser round-trip.
     *
     * @param rowIndex the 0-based row index to check
     * @return {@code true} if the row is between first and last visible index
     */
    public boolean isRowInView(int rowIndex) {
        return (boolean) locator.evaluate(
                "(el, idx) => { if (el.__requestDebounce) el.__requestDebounce.flush();"
                        + " return el.firstVisibleIndex <= idx && idx <= el.lastVisibleIndex; }",
                rowIndex);
    }

    // ── Item Queries ────────────────────────────────────────────────────

    /**
     * Get a locator matching all currently rendered child elements.
     *
     * @return locator for the rendered item root elements
     */
    public Locator getRenderedItems() {
        return locator.locator("> *");
    }

    /**
     * Get the rendered DOM element at the given virtual index.
     * <p>
     * The row must be currently in view (scrolled to), otherwise the
     * locator will not resolve.
     *
     * @param index the 0-based virtual row index
     * @return locator for the rendered item root element
     */
    public Locator getItemByIndex(int index) {
        return locator.locator("> *").nth(index - getFirstVisibleRowIndex());
    }

    /**
     * Get a rendered item containing the given text.
     * <p>
     * The target item must be currently rendered in the DOM.
     *
     * @param text the text to search for
     * @return locator for the first matching item
     */
    public Locator getItemByText(String text) {
        return locator.locator("> *")
                .filter(new Locator.FilterOptions().setHasText(text))
                .first();
    }

    // ── Component Queries ─────────────────────────────────────────────

    /**
     * Get a typed component element from the rendered item at the given
     * virtual index. The component class must be annotated with
     * {@link PlaywrightElement} and have a public constructor accepting a
     * single {@link Locator} parameter.
     *
     * @param index the 0-based virtual row index (must be in view)
     * @param type  the element class (e.g. {@code ButtonElement.class})
     * @param <T>   the element type
     * @return the first matching component inside the item
     */
    public <T extends VaadinElement> T getItemComponent(int index, Class<T> type) {
        return createComponent(getItemByIndex(index), type);
    }

    /**
     * Get a typed component element from the rendered item whose text
     * matches. The component class must be annotated with
     * {@link PlaywrightElement} and have a public constructor accepting a
     * single {@link Locator} parameter.
     *
     * @param text the text used to locate the item
     * @param type the element class (e.g. {@code ButtonElement.class})
     * @param <T>  the element type
     * @return the first matching component inside the item
     */
    public <T extends VaadinElement> T getItemComponentByText(String text, Class<T> type) {
        return createComponent(getItemByText(text), type);
    }

    /**
     * Get the first typed component element found anywhere in the
     * currently rendered items. The component class must be annotated with
     * {@link PlaywrightElement} and have a public constructor accepting a
     * single {@link Locator} parameter.
     *
     * @param type the element class (e.g. {@code ButtonElement.class})
     * @param <T>  the element type
     * @return the first matching component
     */
    public <T extends VaadinElement> T getComponent(Class<T> type) {
        return createComponent(locator, type);
    }

    // ── Actions ─────────────────────────────────────────────────────────

    /**
     * Scroll the list so that the given row index becomes visible.
     *
     * @param rowIndex the 0-based row index to scroll to
     */
    public void scrollToRow(int rowIndex) {
        locator.evaluate("(el, idx) => el.scrollToIndex(idx)", rowIndex);
    }

    /**
     * Scroll to the very beginning of the list.
     */
    public void scrollToStart() {
        scrollToRow(0);
    }

    /**
     * Scroll to the very end of the list.
     */
    public void scrollToEnd() {
        scrollToRow(Integer.MAX_VALUE);
    }

    // ── Assertions ──────────────────────────────────────────────────────

    /**
     * Assert the total number of items matches the expected count.
     * Auto-retries until the condition is met or the default timeout
     * expires.
     *
     * @param expected expected row count
     */
    public void assertRowCount(int expected) {
        locator.page().waitForCondition(() -> getRowCount() == expected);
    }

    /**
     * Assert that the given row index is currently visible.
     * Auto-retries until the condition is met or the default timeout
     * expires.
     *
     * @param rowIndex the 0-based row index
     */
    public void assertRowInView(int rowIndex) {
        locator.page().waitForCondition(() -> isRowInView(rowIndex));
    }

    /**
     * Assert that the given row index is NOT currently visible.
     * Auto-retries until the condition is met or the default timeout
     * expires.
     *
     * @param rowIndex the 0-based row index
     */
    public void assertRowNotInView(int rowIndex) {
        locator.page().waitForCondition(() -> !isRowInView(rowIndex));
    }

    /**
     * Assert that the first visible row index equals the expected value.
     * Uses Playwright's auto-retrying {@code hasJSProperty} assertion;
     * the 50 ms connector debouncer settles naturally within the retry
     * window.
     *
     * @param expected expected first visible row index
     */
    public void assertFirstVisibleRow(int expected) {
        assertThat(locator).hasJSProperty("firstVisibleIndex", expected);
    }

    /**
     * Assert that the last visible row index equals the expected value.
     * Uses Playwright's auto-retrying {@code hasJSProperty} assertion;
     * the 50 ms connector debouncer settles naturally within the retry
     * window.
     *
     * @param expected expected last visible row index
     */
    public void assertLastVisibleRow(int expected) {
        assertThat(locator).hasJSProperty("lastVisibleIndex", expected);
    }

    /**
     * Assert that an item containing the given text is currently rendered.
     *
     * @param text the text to look for
     */
    public void assertItemRendered(String text) {
        assertThat(getItemByText(text)).isVisible();
    }

    /**
     * Assert the list has zero items.
     */
    public void assertEmpty() {
        assertRowCount(0);
    }

    // ── Internal ────────────────────────────────────────────────────────

    /**
     * Locate a component by its {@link PlaywrightElement} tag inside the
     * given parent and instantiate the element wrapper.
     */
    private <T extends VaadinElement> T createComponent(Locator parent, Class<T> type) {
        PlaywrightElement annotation = type.getAnnotation(PlaywrightElement.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    type.getSimpleName() + " is not annotated with @PlaywrightElement");
        }
        Locator componentLocator = parent.locator(annotation.value()).first();
        try {
            return type.getConstructor(Locator.class).newInstance(componentLocator);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type.getSimpleName(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Cannot instantiate " + type.getSimpleName(), e);
        }
    }
}
