package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.element.CheckboxElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CheckboxViewIT extends SpringPlaywrightIT {


    @Override
    public String getView() {
        return "checkbox";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Checkbox Demo");
        assertThat(page.getByText("Checkbox Demo")).isVisible();
    }

    @Test
    public void testBasicCheckbox() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Default Checkbox");
        checkbox.assertVisible();
        checkbox.assertNotChecked();
        checkbox.assertEnabled();

        // Check and uncheck
        checkbox.check();
        checkbox.assertChecked();
        checkbox.uncheck();
        checkbox.assertNotChecked();
    }

    @Test
    public void testCheckedCheckbox() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Checked by default");
        checkbox.assertVisible();
        checkbox.assertChecked();
    }

    @Test
    public void testIndeterminateCheckbox() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Indeterminate Checkbox");
        checkbox.assertVisible();
        checkbox.assertIndeterminate();

        // Clicking should change its state
        checkbox.check();
        checkbox.assertNotIndeterminate();
        checkbox.assertChecked();
    }

    @Test
    public void testDisabledCheckbox() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Disabled Checkbox");
        checkbox.assertVisible();
        checkbox.assertDisabled();
    }

    @Test
    public void testLabelText() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Default Checkbox");
        checkbox.assertLabel("Default Checkbox");
    }

    @Test
    public void testAriaLabel() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Aria label");
        checkbox.assertAriaLabel("Default Checkbox");
        checkbox.assertLabel(null);
    }

    @Test
    public void testFocus() {
        CheckboxElement checkbox = CheckboxElement.getByLabel(page, "Aria label");
        checkbox.assertAriaLabel("Default Checkbox");
        checkbox.assertLabel(null);
    }


    @Test
    public void testFocused() {
        CheckboxElement checkboxElement = CheckboxElement.getByLabel(page, "Default Checkbox");
        CheckboxElement secondCheckbox = CheckboxElement.getByLabel(page, "Checked by default");

        assertEquals("", checkboxElement.getLocator().getAttribute("focused"));
        assertNull(secondCheckbox.getLocator().getAttribute("focused"));
        checkboxElement.assertIsFocused();
        secondCheckbox.assertIsNotFocused();
        secondCheckbox.focus();
        secondCheckbox.assertIsFocused();
    }
}
