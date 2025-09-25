package org.vaadin.addons.dramafinderdemo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.DateTimePickerElement;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DateTimePickerViewIT extends SpringPlaywrightIT {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String getView() {
        return "date-time-picker";
    }

    @Test
    public void testBasicDateTimePicker() {
        DateTimePickerElement dateTimePicker = DateTimePickerElement.getByLabel(page, "Appointment date");
        dateTimePicker.assertVisible();
        dateTimePicker.assertHelperHasText("Enter the appointment date and time");
        LocalDateTime testDate = LocalDateTime.of(2025, 9, 20, 10, 0);
        dateTimePicker.setValue(testDate);
        // The value in the input is not formatted the same way as the value in the property
        dateTimePicker.assertDateValue(dateFormatter.format(testDate));
        dateTimePicker.assertTimeValue(timeFormatter.format(testDate));
        dateTimePicker.assertValue(testDate);
    }

    @Test
    public void testDateTimePicker() {
        DateTimePickerElement dateTimePicker = DateTimePickerElement.getByLabel(page, "Appointment date");
        dateTimePicker.assertVisible();
        LocalDateTime testDate = LocalDateTime.of(2025, 9, 20, 10, 0);
        dateTimePicker.setDate(dateFormatter.format(testDate));
        dateTimePicker.setTime(timeFormatter.format(testDate));
        // The value in the input is not formatted the same way as the value in the property
        dateTimePicker.assertDateValue(dateFormatter.format(testDate));
        dateTimePicker.assertTimeValue(timeFormatter.format(testDate));
        dateTimePicker.assertValue(testDate);
    }

    @Test
    public void testIncompleteDateTimePicker() {
        DateTimePickerElement dateTimePicker = DateTimePickerElement.getByLabel(page, "Appointment date");
        dateTimePicker.assertVisible();
        LocalDateTime testDate = LocalDateTime.of(2025, 9, 20, 10, 0);
        dateTimePicker.setDate(dateFormatter.format(testDate));
        dateTimePicker.setTime(timeFormatter.format(testDate));
        // The value in the input is not formatted the same way as the value in the property
        dateTimePicker.assertDateValue(dateFormatter.format(testDate));
        dateTimePicker.assertTimeValue(timeFormatter.format(testDate));
        dateTimePicker.assertValid();
        // force the datetime to validate
        DateTimePickerElement secondDateTimePicker = DateTimePickerElement.getByLabel(page, "Vacation date");
        secondDateTimePicker.focus();
        secondDateTimePicker.assertIsFocused();
        dateTimePicker.setTime("");
        dateTimePicker.assertInvalid();
    }


    @Test
    public void testMinMax() {
        DateTimePickerElement datePicker = DateTimePickerElement.getByLabel(page, "Vacation date");
        datePicker.assertVisible();

        LocalDateTime startDate = LocalDateTime.of(2025, 9, 18, 14, 30);
        LocalDateTime initialDate = startDate.plusDays(7);
        LocalDateTime maxDate = startDate.plusDays(30);
        datePicker.assertValue(initialDate);

        // Test setting a value within bounds
        LocalDateTime validDate = startDate.plusDays(15);
        datePicker.setValue(validDate);

        datePicker.assertValue(validDate);
        datePicker.assertValid();

        // Test setting a value outside bounds (after max)
        LocalDateTime invalidDate = maxDate.plusDays(1);
        datePicker.setValue(invalidDate);
        datePicker.assertInvalid();
        datePicker.assertErrorMessage("Maximum exceeded.");

        // Test setting a value outside bounds (before min)
        LocalDateTime anotherInvalidDate = startDate.minusDays(1);
        datePicker.setValue(anotherInvalidDate);
        datePicker.assertInvalid();
        datePicker.assertErrorMessage("Minimum exceeded.");

        // Set a valid value again
        datePicker.setValue(initialDate);
        datePicker.assertValid();
    }

    @Test
    public void testTheme() {
        DateTimePickerElement dateTimePickerElement = DateTimePickerElement.getByLabel(page, "Appointment date");
        dateTimePickerElement.assertVisible();
        dateTimePickerElement.assertTheme("small");
    }

    @Test
    public void testFocused() {
        DateTimePickerElement dateTimePickerElement = DateTimePickerElement.getByLabel(page, "Appointment date");
        DateTimePickerElement secondDateTimePicker = DateTimePickerElement.getByLabel(page, "Vacation date");

        dateTimePickerElement.assertIsFocused();
        secondDateTimePicker.assertIsNotFocused();
        secondDateTimePicker.focus();
        secondDateTimePicker.assertIsFocused();
        dateTimePickerElement.assertIsNotFocused();
    }

    @Test
    public void testAriaLabel() {
        DateTimePickerElement dateTimePickerElement = DateTimePickerElement.getByLabel(page, "Invisible label");
        dateTimePickerElement.assertVisible();
        dateTimePickerElement.assertAriaLabel("Invisible label");
    }

    @Test
    public void testEnabledDisabled() {
        DateTimePickerElement dateTimePickerElement = DateTimePickerElement.getByLabel(page, "Enabled/Disabled Field");
        dateTimePickerElement.assertDisabled();

        page.locator("#enable-disable-button").click();

        dateTimePickerElement.assertEnabled();
    }

    @Test
    public void testTooltip() {
        DateTimePickerElement dateTimePickerElement = DateTimePickerElement.getByLabel(page, "Appointment date");
        dateTimePickerElement.assertVisible();
        dateTimePickerElement.assertTooltipHasText("Tooltip for datetimepicker");
    }
}
