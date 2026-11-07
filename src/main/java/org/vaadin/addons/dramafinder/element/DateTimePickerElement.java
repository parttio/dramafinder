package org.vaadin.addons.dramafinder.element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.vaadin.addons.dramafinder.element.query.FieldQuery;
import org.vaadin.addons.dramafinder.element.query.Findable;
import org.vaadin.addons.dramafinder.element.utils.AccessibleNameLocator;
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
        HasEnabledElement, HasTooltipElement, HasLabelElement, HasHelperElement,
        Findable<FieldQuery<DateTimePickerElement>> {

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
        waitForVaadinIdle();
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
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Page, String)} to match part of the name.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabel(Page page, String label) {
        return new DateTimePickerElement(
                AccessibleNameLocator.find(page, FIELD_TAG_NAME, AriaRole.COMBOBOX, label));
    }

    /**
     * Get the {@code DateTimePickerElement} whose accessible name contains {@code text},
     * searching the entire page.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param page the Playwright page
     * @param text part of the accessible name of the field
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabelContaining(Page page, String text) {
        return new DateTimePickerElement(
                AccessibleNameLocator.findContaining(page, FIELD_TAG_NAME, AriaRole.COMBOBOX, text));
    }

    /**
     * Get the {@code DateTimePickerElement} by its label within a given scope.
     * <p>
     * The name must match in full, case-sensitively, and no {@code .first()} is applied:
     * two fields with the same name fail with a strict-mode error. Use
     * {@link #getByLabelContaining(Locator, String)} to match part of the name.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabel(Locator locator, String label) {
        return new DateTimePickerElement(
                AccessibleNameLocator.find(locator, FIELD_TAG_NAME, AriaRole.COMBOBOX, label));
    }

    /**
     * Get the {@code DateTimePickerElement} whose accessible name contains {@code text},
     * scoped to the given locator.
     * <p>
     * The label, {@code aria-label} or placeholder is matched case-insensitively as a
     * substring. No {@code .first()} is applied: several matching fields fail with a
     * strict-mode error.
     *
     * @param locator the locator defining the search scope
     * @param text    part of the accessible name of the field
     * @return the matching {@code DateTimePickerElement}
     */
    public static DateTimePickerElement getByLabelContaining(Locator locator, String text) {
        return new DateTimePickerElement(
                AccessibleNameLocator.findContaining(locator, FIELD_TAG_NAME, AriaRole.COMBOBOX, text));
    }

    /**
     * Set only the date part (string input) and dispatch change events.
     */
    public void setDate(String date) {
        datePickerElement.setValue(date);
        getLocator().press("Enter");
        getLocator().dispatchEvent("change");
        waitForVaadinIdle();
    }

    /**
     * Set only the time part (string input) and dispatch change events.
     */
    public void setTime(String date) {
        timePickerElement.setValue(date);
        getLocator().press("Enter");
        getLocator().dispatchEvent("change");
        waitForVaadinIdle();
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
