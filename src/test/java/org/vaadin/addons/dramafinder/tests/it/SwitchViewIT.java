package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.SwitchElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SwitchViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "switch";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Switch Demo");
        assertThat(page.getByText("Switch Demo")).isVisible();
    }

    @Test
    public void testBasicSwitch() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Default Switch");
        switchElement.assertVisible();
        switchElement.assertNotChecked();
        switchElement.assertEnabled();

        // Check and uncheck
        switchElement.check();
        switchElement.assertChecked();
        switchElement.uncheck();
        switchElement.assertNotChecked();
    }

    @Test
    public void testSetCheckedAndAssertion() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Default Switch");
        switchElement.assertChecked(false);
        assertFalse(switchElement.isChecked());
        switchElement.setChecked(true);
        switchElement.assertChecked(true);
        assertTrue(switchElement.isChecked());
        switchElement.setChecked(false);
        switchElement.assertChecked(false);
    }

    @Test
    public void testCheckedSwitch() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Checked by default");
        switchElement.assertVisible();
        switchElement.assertChecked();
    }

    @Test
    public void testDisabledSwitch() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Disabled Switch");
        switchElement.assertVisible();
        switchElement.assertDisabled();
    }

    @Test
    public void testLabelText() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Default Switch");
        switchElement.assertLabel("Default Switch");
    }

    @Test
    public void testAriaLabel() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Aria label");
        switchElement.assertAriaLabel("Aria label");
        switchElement.assertLabel(null);
    }

    @Test
    public void testHelperText() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Switch with helper");
        switchElement.assertHelperHasText("Helper text");
    }

    @Test
    public void testFocused() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Default Switch");
        SwitchElement secondSwitch = SwitchElement.getByLabel(page, "Checked by default");

        assertEquals("", switchElement.getLocator().getAttribute("focused"));
        assertNull(secondSwitch.getLocator().getAttribute("focused"));
        switchElement.assertIsFocused();
        secondSwitch.assertIsNotFocused();
        secondSwitch.focus();
        secondSwitch.assertIsFocused();
    }

    @Test
    public void testRequired() {
        SwitchElement switchElement = SwitchElement.getByLabel(page, "Required Switch");
        switchElement.assertValid();
        switchElement.check();
        switchElement.assertValid();
        switchElement.uncheck();
        switchElement.assertInvalid();
        switchElement.assertErrorMessage("Required Message");
    }
}
