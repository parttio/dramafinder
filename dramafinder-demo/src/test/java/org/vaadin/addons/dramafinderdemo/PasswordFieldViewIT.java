package org.vaadin.addons.dramafinderdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.PasswordFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PasswordFieldViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "password-field";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("PasswordField Demo");
    }

    @Test
    public void testHasClass() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField");
        passwordField.assertCssClass("custom-text-field");
        assertThat(passwordField.getLocator()).hasClass("custom-text-field");
    }

    @Test
    public void testHasHelperText() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField");
        passwordField.assertVisible();
        passwordField.assertCssClass("custom-text-field");
        assertThat(passwordField.getLocator()).hasClass("custom-text-field");
        assertThat(passwordField.getHelperLocator()).hasText("Helper text");
        passwordField.assertHelperHasText("Helper text");
        assertEquals("Helper text", passwordField.getHelperText());
    }

    @Test
    public void testHasHelperComponent() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField with helper component");
        passwordField.assertVisible();
        passwordField.assertCssClass("custom-text-field");
        PasswordFieldElement helperComponent = new PasswordFieldElement(passwordField.getHelperLocator());
        helperComponent.assertCssClass("custom-helper-component");
        helperComponent.assertVisible();
        assertThat(helperComponent.getHelperLocator()).hasText("Internal helper");
        helperComponent.assertHelperHasText("Internal helper");
    }

    @Test
    public void testValue() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField");
        passwordField.assertValue("");
        passwordField.setValue("new value");
        passwordField.assertValue("new value");
    }

    @Test
    public void testInvalid() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField");
        passwordField.assertValue("");
        passwordField.setValue("new value");
        passwordField.assertValid();
        passwordField.assertValue("new value");
        passwordField.setValue("");
        // field is required
        passwordField.assertInvalid();
        assertThat(passwordField.getErrorMessageLocator()).hasText("Field is required");
    }

    @Test
    public void testPrefixAndSuffix() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField");
        // Locators for prefix/suffix should exist and be visible
        assertThat(passwordField.getPrefixLocator()).isVisible();
        assertThat(passwordField.getSuffixLocator()).isVisible();
        assertThat(passwordField.getPrefixLocator()).hasText("Prefix");
        assertThat(passwordField.getSuffixLocator()).hasText("Suffix");
        passwordField.assertPrefixHasText("Prefix");
        passwordField.assertSuffixHasText("Suffix");
        // Also verify through convenience methods
        assertEquals("Prefix", passwordField.getPrefixText());
        assertEquals("Suffix", passwordField.getSuffixText());
    }


    @Test
    public void testNoPrefixAndSuffix() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField with helper component");
        passwordField.assertPrefixHasText(null);
        passwordField.assertSuffixHasText(null);
    }

    @Test
    public void testAllowedCharPattern() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Validated PasswordField");
        passwordField.assertVisible();
        // Attributes presence
        assertEquals("[0-8]", passwordField.getAllowedCharPattern());
        passwordField.assertAllowedCharPattern("[0-8]");
    }

    @Test
    public void testPattern() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Validated PasswordField");
        assertThat(passwordField.getInputLocator()).hasAttribute("pattern", "\\d{7}");
        passwordField.assertPattern("\\d{7}");
        assertEquals("\\d{7}", passwordField.getPattern());
    }

    @Test
    public void testMinLength() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Validated PasswordField");
        assertThat(passwordField.getInputLocator()).hasAttribute("minlength", "6");
        passwordField.assertMinLength(6);
        assertEquals(6, passwordField.getMinLength());
    }

    @Test
    public void testMaxLength() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Validated PasswordField");
        assertThat(passwordField.getInputLocator()).hasAttribute("maxlength", "7");
        passwordField.assertMaxLength(7);
        assertEquals(7, passwordField.getMaxLength());
    }

    @Test
    public void testValidatedFieldBehavior() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Validated PasswordField");
        // Invalid value should mark field invalid
        passwordField.setValue("1");
        passwordField.assertInvalid();
        passwordField.assertErrorMessage("Minimum length is 6 characters");

        // Test the pattern
        passwordField.setValue("123845");
        passwordField.assertInvalid();
        assertThat(passwordField.getErrorMessageLocator()).hasText("Invalid code format");
        // 9 is not valid
        passwordField.setValue("991238456");

        passwordField.setValue("1238456");
        passwordField.assertValid();
    }

    @Test
    public void testPlaceholder() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField with placeholder and clear button");
        passwordField.assertVisible();
        passwordField.assertPlaceholder("Enter text here");
        assertEquals("Enter text here", passwordField.getPlaceholder());
    }

    @Test
    public void testClearButton() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField with placeholder and clear button");
        passwordField.assertVisible();
        passwordField.assertClearButtonNotVisible();
        passwordField.setValue("some value");
        passwordField.assertValue("some value");
        passwordField.assertClearButtonVisible();
        passwordField.clickClearButton();
        passwordField.assertValue("");
    }

    @Test
    public void testClear() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField with placeholder and clear button");
        passwordField.assertVisible();
        passwordField.assertClearButtonNotVisible();
        passwordField.setValue("some value");
        passwordField.assertValue("some value");
        passwordField.assertClearButtonVisible();
        passwordField.clear();
        passwordField.assertValue("");
    }

    @Test
    public void testTheme() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField with theme");
        passwordField.assertVisible();
        passwordField.assertTheme("small");
    }

    @Test
    public void testFocused() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "PasswordField");
        PasswordFieldElement passwordFieldWithHelperComponent = PasswordFieldElement.getByLabel(page, "PasswordField with helper component");

        assertEquals("", passwordField.getLocator().getAttribute("focused"));
        assertNull(passwordFieldWithHelperComponent.getLocator().getAttribute("focused"));
        passwordField.assertIsFocused();
        passwordFieldWithHelperComponent.assertIsNotFocused();
        passwordFieldWithHelperComponent.focus();
        passwordFieldWithHelperComponent.assertIsFocused();
    }

    @Test
    public void testAriaLabel() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Invisible label");
        passwordField.assertVisible();
        passwordField.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Labelled by");
        passwordField.assertVisible();
        passwordField.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Enabled/Disabled Field");
        passwordField.assertDisabled();

        page.locator("#enable-disable-button").click();

        passwordField.assertEnabled();
    }

    @Test
    public void testLabel() {
        PasswordFieldElement passwordFieldWithLabel = PasswordFieldElement.getByLabel(page, "PasswordField");
        passwordFieldWithLabel.assertVisible();
        passwordFieldWithLabel.assertLabel("PasswordField");
        PasswordFieldElement passwordField = PasswordFieldElement.getByLabel(page, "Invisible label");
        passwordField.assertVisible();
        passwordField.assertLabel(null);
    }

    @Test
    public void testTooltip() {
        PasswordFieldElement passwordFieldWithLabel = PasswordFieldElement.getByLabel(page, "PasswordField");
        passwordFieldWithLabel.assertVisible();
        passwordFieldWithLabel.assertTooltipHasText("Tooltip for passwordField");
    }
}
