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
}
