package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.TextQuery;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;
import org.vaadin.addons.dramafinder.element.utils.ItemLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for tabs {@code <vaadin-tab>}.
 * <p>
 * A single tab of a {@code vaadin-tabs} strip: selects it, reads its label and
 * its selected and enabled state. A disabled tab sets {@code aria-disabled} on its
 * {@code tab} role, so the enabled checks of {@link HasEnabledElement} apply to it.
 */
@PlaywrightElement(TabElement.FIELD_TAG_NAME)
public class TabElement extends VaadinElement implements HasEnabledElement, Findable<TextQuery<TabElement>> {

    public static final String FIELD_PARENT_TAG_NAME = "vaadin-tabs";
    public static final String FIELD_TAG_NAME = "vaadin-tab";

    // A tab's label: its own text, leaving out children placed in a named slot, with whitespace
    // collapsed and trimmed as a user sees it; the aria-label of a tab with no text (icon only)
    static final String LABEL_JS = """
            el => Array.from(el.childNodes)
                .filter(node => !(node.nodeType === Node.ELEMENT_NODE && node.hasAttribute('slot')))
                .map(node => node.textContent)
                .join('').replace(/\\s+/g, ' ').trim() || (el.getAttribute('aria-label') || '').trim()""";

    /** Create a {@code TabElement} from an existing locator. */
    public TabElement(Locator locator) {
        super(locator);
    }

    /** Whether the tab is currently selected. */
    public boolean isSelected() {
        return getLocator().getAttribute("selected") != null;
    }

    /**
     * Select the tab by clicking it.
     *
     * @throws IllegalStateException if the tab is disabled; checked once before the click, not
     *                               retried, so the call fails at once instead of waiting for
     *                               the tab to become clickable
     */
    public void select() {
        if (!isEnabled()) {
            throw new IllegalStateException("Cannot select tab '" + getLabel() + "': it is disabled");
        }
        getLocator().click();
        waitAfterAction();
    }

    /**
     * Get the tab label: the tab's own text, with whitespace collapsed and trimmed. Children in a
     * named slot (such as a tooltip) are left out. A tab with no text, such as an icon-only tab,
     * reads as its {@code aria-label}.
     *
     * @return the label; empty when the tab has neither text nor {@code aria-label}
     */
    public String getLabel() {
        return (String) getLocator().evaluate(LABEL_JS);
    }

    /** Assert that the tab is selected. */
    public void assertSelected() {
        assertThat(getLocator()).hasAttribute("selected", "");
    }

    /** Assert that the tab is not selected. */
    public void assertNotSelected() {
        assertThat(getLocator()).not().hasAttribute("selected", "");
    }

    /**
     * Get a tab by its full visible text within a scope.
     * <p>
     * The text must match the tab's whole label, so {@code "Tab 1"} does not
     * resolve to {@code "Tab 10"}. Two tabs sharing the text fail with a
     * Playwright strict-mode error.
     */
    public static TabElement getTabByText(Locator locator, String summary) {
        return new TabElement(ItemLocator.byExactText(
                locator.locator(FIELD_PARENT_TAG_NAME), FIELD_TAG_NAME, summary));
    }

    /** Get the currently selected tab within a scope. */
    public static TabElement getSelectedTab(Locator locator) {
        return new TabElement(locator.locator(FIELD_PARENT_TAG_NAME).locator(FIELD_TAG_NAME + "[selected]"));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TabElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TabElement}
     */
    public static TabElement getById(Page page, String id) {
        return new TabElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TabElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TabElement}
     */
    public static TabElement getById(Locator locator, String id) {
        return new TabElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TabElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TabElement}
     */
    public static TabElement getByTestId(Page page, String testId) {
        return new TabElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TabElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TabElement}
     */
    public static TabElement getByTestId(Locator locator, String testId) {
        return new TabElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
