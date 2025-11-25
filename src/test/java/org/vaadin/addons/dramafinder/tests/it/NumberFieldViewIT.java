package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.NumberFieldElement;
import org.vaadin.addons.dramafinder.tests.it.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NumberFieldViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "number";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("NumberField Demo");
        assertThat(page.getByText("NumberField Demo")).isVisible();
    }

    @Test
    public void testBasicNumberField() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Weight");
        numberField.assertVisible();
        numberField.assertHelperHasText("Enter the weight in kg");
        numberField.setValue("12.3");
        numberField.assertValue("12.3");
    }

    @Test
    public void testMinMaxStep() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Measurement");
        numberField.assertVisible();
        numberField.assertMin(0.5);
        numberField.assertMax(10.0);
        numberField.assertStep(0.5);

        // Check initial value
        numberField.assertValue("1.5");

        // Test setting a value within bounds
        numberField.setValue("5.5");
        numberField.assertValue("5.5");

        // Test setting a value outside bounds
        numberField.setValue("11.5");
        numberField.assertValue("11.5");
        numberField.assertInvalid();

        numberField.setValue("0");
        numberField.assertInvalid();

        // Set a valid value again
        numberField.setValue("2.5");
        numberField.assertValid();
    }

    @Test
    public void testStepControls() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "With Controls");
        numberField.assertVisible();
        numberField.assertHasControls(true);

        // Assuming the field is empty initially
        numberField.assertValue("");

        // Increment
        numberField.clickIncreaseButton();
        numberField.assertValue("0.1");

        // Increment again
        numberField.clickIncreaseButton();
        numberField.assertValue("0.2");

        // Decrement
        numberField.clickDecreaseButton();
        numberField.assertValue("0.1");
    }
}
