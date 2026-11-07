package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-text-area>}.
 * <p>
 * Extends {@link TextFieldElement} with a textarea input slot and label-based lookup.
 */
@PlaywrightElement(TextAreaElement.FIELD_TAG_NAME)
public class TextAreaElement extends TextFieldElement {

    public static final String FIELD_TAG_NAME = "vaadin-text-area";

    /**
     * Create a new {@code TextAreaElement}.
     *
     * @param locator the locator for the {@code <vaadin-text-area>} element
     */
    public TextAreaElement(Locator locator) {
        super(locator);
    }

    /**
     * Locator for the native {@code textarea} slotted into the component.
     */
    public Locator getInputLocator() {
        return getLocator().locator("*[slot=\"textarea\"]").first(); // slot="helper"
    }

    /**
     * Get the {@code TextAreaElement} by its accessible name, searching the entire page.
     * <p>
     * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
     * placeholder text (used as a fallback when no label is present).
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the accessible name of the text area
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabel(Page page, String label) {
        return new TextAreaElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code TextAreaElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabelContaining(Page page, String text) {
        return new TextAreaElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }

    /**
     * Get the {@code TextAreaElement} by its accessible name, scoped to the given locator.
     * <p>
     * Matches fields identified by a visible label, an {@code aria-label} attribute, or a
     * placeholder text (used as a fallback when no label is present). Only fields within
     * the subtree of the provided locator are considered.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator defining the search scope
     * @param label   the accessible name of the text area
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabel(Locator locator, String label) {
        return new TextAreaElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code TextAreaElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByLabelContaining(Locator locator, String text) {
        return new TextAreaElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code TextAreaElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getById(Page page, String id) {
        return new TextAreaElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TextAreaElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getById(Locator locator, String id) {
        return new TextAreaElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code TextAreaElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByTestId(Page page, String testId) {
        return new TextAreaElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code TextAreaElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code TextAreaElement}
     */
    public static TextAreaElement getByTestId(Locator locator, String testId) {
        return new TextAreaElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
