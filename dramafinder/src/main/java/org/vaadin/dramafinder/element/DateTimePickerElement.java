package org.vaadin.dramafinder.element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.dramafinder.element.shared.FocusableElement;
import org.vaadin.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.dramafinder.element.shared.HasHelperElement;
import org.vaadin.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.dramafinder.element.shared.HasLabelElement;
import org.vaadin.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.dramafinder.element.shared.HasThemeElement;
import org.vaadin.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.dramafinder.element.shared.HasValidationPropertiesElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

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

    public DateTimePickerElement(Locator locator) {
        super(locator);
        datePickerElement = new DatePickerElement(locator.locator(DatePickerElement.FIELD_TAG_NAME));
        timePickerElement = new TimePickerElement(locator.locator(TimePickerElement.FIELD_TAG_NAME));
    }

    public void setValue(LocalDateTime date) {
        String formattedDate = date.format(ISO_LOCAL_DATE_TIME);
        setProperty("value", formattedDate);
    }

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
     * Check if the input value equals to the parameter
     *
     * @param value formatted as in the view dd/mm/yyyy hh:mm.
     */
    @Override
    public void assertValue(String value) {
        HasInputFieldElement.super.assertValue(value);
    }

    /**
     * Check if the value equals to the parameter
     *
     * @param value
     */
    public void assertValue(LocalDateTime value) {
        if (value != null) {
            assertThat(getLocator()).hasJSProperty("value", value.format(ISO_LOCAL_DATE_TIME));
        } else {
            assertValue("");
        }
    }


    public static DateTimePickerElement getByLabel(Page page, String label) {
        return new DateTimePickerElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    public static DateTimePickerElement getByLabel(Locator locator, String label) {
        return new DateTimePickerElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }

    public void setDate(String date) {
        datePickerElement.setValue(date);
        getLocator().page().keyboard().press("Enter");
    }

    public void setTime(String date) {
        timePickerElement.setValue(date);
        getLocator().page().keyboard().press("Enter");
    }

    public void assertDateValue(String date) {
        datePickerElement.assertValue(date);
    }

    public void assertTimeValue(String time) {
        timePickerElement.assertValue(time);
    }
}
