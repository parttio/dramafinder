package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
        assertThat(textfield.getLocator()).not().hasAttribute("invalid", "");
        assertThat(textfield.getInputLocator()).hasValue("new value");
        textfield.setValue("");
        // field is required
        assertThat(textfield.getLocator()).hasAttribute("invalid", "");
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
