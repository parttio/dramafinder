package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.IntegerFieldElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // ── Typed value ────────────────────────────────────────────────────

    @Test
    public void testSetIntegerValue() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "Quantity");
        integerField.setValue(42);
        integerField.assertValue("42");
        integerField.assertValue(42);
        assertEquals(42, integerField.getValueAsInteger());
        assertThat(page.locator("#quantity-server-value")).hasText("Server value: 42");

        integerField.setValue(-7);
        integerField.assertValue(-7);
        assertThat(page.locator("#quantity-server-value")).hasText("Server value: -7");
    }

    @Test
    public void testIntegerNullClearsTheField() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "Score");
        assertEquals(10, integerField.getValueAsInteger());
        integerField.assertValue(10);

        integerField.setValue((Integer) null);
        integerField.assertValue("");
        integerField.assertValue((Integer) null);
        assertNull(integerField.getValueAsInteger());
    }

    @Test
    public void testIntegerAssertValueFailsOnOtherNumber() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "Score");
        assertThrows(AssertionError.class, () -> integerField.assertValue(20));
    }

    @Test
    public void testIntegerAssertValueOnUnparsableTextFailsAsMismatch() {
        IntegerFieldElement integerField = IntegerFieldElement.getByLabel(page, "Quantity");
        // Beyond Integer.MAX_VALUE: readable as text, not as an Integer
        integerField.setValue("3000000000");
        var e = assertThrows(AssertionError.class, () -> integerField.assertValue(5));
        assertTrue(e.getMessage().contains("3000000000"), e.getMessage());
    }
}
