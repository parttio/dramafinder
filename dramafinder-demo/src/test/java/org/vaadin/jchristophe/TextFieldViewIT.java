package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.VaadinAssertions;
import org.vaadin.dramafinder.element.TextFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TextFieldViewIT extends SpringPlaywrightIT {

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("TextField Demo");
        assertThat(page.getByText("TextField Demo")).isVisible();
    }

    @Test
    public void testHasHelperText() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        assertThat(textfield.getLocator()).isVisible();
        assertThat(textfield.getLocator()).hasClass("custom-text-field");
        assertThat(textfield.getHelperTextLocator()).hasText("Helper text");
    }

    @Test
    public void testHasHelperComponent() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with helper component");
        assertThat(textfield.getLocator()).isVisible();
        assertThat(textfield.getLocator()).hasClass("custom-text-field");
      //  assertThat(textfield.getHelperTextLocator()).not().hasText("Internal helper");
        TextFieldElement helperComponent = new TextFieldElement(textfield.getHelperTextLocator());
        assertThat(helperComponent.getLocator()).hasClass("custom-helper-component");
        assertThat(helperComponent.getLocator()).isVisible();
        assertThat(helperComponent.getHelperTextLocator()).hasText("Internal helper");
    }

    @Test
    public void testValue() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        assertThat(textfield.getInputLocator()).hasValue("");
        textfield.setValue("new value");
        assertThat(textfield.getInputLocator()).hasValue("new value");
    }

    @Test
    public void testInvalid() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Textfield");
        assertThat(textfield.getInputLocator()).hasValue("");
        textfield.setValue("new value");
        VaadinAssertions.assertValid(textfield);
        assertThat(textfield.getInputLocator()).hasValue("new value");
        textfield.setValue("");
        // field is required
        VaadinAssertions.assertInvalid(textfield);
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
        // Also verify through convenience methods
        assertEquals("Prefix", textfield.getPrefixText());
        assertEquals("Suffix", textfield.getSuffixText());
    }

    @Test
    public void testAllowedCharPattern() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        assertThat(textfield.getLocator()).isVisible();
        // Attributes presence
        assertEquals("[0-8]", textfield.getAllowedCharPattern());
        textfield.assertAllowedCharPattern("[0-8]");
    }

    @Test
    public void testPattern() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "Validated Textfield");
        assertThat(textfield.getInputLocator()).hasAttribute("pattern", "\\d{7}");
        textfield.assertPattern("\\d{7}");
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
        VaadinAssertions.assertInvalid(textfield);
        assertThat(textfield.getErrorMessageLocator()).hasText("Minimum length is 6 characters");

        // Test the pattern
        textfield.setValue("123845");
        VaadinAssertions.assertInvalid(textfield);
        assertThat(textfield.getErrorMessageLocator()).hasText("Invalid code format");
        // 9 is not valid
        textfield.setValue("991238456");

        textfield.setValue("1238456");
        VaadinAssertions.assertValid(textfield);
    }

    @Test
    public void testPlaceholderAndClearButton() {
        TextFieldElement textfield = TextFieldElement.getByLabel(page, "TextField with placeholder and clear button");
        assertThat(textfield.getLocator()).isVisible();
        assertThat(textfield.getLocator()).hasAttribute("placeholder", "Enter text here");
        assertEquals("Enter text here", textfield.getPlaceholder());
        assertThat(textfield.getClearButtonLocator()).not().isVisible();
        textfield.setValue("some value");
        assertThat(textfield.getClearButtonLocator()).isVisible();
        textfield.clickClearButton();
        assertThat(textfield.getInputLocator()).hasValue("");
    }

}
