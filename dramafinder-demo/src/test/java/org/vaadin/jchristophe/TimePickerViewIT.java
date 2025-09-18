package org.vaadin.jchristophe;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.element.TimePickerElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TimePickerViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "time-picker";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("TimePicker Demo");
        assertThat(page.getByText("TimePicker Demo")).isVisible();
    }

    @Test
    public void testBasicTimePicker() {
        TimePickerElement timePicker = TimePickerElement.getByLabel(page, "Appointment time");
        timePicker.assertVisible();
        timePicker.assertHelperHasText("Enter the appointment time");
        LocalTime testTime = LocalTime.of(14, 30);
        timePicker.setValue(testTime);
        timePicker.assertValue("14:30");
        timePicker.assertValue(testTime);
    }

    @Test
    public void testMinMaxStep() {
        TimePickerElement timePicker = TimePickerElement.getByLabel(page, "Meeting time");
        timePicker.assertVisible();

        LocalTime initialTime = LocalTime.of(10, 30);
        LocalTime minTime = LocalTime.of(9, 0);
        LocalTime maxTime = LocalTime.of(17, 0);

        // Check initial value
        timePicker.assertValue(initialTime);

        // Test setting a value within bounds
        LocalTime validTime = LocalTime.of(11, 0);
        timePicker.setValue(validTime);
        timePicker.assertValue(validTime);

        timePicker.assertValid();

        // Test setting a value outside bounds (after max)
        LocalTime invalidTime = maxTime.plusHours(1);
        timePicker.setValue(invalidTime);
        timePicker.assertInvalid();
        timePicker.assertErrorMessage("Maximum time exceeded");

        // Test setting a value outside bounds (before min)
        LocalTime anotherInvalidTime = minTime.minusHours(1);
        timePicker.setValue(anotherInvalidTime);
        timePicker.assertInvalid();
        timePicker.assertErrorMessage("Minimum time exceeded");

        // Set a valid value again
        timePicker.setValue(initialTime);
        timePicker.assertValid();
    }

    @Test
    public void testTheme() {
        TimePickerElement timePickerElement = TimePickerElement.getByLabel(page, "Appointment time");
        timePickerElement.assertVisible();
        timePickerElement.assertTheme("small");
    }

    @Test
    public void testFocused() {
        TimePickerElement timePickerElement = TimePickerElement.getByLabel(page, "Appointment time");
        TimePickerElement secondDateTimePicker = TimePickerElement.getByLabel(page, "Meeting time");

        timePickerElement.assertIsFocused();
        secondDateTimePicker.assertIsNotFocused();
        secondDateTimePicker.focus();
        secondDateTimePicker.assertIsFocused();
        timePickerElement.assertIsNotFocused();
    }

    @Test
    public void testAriaLabel() {
        TimePickerElement timePickerElement = TimePickerElement.getByLabel(page, "Invisible label");
        timePickerElement.assertVisible();
        timePickerElement.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        TimePickerElement timePickerElement = TimePickerElement.getByLabel(page, "Labelled by");
        timePickerElement.assertVisible();
        timePickerElement.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        TimePickerElement timePickerElement = TimePickerElement.getByLabel(page, "Enabled/Disabled Field");
        timePickerElement.assertDisabled();

        page.locator("#enable-disable-button").click();

        timePickerElement.assertEnabled();
    }

    @Test
    public void testTooltip() {
        TimePickerElement timePickerElement = TimePickerElement.getByLabel(page, "Appointment time");
        timePickerElement.assertVisible();
        timePickerElement.assertTooltipHasText("Tooltip for component");
    }
}
