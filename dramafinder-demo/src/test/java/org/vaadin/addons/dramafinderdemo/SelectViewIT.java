package org.vaadin.addons.dramafinderdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.SelectElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SelectViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "select";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("Select Demo");
    }

    @Test
    public void testSelect() {
        SelectElement select = SelectElement.getByLabel(page, "Sort by");
        select.assertVisible();
        select.assertValue(null);
        select.selectItem("Rating: high to low");
        select.assertValue("Rating: high to low");
    }

    @Test
    public void testHasClass() {
        SelectElement textfield = SelectElement.getByLabel(page, "Sort by");
        textfield.assertCssClass("custom-text-field");
        assertThat(textfield.getLocator()).hasClass("custom-text-field");
    }

    @Test
    public void testHasHelperText() {
        SelectElement textfield = SelectElement.getByLabel(page, "Sort by");
        textfield.assertVisible();
        textfield.assertCssClass("custom-text-field");
        assertThat(textfield.getLocator()).hasClass("custom-text-field");
        assertThat(textfield.getHelperLocator()).hasText("Helper text");
        textfield.assertHelperHasText("Helper text");
        assertEquals("Helper text", textfield.getHelperText());
    }

    @Test
    public void testHasHelperComponent() {
        SelectElement textfield = SelectElement.getByLabel(page, "Select with helper component");
        textfield.assertVisible();
        textfield.assertCssClass("custom-text-field");
        SelectElement helperComponent = new SelectElement(textfield.getHelperLocator());
        helperComponent.assertCssClass("custom-helper-component");
        helperComponent.assertVisible();
        assertThat(helperComponent.getHelperLocator()).hasText("Internal helper");
        helperComponent.assertHelperHasText("Internal helper");
    }

    @Test
    public void testInvalid() {
        SelectElement select = SelectElement.getByLabel(page, "Select with theme");
        select.assertValue(null);
        select.assertValid();
        ButtonElement.getByText(page, "Validate").click();
        // field is required
        select.assertInvalid();
        assertThat(select.getErrorMessageLocator()).hasText("Required field");
    }

    @Test
    public void testPrefix() {
        SelectElement textfield = SelectElement.getByLabel(page, "Sort by");
        // Locators for prefix/suffix should exist and be visible
        assertThat(textfield.getPrefixLocator()).isVisible();
        assertThat(textfield.getPrefixLocator()).hasText("Prefix");
        textfield.assertPrefixHasText("Prefix");
        // Also verify through convenience methods
        assertEquals("Prefix", textfield.getPrefixText());
    }


    @Test
    public void testNoPrefix() {
        SelectElement select = SelectElement.getByLabel(page, "Select with helper component");
        select.assertPrefixHasText(null);
    }

    @Test
    public void testPlaceholder() {
        SelectElement select = SelectElement.getByLabel(page, "Select with placeholder and clear button");
        select.assertVisible();
        select.assertPlaceholder("Enter text here");
        assertEquals("Enter text here", select.getPlaceholder());
    }

    @Test
    public void testTheme() {
        SelectElement textfield = SelectElement.getByLabel(page, "Select with theme");
        textfield.assertVisible();
        textfield.assertTheme("small");
    }

    @Test
    public void testFocused() {
        SelectElement select = SelectElement.getByLabel(page, "Sort by");
        SelectElement selectWithHelperComponent = SelectElement.getByLabel(page, "Select with helper component");

        assertEquals("", select.getLocator().getAttribute("focused"));
        assertNull(selectWithHelperComponent.getLocator().getAttribute("focused"));
        select.assertIsFocused();
        selectWithHelperComponent.assertIsNotFocused();
        selectWithHelperComponent.focus();
        selectWithHelperComponent.assertIsFocused();
    }

    @Test
    public void testAriaLabel() {
        SelectElement select = SelectElement.getByLabel(page, "Invisible label");
        select.assertVisible();
        select.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        SelectElement textfield = SelectElement.getByLabel(page, "Labelled by");
        textfield.assertVisible();
        textfield.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        SelectElement select = SelectElement.getByLabel(page, "Enabled/Disabled Field");
        select.assertDisabled();

        page.locator("#enable-disable-button").click();

        select.assertEnabled();
    }

    @Test
    public void testLabel() {
        SelectElement select = SelectElement.getByLabel(page, "Sort by");
        select.assertVisible();
        select.assertLabel("Sort by");
        SelectElement ariaLabel = SelectElement.getByLabel(page, "Invisible label");
        ariaLabel.assertVisible();
        ariaLabel.assertLabel(null);
    }

    @Test
    public void testTooltip() {
        SelectElement textfieldWithLabel = SelectElement.getByLabel(page, "Sort by");
        textfieldWithLabel.assertVisible();
        textfieldWithLabel.assertTooltipHasText("Tooltip for select");
    }
}
