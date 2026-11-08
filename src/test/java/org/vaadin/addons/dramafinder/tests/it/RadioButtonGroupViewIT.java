package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.RadioButtonGroupElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        group.assertLabel("Basic RadioButtonGroup");
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

    // ── Labels ─────────────────────────────────────────────────────────

    @Test
    public void testItemLabels() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup");
        assertEquals(List.of("Option 1", "Option 2", "Option 3"), group.getItemLabels());
    }

    @Test
    public void testSelectedLabel() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Basic RadioButtonGroup");
        assertNull(group.getSelectedLabel());
        group.assertSelectedLabel(null);

        group.selectByLabel("Option 3");
        assertEquals("Option 3", group.getSelectedLabel());
        group.assertSelectedLabel("Option 3");
        assertThrows(AssertionError.class, () -> group.assertSelectedLabel("Option 1"));
    }

    @Test
    public void testPreselectedLabel() {
        RadioButtonGroupElement group = RadioButtonGroupElement.getByLabel(page, "Pre-selected Value");
        group.assertSelectedLabel("Option 2");
    }
}
