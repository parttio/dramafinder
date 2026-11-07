package org.vaadin.addons.dramafinder.element;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.query.InputFieldQuery;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasSuffixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import org.vaadin.addons.dramafinder.element.utils.IdLocator;

/**
 * PlaywrightElement for {@code <vaadin-big-decimal-field>}.
 */
@PlaywrightElement(BigDecimalFieldElement.FIELD_TAG_NAME)
public class BigDecimalFieldElement extends VaadinElement
        implements HasValidationPropertiesElement, HasInputFieldElement,
        HasPrefixElement, HasSuffixElement, HasClearButtonElement, HasPlaceholderElement, HasAllowedCharPatternElement,
        HasThemeElement, FocusableElement, HasAriaLabelElement, HasEnabledElement, HasTooltipElement,
        Findable<InputFieldQuery<BigDecimalFieldElement>> {

    public static final String FIELD_TAG_NAME = "vaadin-big-decimal-field";

    /**
     * Create a new {@code BigDecimalFieldElement}.
     *
     * @param locator the locator for the {@code <vaadin-big-decimal-field>} element
     */
    public BigDecimalFieldElement(Locator locator) {
        super(locator);
    }

    /**
     * Get the {@code BigDecimalFieldElement} by its label.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByLabel(Page page, String label) {
        return new BigDecimalFieldElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code BigDecimalFieldElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByLabelContaining(Page page, String text) {
        return new BigDecimalFieldElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }

    /**
     * Get the {@code BigDecimalFieldElement} by its label within a given scope.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByLabel(Locator locator, String label) {
        return new BigDecimalFieldElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, label));
    }

    /**
     * Get the {@code BigDecimalFieldElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByLabelContaining(Locator locator, String text) {
        return new BigDecimalFieldElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.TEXTBOX, text));
    }

    // ── Lookup by id or test id ────────────────────────────────────────

    /**
     * Get a {@code BigDecimalFieldElement} by its {@code id}, searching the entire page.
     * <p>
     * A fallback for a component without a user-visible name: prefer the label or
     * text lookups when there is one. The id must match in full; two components
     * with that id fail with a strict-mode error.
     *
     * @param page the Playwright page
     * @param id   the full {@code id}
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getById(Page page, String id) {
        return new BigDecimalFieldElement(IdLocator.byId(page, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code BigDecimalFieldElement} by its {@code id}, scoped to the given locator.
     * <p>
     * A fallback for a component without a user-visible name. The id must match in
     * full; two components with that id inside the scope fail with a strict-mode
     * error.
     *
     * @param locator the locator whose subtree is searched
     * @param id      the full {@code id}
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getById(Locator locator, String id) {
        return new BigDecimalFieldElement(IdLocator.byId(locator, FIELD_TAG_NAME, id));
    }

    /**
     * Get a {@code BigDecimalFieldElement} by its test id, searching the entire page.
     * <p>
     * The test id is Playwright's test id attribute, {@code data-testid} unless
     * reconfigured — the attribute Flow's {@code Component.setTestId(String)} sets.
     * A fallback for a component without a user-visible name; it must match in
     * full, and two components with that test id fail with a strict-mode error.
     *
     * @param page   the Playwright page
     * @param testId the full test id
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByTestId(Page page, String testId) {
        return new BigDecimalFieldElement(IdLocator.byTestId(page, FIELD_TAG_NAME, testId));
    }

    /**
     * Get a {@code BigDecimalFieldElement} by its test id, scoped to the given locator.
     * <p>
     * See {@link #getByTestId(Page, String)}; only components inside the scope are
     * considered.
     *
     * @param locator the locator whose subtree is searched
     * @param testId  the full test id
     * @return the matching {@code BigDecimalFieldElement}
     */
    public static BigDecimalFieldElement getByTestId(Locator locator, String testId) {
        return new BigDecimalFieldElement(IdLocator.byTestId(locator, FIELD_TAG_NAME, testId));
    }
}
