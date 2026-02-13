package org.vaadin.addons.dramafinder.element;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasHelperElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * PlaywrightElement for {@code <vaadin-date-picker>}.
 * <p>
 * Adds convenience methods for {@link LocalDate} values and lookup by label.
 */
@PlaywrightElement(DatePickerElement.FIELD_TAG_NAME)
public class DatePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement {

    /** The HTML tag name for this Vaadin component. */
    public static final String FIELD_TAG_NAME = "vaadin-date-picker";

    /**
     * Create a new {@code DatePickerElement}.
     *
     * @param locator the locator for the {@code <vaadin-date-picker>} element
     */
    public DatePickerElement(Locator locator) {
        super(locator);
    }

    /**
     * Set the value using a {@link LocalDate} formatted as ISO-8601.
     *
     * @param date the date to set
     */
    public void setValue(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        setProperty("value", formattedDate);
    }

    /**
     * Get the current value as a {@link LocalDate}.
     *
     * @return the parsed date or {@code null} when empty
     */
    public LocalDate getValueAsLocalDate() {
        String value = getValue();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }


    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    /**
     * Set the value of the input.
     *
     * @param value value formatted as in the view dd/mm/yyyy.
     */
    @Override
    public void setValue(String value) {
        HasInputFieldElement.super.setValue(value);
    }


    /**
     * Assert that the input value equals the provided string.
     *
     * @param value formatted as in the view dd/mm/yyyy.
     */
    @Override
    public void assertValue(String value) {
        HasInputFieldElement.super.assertValue(value);
    }

    /**
     * Assert that the value equals the provided date.
     *
     * @param value expected {@link LocalDate} or {@code null} for empty
     */
    public void assertValue(LocalDate value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            assertValue("");
        }
    }


    /**
     * Get the {@code DatePickerElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByLabel(Page page, String label) {
        return new DatePickerElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code DatePickerElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code DatePickerElement}
     */
    public static DatePickerElement getByLabel(Locator locator, String label) {
        return new DatePickerElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }
}
