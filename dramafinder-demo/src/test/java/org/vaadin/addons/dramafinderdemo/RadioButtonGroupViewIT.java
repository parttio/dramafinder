package org.vaadin.addons.dramafinderdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.RadioButtonGroupElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RadioButtonGroupViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "radio-button-group";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Radio Button Demo");
        assertThat(page.getByText("Radio Button Demo")).isVisible();
    }

    @Test
    public void testBasicRadioGroup() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup");
        group.assertVisible();
        group.assertEnabled();
        group.assertValue(null);

        group.selectByLabel("Option 2");
        group.assertValue("Option 2");

        group.selectByLabel("Option 3");
        group.assertValue("Option 3");
    }

    @Test
    public void testPreselectedValue() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Pre-selected Value");
        group.assertVisible();
        group.assertValue("Option 2");
    }

    @Test
    public void testDisabledRadioGroup() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Disabled RadioButtonGroup");
        group.assertVisible();
        group.assertDisabled();
    }

    @Test
    public void testHelperText() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Helper Text");
        group.assertVisible();
        group.assertHelperHasText("This is a helper text");
    }
}
