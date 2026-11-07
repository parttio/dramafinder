package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasDisabledAttributeElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-details>}.
 * <p>
 * Provides helpers to open/close and access the summary/content.
 */
@PlaywrightElement(DetailsElement.FIELD_TAG_NAME)
public class DetailsElement extends VaadinElement implements HasStyleElement, HasThemeElement,
        HasTooltipElement, HasDisabledAttributeElement,
        Findable<ComponentQuery<DetailsElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-details";

    /** Create a new {@code DetailsElement}. */
    public DetailsElement(Locator locator) {
        super(locator);
    }

    /** Assert that the details is opened. */
    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the details is closed. */
    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    /** Whether the details is opened. */
    public boolean isOpen() {
        return getLocator().getAttribute("opened") != null;
    }

    /** Set the opened state by clicking the summary when necessary. */
    public void setOpen(boolean open) {
        if (isOpen() != open) {
            getSummaryLocator().click();
            waitAfterAction();
        }
    }

    /** Locator for the summary element. */
    public Locator getSummaryLocator() {
        return getLocator().locator("vaadin-details-summary");
    }

    /** Text of the summary element. */
    public String getSummaryText() {
        return getSummaryLocator().textContent();
    }


    /** Locator for the currently visible content container. */
    public Locator getContentLocator() {
        return getLocator().locator("> div[aria-hidden='false']");
    }

    /**
     * Get a details component by its summary text.
     * <p>
     * The summary's accessible name — its text, including the text of any
     * component in it — must match in full, case-sensitively; two details with
     * that summary fail with a strict-mode error.
     *
     * @param page    the Playwright page
     * @param summary the full summary text
     * @return the matching {@code DetailsElement}
     */
    public static DetailsElement getBySummaryText(Page page, String summary) {
        return new DetailsElement(
                page.locator(FIELD_TAG_NAME).filter(
                        new Locator.FilterOptions().setHas(
                                page.locator(":scope > vaadin-details-summary").and(page.getByRole(AriaRole.BUTTON,
                                        new Page.GetByRoleOptions().setName(summary).setExact(true)))
                        )
                )
        );
    }

    /** Assert that the content is visible. */
    public void assertContentVisible() {
        assertThat(getContentLocator()).isVisible();
    }

    /** Assert that the content is not visible. */
    public void assertContentNotVisible() {
        assertThat(getContentLocator()).not().isVisible();
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code DetailsElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code DetailsElement}
     */
    public static DetailsElement getById(Page page, String id) {
        return new DetailsElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DetailsElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code DetailsElement}
     */
    public static DetailsElement getById(Locator locator, String id) {
        return new DetailsElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code DetailsElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code DetailsElement}
     */
    public static DetailsElement getByTestId(Page page, String testId) {
        return new DetailsElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code DetailsElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code DetailsElement}
     */
    public static DetailsElement getByTestId(Locator locator, String testId) {
        return new DetailsElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
