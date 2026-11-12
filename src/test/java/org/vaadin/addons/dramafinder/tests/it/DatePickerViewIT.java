package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.DatePickerElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.vaadin.addons.dramafinder.element.query.ElementQueries.find;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DatePickerViewIT extends SpringPlaywrightIT {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getView() {
        return "date-picker";
    }

    @Test
    public void testBasicDatePicker() {
        DatePickerElement datePicker = DatePickerElement.getByLabel(page, "Appointment date");
        datePicker.assertVisible();
        datePicker.assertHelperHasText("Enter the appointment date");
        LocalDate testDate = LocalDate.of(2025, 9, 20);
        datePicker.setValue(testDate);
        datePicker.assertValue(formatter.format(testDate));
        datePicker.assertValue(testDate);
    }

    @Test
    public void testMinMax() {
        DatePickerElement datePicker = DatePickerElement.getByLabel(page, "Vacation date");
        datePicker.assertVisible();

        LocalDate today = LocalDate.now();
        LocalDate initialDate = today.plusDays(7);
        LocalDate maxDate = today.plusDays(30);
        datePicker.assertValue(formatter.format(initialDate));
        datePicker.assertValue(initialDate);

        // Test setting a value within bounds
        LocalDate validDate = today.plusDays(15);
        datePicker.setValue(validDate);

        datePicker.assertValue(validDate);
        datePicker.assertValue(formatter.format(validDate));
        datePicker.assertValid();

        // Test setting a value outside bounds (after max)
        LocalDate invalidDate = maxDate.plusDays(1);
        datePicker.setValue(invalidDate);
        datePicker.assertInvalid();
        datePicker.assertErrorMessage("Maximum number of days exceeded.");

        // Test setting a value outside bounds (before min)
        LocalDate anotherInvalidDate = today.minusDays(1);
        datePicker.setValue(anotherInvalidDate);
        datePicker.assertInvalid();
        datePicker.assertErrorMessage("Minimum number of days exceeded.");

        // Set a valid value again
        datePicker.setValue(initialDate);
        datePicker.assertValid();
    }

    @Test
    public void testTypeValidText() {
        DatePickerElement datePicker = DatePickerElement.getByLabel(page, "Typed date");
        countHostChangeEvents(datePicker);

        datePicker.setValue("20/09/2025");

        assertThat(page.locator("#typed-date-value")).hasText("2025-09-20");
        datePicker.assertValue(LocalDate.of(2025, 9, 20));
        datePicker.assertValid();
        // Only the picker's own change: nothing synthetic was dispatched on the host
        assertEquals(1, hostChangeEvents());
    }

    @Test
    public void testTypeUnparsableText() {
        DatePickerElement datePicker = DatePickerElement.getByLabel(page, "Typed date");
        datePicker.setValue("20/09/2025");
        assertThat(page.locator("#typed-date-value")).hasText("2025-09-20");

        datePicker.setValue("dsdds");

        assertThat(page.locator("#typed-date-value")).hasText("null");
        // The text stays in the input; the picker's value is empty
        datePicker.assertValue("dsdds");
        assertThat(datePicker.getLocator()).hasJSProperty("value", "");
        datePicker.assertInvalid();
        datePicker.assertErrorMessage("Bad input.");
    }

    @Test
    public void testClear() {
        DatePickerElement datePicker = DatePickerElement.getByLabel(page, "Typed date");
        datePicker.setValue("20/09/2025");
        assertThat(page.locator("#typed-date-value")).hasText("2025-09-20");

        datePicker.clear();

        assertThat(page.locator("#typed-date-value")).hasText("null");
        datePicker.assertValue((LocalDate) null);
        datePicker.assertValid();
    }

    private void countHostChangeEvents(DatePickerElement datePicker) {
        datePicker.getLocator().evaluate("el => { window.__hostChanges = 0; "
                + "el.addEventListener('change', e => { if (e.target === el) window.__hostChanges++; }); }");
    }

    private int hostChangeEvents() {
        return ((Number) page.evaluate("() => window.__hostChanges")).intValue();
    }

    @Test
    public void testTheme() {
        DatePickerElement datePickerElement = DatePickerElement.getByLabel(page, "Appointment date");
        datePickerElement.assertVisible();
        datePickerElement.assertTheme("small");
    }

    @Test
    public void testFocused() {
        DatePickerElement datePickerElement = DatePickerElement.getByLabel(page, "Appointment date");
        DatePickerElement secondDateTimePicker = DatePickerElement.getByLabel(page, "Vacation date");

        datePickerElement.assertIsFocused();
        secondDateTimePicker.assertIsNotFocused();
        secondDateTimePicker.focus();
        secondDateTimePicker.assertIsFocused();
        datePickerElement.assertIsNotFocused();
    }

    @Test
    public void testAriaLabel() {
        DatePickerElement datePickerElement = find(page, DatePickerElement.class).withAccessibleName("Invisible label").single();
        datePickerElement.assertVisible();
        datePickerElement.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        DatePickerElement datePickerElement = find(page, DatePickerElement.class).withAccessibleName("Labelled by").single();
        datePickerElement.assertVisible();
        datePickerElement.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        DatePickerElement datePickerElement = DatePickerElement.getByLabel(page, "Enabled/Disabled Field");
        datePickerElement.assertDisabled();

        page.locator("#enable-disable-button").click();

        datePickerElement.assertEnabled();
    }

    @Test
    public void testTooltip() {
        DatePickerElement datePickerElement = DatePickerElement.getByLabel(page, "Appointment date");
        datePickerElement.assertVisible();
        datePickerElement.assertTooltipHasText("Tooltip for datepicker");
    }
}
