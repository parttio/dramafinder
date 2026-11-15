package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-password-field>}.
 */
@PlaywrightElement(PasswordFieldElement.FIELD_TAG_NAME)
public class PasswordFieldElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-password-field";

    /**
     * Create a new {@code PasswordFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-password-field>} element
     */
    public PasswordFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code PasswordFieldElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, PasswordFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, PasswordFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabel(Page page, String label) {
        return new PasswordFieldElement(ElementQueries.find(page, PasswordFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code PasswordFieldElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, PasswordFieldElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabelContaining(Page page, String text) {
        return new PasswordFieldElement(ElementQueries.find(page, PasswordFieldElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code PasswordFieldElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, PasswordFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, PasswordFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabel(Locator locator, String label) {
        return new PasswordFieldElement(ElementQueries.find(locator, PasswordFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code PasswordFieldElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, PasswordFieldElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByLabelContaining(Locator locator, String text) {
        return new PasswordFieldElement(ElementQueries.find(locator, PasswordFieldElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code PasswordFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getById(Page page, String id) {
        return new PasswordFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code PasswordFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getById(Locator locator, String id) {
        return new PasswordFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code PasswordFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByTestId(Page page, String testId) {
        return new PasswordFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code PasswordFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code PasswordFieldElement}
     */
    public static PasswordFieldElement getByTestId(Locator locator, String testId) {
        return new PasswordFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
