package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.tests.it.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TextFieldViewIT extends SpringPlaywrightIT {

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("TextField Demo");
        assertThat(page.getByText("TextField Demo")).isVisible();
    }

    @Test
    public void testHasClass() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertCssClass("custom-text-field");
        assertThat(textfield.getLocator()).hasClass("custom-text-field");
    }

    @Test
    public void testHasHelperText() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertVisible();
        textfield.assertCssClass("custom-text-field");
        assertThat(textfield.getLocator()).hasClass("custom-text-field");
        assertThat(textfield.getHelperLocator()).hasText("Helper text");
        textfield.assertHelperHasText("Helper text");
        assertEquals("Helper text", textfield.getHelperText());
    }

    @Test
    public void testHasHelperComponent() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with helper component");
        textfield.assertVisible();
        textfield.assertCssClass("custom-text-field");
        TextFieldElement helperComponent = new TextFieldElement(textfield.getHelperLocator());
        helperComponent.assertCssClass("custom-helper-component");
        helperComponent.assertVisible();
        assertThat(helperComponent.getHelperLocator()).hasText("Internal helper");
        helperComponent.assertHelperHasText("Internal helper");
    }

    @Test
    public void testValue() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertValue("");
        textfield.setValue("new value");
        textfield.assertValue("new value");
    }

    @Test
    public void testInvalid() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        textfield.assertValue("");
        textfield.setValue("new value");
        textfield.assertValid();
        textfield.assertValue("new value");
        textfield.setValue("");
        // field is required
        textfield.assertInvalid();
        assertThat(textfield.getErrorMessageLocator()).hasText("Field is required");
    }

    @Test
    public void testPrefixAndSuffix() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        // Locators for prefix/suffix should exist and be visible
        assertThat(textfield.getPrefixLocator()).isVisible();
        assertThat(textfield.getSuffixLocator()).isVisible();
        assertThat(textfield.getPrefixLocator()).hasText("Prefix");
        assertThat(textfield.getSuffixLocator()).hasText("Suffix");
        textfield.assertPrefixHasText("Prefix");
        textfield.assertSuffixHasText("Suffix");
        // Also verify through convenience methods
        assertEquals("Prefix", textfield.getPrefixText());
        assertEquals("Suffix", textfield.getSuffixText());
    }


    @Test
    public void testNoPrefixAndSuffix() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with helper component");
        textfield.assertPrefixHasText(null);
        textfield.assertSuffixHasText(null);
    }

    @Test
    public void testAllowedCharPattern() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        textfield.assertVisible();
        // Attributes presence
        assertEquals("[0-8]", textfield.getAllowedCharPattern());
        textfield.assertAllowedCharPattern("[0-8]");
    }

    @Test
    public void testPattern() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        assertThat(textfield.getInputLocator()).hasAttribute("pattern", "\\d{7}");
        textfield.assertPattern("\\d{7}");
        assertEquals("\\d{7}", textfield.getPattern());
    }

    @Test
    public void testMinLength() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        assertThat(textfield.getInputLocator()).hasAttribute("minlength", "6");
        textfield.assertMinLength(6);
        assertEquals(6, textfield.getMinLength());
    }

    @Test
    public void testMaxLength() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        assertThat(textfield.getInputLocator()).hasAttribute("maxlength", "7");
        textfield.assertMaxLength(7);
        assertEquals(7, textfield.getMaxLength());
    }

    @Test
    public void testValidatedFieldBehavior() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        // Invalid value should mark field invalid
        textfield.setValue("1");
        textfield.assertInvalid();
        textfield.assertErrorMessage("Minimum length is 6 characters");

        // Test the pattern
        textfield.setValue("123845");
        textfield.assertInvalid();
        assertThat(textfield.getErrorMessageLocator()).hasText("Invalid code format");
        // 9 is not valid
        textfield.setValue("991238456");

        textfield.setValue("1238456");
        textfield.assertValid();
    }

    @Test
    public void testPlaceholder() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with placeholder and clear button");
        textfield.assertVisible();
        textfield.assertPlaceholder("Enter text here");
        assertEquals("Enter text here", textfield.getPlaceholder());
    }

    @Test
    public void testClearButton() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with placeholder and clear button");
        textfield.assertVisible();
        textfield.assertClearButtonNotVisible();
        textfield.setValue("some value");
        textfield.assertValue("some value");
        textfield.assertClearButtonVisible();
        textfield.clickClearButton();
        textfield.assertValue("");
    }

    @Test
    public void testClear() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with placeholder and clear button");
        textfield.assertVisible();
        textfield.assertClearButtonNotVisible();
        textfield.setValue("some value");
        textfield.assertValue("some value");
        textfield.assertClearButtonVisible();
        textfield.clear();
        textfield.assertValue("");
    }

    @Test
    public void testTheme() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with theme");
        textfield.assertVisible();
        textfield.assertTheme("small");
    }

    @Test
    public void testFocused() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField");
        TextFieldElement textFieldWithHelperComponent = TextFieldElement.getByLabel(page, "TextField with helper component");

        assertEquals("", textfield.getLocator().getAttribute("focused"));
        assertNull(textFieldWithHelperComponent.getLocator().getAttribute("focused"));
        textfield.assertIsFocused();
        textFieldWithHelperComponent.assertIsNotFocused();
        textFieldWithHelperComponent.focus();
        textFieldWithHelperComponent.assertIsFocused();
    }

    @Test
    public void testAriaLabel() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Invisible label");
        textfield.assertVisible();
        textfield.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Labelled by");
        textfield.assertVisible();
        textfield.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Enabled/Disabled Field");
        textfield.assertDisabled();

        page.locator("#enable-disable-button").click();

        textfield.assertEnabled();
    }

    @Test
    public void testLabel() {
        TextFieldElement textfieldWithLabel = TextFieldElement.getByLabel(page, "Textfield");
        textfieldWithLabel.assertVisible();
        textfieldWithLabel.assertLabel("Textfield");
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Invisible label");
        textfield.assertVisible();
        textfield.assertLabel(null);
    }

    @Test
    public void testTooltip() {
        TextFieldElement textfieldWithLabel = TextFieldElement.getByLabel(page, "Textfield");
        textfieldWithLabel.assertVisible();
        textfieldWithLabel.assertTooltipHasText("Tooltip for textfield");
    }
}
