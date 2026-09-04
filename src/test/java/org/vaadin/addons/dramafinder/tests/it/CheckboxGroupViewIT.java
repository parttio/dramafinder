package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.CheckboxGroupElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CheckboxGroupViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "checkbox-group";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Checkbox Group Demo");
        assertThat(page.getByText("Checkbox Group Demo")).isVisible();
    }

    @Test
    public void testBasicCheckboxGroup() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup");
        group.assertVisible();
        group.assertEnabled();
        group.assertLabel("Basic CheckboxGroup");
        group.assertCheckboxCount(3);
        group.assertSelected();

        group.selectByLabel("Option 1", "Option 3");
        group.assertSelected("Option 1", "Option 3");

        group.deselectByLabel("Option 1");
        group.assertSelected("Option 3");

        group.deselectAll();
        group.assertSelected();
    }

    @Test
    public void testGetSelectedValues() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup");
        assertEquals(List.of(), group.getSelectedValues());

        group.selectByLabel("Option 2", "Option 3");
        assertEquals(List.of("Option 2", "Option 3"), group.getSelectedValues());
    }

    @Test
    public void testGetCheckboxes() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup");
        List<CheckboxElement> checkboxes = group.getCheckboxes();
        assertEquals(3, checkboxes.size());
        assertEquals(3, group.getCheckboxCount());
        checkboxes.forEach(CheckboxElement::assertNotChecked);

        checkboxes.get(1).check();
        group.assertSelected("Option 2");
    }

    @Test
    public void testGetCheckboxByLabel() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Basic CheckboxGroup");
        CheckboxElement checkbox = group.getCheckbox("Option 2");
        checkbox.assertVisible();
        checkbox.assertNotChecked();

        checkbox.check();
        assertTrue(checkbox.isChecked());
        group.assertSelected("Option 2");
    }

    @Test
    public void testPreselectedValue() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Pre-selected Value");
        group.assertVisible();
        group.assertSelected("Option 1", "Option 3");
    }

    @Test
    public void testDisabledCheckboxGroup() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Disabled CheckboxGroup");
        group.assertVisible();
        group.assertDisabled();
        group.getCheckboxes().forEach(CheckboxElement::assertDisabled);
    }

    @Test
    public void testDisabledItem() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Disabled Item");
        group.assertEnabled();
        group.getCheckbox("Option 1").assertEnabled();
        group.getCheckbox("Option 2").assertDisabled();
    }

    @Test
    public void testHelperText() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Helper Text");
        group.assertVisible();
        group.assertHelperHasText("This is a helper text");
    }

    @Test
    public void testThemeAndCssClass() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Vertical CheckboxGroup");
        group.assertTheme("vertical");
        assertEquals("vertical", group.getTheme());
        group.assertCssClass("styled-group");
    }

    @Test
    public void testTooltip() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Tooltip CheckboxGroup");
        group.assertTooltipHasText("This is a tooltip");
    }

    @Test
    public void testRequiredAndInvalid() {
        CheckboxGroupElement group = CheckboxGroupElement.getByLabel(page, "Required CheckboxGroup");
        group.assertValid();

        ButtonElement.getByText(page, "Validate CheckboxGroup").click();
        group.assertInvalid();
        group.assertErrorMessage("Selection is required");

        group.selectByLabel("Option 1");
        ButtonElement.getByText(page, "Validate CheckboxGroup").click();
        group.assertValid();
    }
}
