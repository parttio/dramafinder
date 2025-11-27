package org.vaadin.addons.dramafinder.element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

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
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * PlaywrightElement for {@code <vaadin-date-time-picker>}.
 * <p>
 * Composes a {@link DatePickerElement} and {@link TimePickerElement} and exposes
 * helpers to interact using {@link LocalDateTime}.
 */
@PlaywrightElement(DateTimePickerElement.FIELD_TAG_NAME)
public class DateTimePickerElement extends VaadinElement implements HasInputFieldElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasPlaceholderElement, HasThemeElement, FocusableElement, HasAriaLabelElement,
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement {

    private final DatePickerElement datePickerElement;
    private final TimePickerElement timePickerElement;
    public static final String FIELD_TAG_NAME = "vaadin-date-time-picker";
    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(TimePickerElement.LOCAL_TIME)
            .toFormatter();

    /**
     * Create a new {@code DateTimePickerElement}.
     *
     * @param locator the locator for the {@code <vaadin-date-time-picker>} element
     */
    public DateTimePickerElement(Locator locator) {
        super(locator);
        datePickerElement = new DatePickerElement(locator.locator(DatePickerElement.FIELD_TAG_NAME));
        timePickerElement = new TimePickerElement(locator.locator(TimePickerElement.FIELD_TAG_NAME));
    }

    /**
     * Set the value using a {@link LocalDateTime}.
     *
     * @param date the date-time to set
     */
    public void setValue(LocalDateTime date) {
        datePickerElement.setValue(date.toLocalDate());
        timePickerElement.setValue(date.toLocalTime());
        getLocator().dispatchEvent("change");
    }

    /**
     * Get the current value as a {@link LocalDateTime}.
     *
     * @return the parsed value or {@code null} when empty
     */
    public LocalDateTime getValueAsLocalDateTime() {
        String value = getValue();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value, ISO_LOCAL_DATE_TIME);
    }

    @Override
    public String getAriaLabel() {
        return datePickerElement.getAriaLabel();
    }

    @Override
    public void assertAriaLabel(String ariaLabel) {
        datePickerElement.assertAriaLabel(ariaLabel);
        timePickerElement.assertAriaLabel(ariaLabel);
    }

    @Override
    public Locator getFocusLocator() {
        return datePickerElement.getInputLocator();
    }

    @Override
    public boolean isEnabled() {
        return datePickerElement.isEnabled() && timePickerElement.isEnabled();
    }

    @Override
    public void assertEnabled() {
        datePickerElement.assertEnabled();
        timePickerElement.assertEnabled();
    }

    @Override
    public void assertDisabled() {
        datePickerElement.assertDisabled();
        timePickerElement.assertDisabled();
    }

    /**
     * Set the value of the input.
     *
     * @param value value formatted as in the view dd/mm/yyyy hh:mm.
     */
    @Override
    public void setValue(String value) {
        HasInputFieldElement.super.setValue(value);
    }


    /**
     * Assert that the input value equals the provided string.
     *
     * @param value formatted as in the view dd/mm/yyyy hh:mm.
     */
    @Override
    public void assertValue(String value) {
        HasInputFieldElement.super.assertValue(value);
    }

    /**
     * Assert that the value equals the provided date-time.
     *
     * @param value expected {@link LocalDateTime} or {@code null} for empty
     */
    public void assertValue(LocalDateTime value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(ISO_LOCAL_DATE_TIME));
        } else {
            assertValue("");
        }
    }


    /**
     * Get the {@code DateTimePickerElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabel(Page page, String label) {
        return new DateTimePickerElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code DateTimePickerElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabel(Locator locator, String label) {
        return new DateTimePickerElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }

    /**
     * Set only the date part (string input) and dispatch change events.
     */
    public void setDate(String date) {
        datePickerElement.setValue(date);
        getLocator().page().keyboard().press("Enter");
        getLocator().dispatchEvent("change");
    }

    /**
     * Set only the time part (string input) and dispatch change events.
     */
    public void setTime(String date) {
        timePickerElement.setValue(date);
        getLocator().page().keyboard().press("Enter");
        getLocator().dispatchEvent("change");
    }

    /**
     * Assert the date sub-field value equals the expected string.
     */
    public void assertDateValue(String date) {
        datePickerElement.assertValue(date);
    }

    /**
     * Assert the time sub-field value equals the expected string.
     */
    public void assertTimeValue(String time) {
        timePickerElement.assertValue(time);
    }
}
