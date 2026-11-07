package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.ComponentQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasStyleElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-popover>}.
 */
@PlaywrightElement(PopoverElement.FIELD_TAG_NAME)
public class PopoverElement extends VaadinElement implements HasThemeElement, HasStyleElement, HasAriaLabelElement,
        Findable<ComponentQuery<PopoverElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-popover";

    /** Create a {@code PopoverElement} by resolving the dialog with ARIA role. */
    public PopoverElement(Page page) {
        super(
                page.getByRole(AriaRole.DIALOG)
                        .and(page.locator(FIELD_TAG_NAME)));
    }

    /** Create a {@code PopoverElement} from an existing locator. */
    public PopoverElement(Locator locator) {
        super(locator);
    }

    /** Whether the popover is open (visible). */
    public boolean isOpen() {
        return getLocator().isVisible();
    }

    /** Assert that the popover is open. */
    public void assertOpen() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /** Assert that the popover is closed (hidden). */
    public void assertClosed() {
        assertThat(getLocator()).isHidden();
    }

    /** Locator for the popover content. */
    public Locator getContentLocator() {
        return getLocator();
    }


    /**
     * Get the {@code PopoverElement} by its accessible name (ARIA role {@code dialog}),
     * searching the entire page.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two matching components fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the full accessible name
     * @return the matching {@code PopoverElement}
     * @deprecated A {@code PopoverElement} has no visible label: this matches its
     *             accessible name. Use {@code find(page, PopoverElement.class).withAccessibleName(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static PopoverElement getByLabel(Page page, String label) {
        return new PopoverElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(label).setExact(true))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

    /**
     * Get the {@code PopoverElement} whose accessible name (ARIA role {@code dialog}) contains
     * {@code text}, searching the entire page.
     * <p>
     * The name is matched case-insensitively as a substring. No {@code .first()} is
     * applied: several matching components fail with a strict-mode error.
     *
     * @param page  the Playwright page
     * @param text  part of the accessible name
     * @return the matching {@code PopoverElement}
     * @deprecated A {@code PopoverElement} has no visible label: this matches its
     *             accessible name. Use {@code find(page, PopoverElement.class).withAccessibleNameContaining(..)}
     *             instead. To be removed in 3.0.
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static PopoverElement getByLabelContaining(Page page, String text) {
        return new PopoverElement(
                page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(text))
                        .and(page.locator(FIELD_TAG_NAME))
        );
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code PopoverElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code PopoverElement}
     */
    public static PopoverElement getById(Page page, String id) {
        return new PopoverElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code PopoverElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code PopoverElement}
     */
    public static PopoverElement getById(Locator locator, String id) {
        return new PopoverElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code PopoverElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code PopoverElement}
     */
    public static PopoverElement getByTestId(Page page, String testId) {
        return new PopoverElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code PopoverElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code PopoverElement}
     */
    public static PopoverElement getByTestId(Locator locator, String testId) {
        return new PopoverElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
