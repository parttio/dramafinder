package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-tabsheet>}.
 * <p>
 * Provides helpers to access/select tabs and current content panel. Tab lookup
 * and selection are delegated to the {@link TabsElement} of the embedded
 * {@code <vaadin-tabs>} strip.
 */
@PlaywrightElement(TabSheetElement.FIELD_TAG_NAME)
public class TabSheetElement extends VaadinElement implements Findable<ComponentQuery<TabSheetElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-tabsheet";

    private final TabsElement tabsElement;

    /** Create a {@code TabSheetElement} from an existing locator. */
    public TabSheetElement(Locator locator) {
        super(locator);
        this.tabsElement = TabsElement.get(locator);
    }

    /** Get the first tabsheet instance on the page. */
    public static TabSheetElement get(Page page) {
        return new TabSheetElement(page.locator(FIELD_TAG_NAME).first());
    }

    /**
     * Get the embedded tab strip.
     *
     * @return the {@code TabsElement} for this tabsheet's {@code <vaadin-tabs>}
     */
    public TabsElement getTabsElement() {
        return tabsElement;
    }

    /** Assert the count of tabs. */
    public void assertTabsCount(int count) {
        tabsElement.assertTabCount(count);
    }

    /** Get a tab by its label. */
    public TabElement getTab(String label) {
        return tabsElement.getTab(label);
    }

    /** Get the currently selected tab. */
    public TabElement getSelectedTab() {
        return tabsElement.getSelectedTab();
    }

    /** Select a tab by label text. */
    public void selectTab(String label) {
        tabsElement.selectTab(label);
    }

    /** Select a tab by its zero-based index. */
    public void selectTab(int index) {
        tabsElement.selectTab(index);
    }

    /** Assert that the tab with the given label is selected. */
    public void assertSelectedTab(String label) {
        tabsElement.assertSelectedTab(label);
    }

    /** Locator for the currently visible content panel. */
    public Locator getContentLocator() {
        return getLocator().locator("[tab]:not([hidden])");
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TabSheetElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TabSheetElement}
     */
    public static TabSheetElement getById(Page page, String id) {
        return new TabSheetElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TabSheetElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TabSheetElement}
     */
    public static TabSheetElement getById(Locator locator, String id) {
        return new TabSheetElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TabSheetElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TabSheetElement}
     */
    public static TabSheetElement getByTestId(Page page, String testId) {
        return new TabSheetElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TabSheetElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TabSheetElement}
     */
    public static TabSheetElement getByTestId(Locator locator, String testId) {
        return new TabSheetElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
