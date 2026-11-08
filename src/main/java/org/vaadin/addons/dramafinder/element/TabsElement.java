package org.vaadin.addons.dramafinder.element;

import java.util.List;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-tabs>}.
 * <p>
 * Wraps the standalone tab strip (the {@code Tabs} component, and the tab strip
 * embedded in a {@code TabSheet}). Provides helpers to look up tabs by label or
 * index, to select them and to assert the selection, the tab count and the
 * orientation.
 * <p>
 * Tabs are looked up through the ARIA {@code tab} role, which {@code vaadin-tab}
 * sets on itself.
 */
@PlaywrightElement(TabsElement.FIELD_TAG_NAME)
public class TabsElement extends VaadinElement implements HasThemeElement, HasStyleElement,
        Findable<ComponentQuery<TabsElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-tabs";

    /** Horizontal value of the {@code orientation} attribute. */
    public static final String ORIENTATION_HORIZONTAL = "horizontal";
    /** Vertical value of the {@code orientation} attribute. */
    public static final String ORIENTATION_VERTICAL = "vertical";

    /**
     * Create a new {@code TabsElement}.
     *
     * @param locator the locator for the {@code <vaadin-tabs>} element
     */
    public TabsElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the first {@code TabsElement} on the page.
     *
     * @param page the Playwright page
     * @return the first matching {@code TabsElement}
     */
    public static TabsElement get(Page page) {
        return new TabsElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the first {@code TabsElement} within a parent locator.
     *
     * @param parent the parent locator to search within
     * @return the first matching {@code TabsElement}
     */
    public static TabsElement get(Locator parent) {
        return new TabsElement(parent.locator(FIELD_TAG_NAME).first());
    }

    // ── Tabs ──────────────────────────────────────────────────────────

    /**
     * Locator matching every tab of this tab strip, in DOM order.
     *
     * @return a locator resolving to all {@code <vaadin-tab>} children
     */
    public Locator getTabs() {
        return getLocator().locator(TabElement.FIELD_TAG_NAME);
    }

    /**
     * Get the number of tabs currently in the tab strip.
     *
     * @return the tab count
     */
    public int getTabCount() {
        return getTabs().count();
    }

    /**
     * Get a tab by its label.
     * <p>
     * The label must match the tab's full accessible name; {@code "Tab 1"} does
     * not resolve to {@code "Tab 10"}. Two tabs sharing the label fail with a
     * Playwright strict-mode error.
     *
     * @param label the full tab label
     * @return the matching {@code TabElement}
     */
    public TabElement getTab(String label) {
        return new TabElement(ItemLocator.byExactName(getLocator(), AriaRole.TAB, label));
    }

    /**
     * Get a tab by its zero-based index.
     *
     * @param index the tab index
     * @return the {@code TabElement} at that index
     */
    public TabElement getTab(int index) {
        return new TabElement(getTabs().nth(index));
    }

    /**
     * Get the currently selected tab.
     *
     * @return the selected {@code TabElement}
     */
    public TabElement getSelectedTab() {
        return new TabElement(getLocator().locator(TabElement.FIELD_TAG_NAME + "[selected]").first());
    }

    /**
     * Get the zero-based index of the selected tab.
     *
     * @return the selected index, or {@code -1} when nothing is selected
     */
    public int getSelectedIndex() {
        Object selected = getProperty("selected");
        return selected instanceof Number number ? number.intValue() : -1;
    }

    /**
     * Get the current orientation.
     *
     * @return {@value #ORIENTATION_HORIZONTAL} or {@value #ORIENTATION_VERTICAL}
     */
    public String getOrientation() {
        String orientation = getLocator().getAttribute("orientation");
        return orientation == null ? ORIENTATION_HORIZONTAL : orientation;
    }

    /**
     * Get the labels of all tabs, in DOM order, as {@link TabElement#getLabel()} reads them.
     *
     * @return the tab labels; empty when the strip has no tabs
     */
    @SuppressWarnings("unchecked")
    public List<String> getTabLabels() {
        return (List<String>) getTabs().evaluateAll("tabs => tabs.map(t => t.textContent)");
    }

    /**
     * Assert the labels of all tabs, in order. Each label must match its tab's whole text
     * (whitespace normalized), and the strip must hold exactly that many tabs.
     *
     * @param expected the expected tab labels, in DOM order; none asserts the strip has no tabs
     */
    public void assertTabLabels(String... expected) {
        assertThat(getTabs()).hasText(expected);
    }

    // ── Actions ───────────────────────────────────────────────────────

    /**
     * Select a tab by its label.
     *
     * @param label the tab label
     * @throws IllegalStateException if the tab is disabled
     */
    public void selectTab(String label) {
        getTab(label).select();
    }

    /**
     * Select a tab by its zero-based index.
     *
     * @param index the tab index
     * @throws IllegalStateException if the tab is disabled
     */
    public void selectTab(int index) {
        getTab(index).select();
    }

    // ── Assertions ────────────────────────────────────────────────────

    /**
     * Assert the number of tabs in the tab strip.
     *
     * @param count the expected tab count
     */
    public void assertTabCount(int count) {
        assertThat(getTabs()).hasCount(count);
    }

    /**
     * Assert that the tab with the given label is the selected one.
     *
     * @param label the expected selected tab label
     */
    public void assertSelectedTab(String label) {
        getTab(label).assertSelected();
    }

    /**
     * Assert that the tab at the given zero-based index is the selected one.
     *
     * @param index the expected selected tab index
     */
    public void assertSelectedTab(int index) {
        getTab(index).assertSelected();
    }

    /**
     * Assert the orientation of the tab strip.
     *
     * @param orientation the expected orientation, e.g. {@value #ORIENTATION_VERTICAL};
     *                    {@code null} asserts that no {@code orientation} attribute is set
     */
    public void assertOrientation(String orientation) {
        if (orientation == null) {
            assertThat(getLocator()).not().hasAttribute("orientation", Pattern.compile(".*"));
        } else {
            assertThat(getLocator()).hasAttribute("orientation", orientation);
        }
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TabsElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TabsElement}
     */
    public static TabsElement getById(Page page, String id) {
        return new TabsElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TabsElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TabsElement}
     */
    public static TabsElement getById(Locator locator, String id) {
        return new TabsElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TabsElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TabsElement}
     */
    public static TabsElement getByTestId(Page page, String testId) {
        return new TabsElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TabsElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TabsElement}
     */
    public static TabsElement getByTestId(Locator locator, String testId) {
        return new TabsElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
