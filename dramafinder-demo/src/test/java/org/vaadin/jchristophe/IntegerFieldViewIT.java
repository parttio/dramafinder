package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.element.IntegerFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegerFieldViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "integer";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("IntegerField Demo");
        assertThat(page.getByText("IntegerField Demo")).isVisible();
    }

    @Test
    public void testBasicIntegerField() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "Quantity");
        integerField.assertVisible();
        integerField.assertHelperHasText("Enter the number of items");
        integerField.setValue("123");
        integerField.assertValue("123");

        integerField.assertAriaLabel(null);
        integerField.assertMax(null);
    }

    @Test
    public void testMinMaxStep() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "Score");
        integerField.assertVisible();
        integerField.assertMin(0);
        integerField.assertMax(100);
        integerField.assertStep(10);

        // Check initial value
        integerField.assertValue("10");

        // Test setting a value within bounds
        integerField.setValue("50");
        integerField.assertValue("50");

        // Test setting a value outside bounds (behavior might depend on component, just checking value)
        integerField.setValue("110");
        integerField.assertValue("110"); // Vaadin fields often allow out-of-bounds values but mark as invalid
        integerField.assertInvalid();

        integerField.setValue("-10");
        integerField.assertInvalid();

        // Set a valid value again
        integerField.setValue("20");
        integerField.assertValid();
    }

    @Test
    public void testStepControls() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "With Controls");
        integerField.assertVisible();
        integerField.assertHasControls(true);

        // Assuming the field is empty initially
        integerField.assertValue("");

        // Increment
        integerField.clickIncreaseButton();
        integerField.assertValue("1");

        // Increment again
        integerField.clickIncreaseButton();
        integerField.assertValue("2");

        // Decrement
        integerField.clickDecreaseButton();
        integerField.assertValue("1");
    }
}
