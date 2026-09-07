package org.vaadin.addons.dramafinder.element;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;

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
public class TabsElement extends VaadinElement implements HasThemeElement, HasStyleElement {

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

    /**
     * Get a {@code TabsElement} by its {@code id} attribute.
     *
     * @param page the Playwright page
     * @param id   the element id
     * @return the matching {@code TabsElement}
     */
    public static TabsElement getById(Page page, String id) {
        return new TabsElement(page.locator("#" + id));
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
     * The label is matched case-insensitively against the tab's accessible name,
     * as a substring.
     *
     * @param label the tab label
     * @return the matching {@code TabElement}
     */
    public TabElement getTab(String label) {
        return new TabElement(getLocator()
                .getByRole(AriaRole.TAB, new Locator.GetByRoleOptions().setName(label))
                .first());
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

    // ── Actions ───────────────────────────────────────────────────────

    /**
     * Select a tab by its label.
     *
     * @param label the tab label
     */
    public void selectTab(String label) {
        getTab(label).select();
    }

    /**
     * Select a tab by its zero-based index.
     *
     * @param index the tab index
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
}
