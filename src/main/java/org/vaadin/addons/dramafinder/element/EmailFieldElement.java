package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.vaadin.addons.dramafinder.element.query.ElementQueries;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-email-field>}.
 */
@PlaywrightElement(EmailFieldElement.FIELD_TAG_NAME)
public class EmailFieldElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-email-field";

    /**
     * Create a new {@code EmailFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-email-field>} element
     */
    public EmailFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code EmailFieldElement} whose visible label is exactly {@code label}, searching the entire page.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(page, EmailFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(page, EmailFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Page, String)} to match part of the label.
     *
     * @param page  the Playwright page
     * @param label the full visible label
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabel(Page page, String label) {
        return new EmailFieldElement(ElementQueries.find(page, EmailFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code EmailFieldElement} whose visible label contains {@code text}, searching the entire page.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(page, EmailFieldElement.class).withLabelContaining(text)}.
     *
     * @param page  the Playwright page
     * @param text  part of the visible label
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabelContaining(Page page, String text) {
        return new EmailFieldElement(ElementQueries.find(page, EmailFieldElement.class).withLabelContaining(text).getLocator());
    }

    /**
     * Get the {@code EmailFieldElement} whose visible label is exactly {@code label}, scoped to the given locator.
     * <p>
     * The label is the text of the component's own {@code label} slot, matched in
     * full and case-sensitively. An {@code aria-label} or a placeholder is not a
     * label: use {@code find(locator, EmailFieldElement.class).withAccessibleName(..)} or
     * {@code .withPlaceholder(..)}. No {@code .first()} is applied: two components
     * with that label fail with a strict-mode error. Same as
     * {@code find(locator, EmailFieldElement.class).withLabel(label)}; use
     * {@link #getByLabelContaining(Locator, String)} to match part of the label.
     *
     * @param locator the locator whose subtree is searched
     * @param label   the full visible label
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabel(Locator locator, String label) {
        return new EmailFieldElement(ElementQueries.find(locator, EmailFieldElement.class).withLabel(label).getLocator());
    }

    /**
     * Get the {@code EmailFieldElement} whose visible label contains {@code text}, scoped to the given locator.
     * <p>
     * The label is matched case-insensitively as a substring; an {@code aria-label}
     * or a placeholder is not a label. No {@code .first()} is applied: several
     * matching components fail with a strict-mode error. Same as
     * {@code find(locator, EmailFieldElement.class).withLabelContaining(text)}.
     *
     * @param locator the locator whose subtree is searched
     * @param text    part of the visible label
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByLabelContaining(Locator locator, String text) {
        return new EmailFieldElement(ElementQueries.find(locator, EmailFieldElement.class).withLabelContaining(text).getLocator());
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get an {@code EmailFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getById(Page page, String id) {
        return new EmailFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code EmailFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getById(Locator locator, String id) {
        return new EmailFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get an {@code EmailFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByTestId(Page page, String testId) {
        return new EmailFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get an {@code EmailFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code EmailFieldElement}
     */
    public static EmailFieldElement getByTestId(Locator locator, String testId) {
        return new EmailFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
