package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.NumberFieldElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    // ── Typed value ────────────────────────────────────────────────────

    @Test
    public void testSetWholeNumberTypesItWithoutFraction() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Weight");
        numberField.setValue(170.0);
        numberField.assertValue("170");
        numberField.assertValue(170.0);
        assertEquals(170.0, numberField.getValueAsDouble());
        assertThat(page.locator("#weight-server-value")).hasText("Server value: 170.0");
    }

    @Test
    public void testSetFractionalNumber() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Weight");
        numberField.setValue(180.5);
        numberField.assertValue("180.5");
        numberField.assertValue(180.5);
        assertEquals(180.5, numberField.getValueAsDouble());
        assertThat(page.locator("#weight-server-value")).hasText("Server value: 180.5");
    }

    @Test
    public void testNullClearsTheField() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Measurement");
        assertEquals(1.5, numberField.getValueAsDouble());
        numberField.assertValue(1.5);

        numberField.setValue((Double) null);
        numberField.assertValue("");
        numberField.assertValue((Double) null);
        assertNull(numberField.getValueAsDouble());
    }

    @Test
    public void testAssertValueComparesNumbers() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Weight");
        numberField.setValue("2.50");
        numberField.assertValue(2.5);
        assertThrows(AssertionError.class, () -> numberField.assertValue(3.0));
    }

    @Test
    public void testNonFiniteValueIsRejected() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Weight");
        assertThrows(IllegalArgumentException.class, () -> numberField.setValue(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> numberField.setValue(Double.POSITIVE_INFINITY));
    }

    @Test
    public void testNegativeZeroEqualsZero() {
        NumberFieldElement numberField = NumberFieldElement.getByLabel(page, "Weight");
        numberField.setValue(-0.0);
        numberField.assertValue(0.0);
        numberField.assertValue(-0.0);
    }
}
