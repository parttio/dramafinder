package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.TextAreaElement;
import org.vaadin.addons.dramafinder.tests.it.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TextAreaViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "text-area";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("TextArea Demo");
    }

    @Test
    public void testHasClass() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea");
        textArea.assertCssClass("custom-text-field");
        assertThat(textArea.getLocator()).hasClass("custom-text-field");
    }

    @Test
    public void testHasHelperText() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea");
        textArea.assertVisible();
        textArea.assertCssClass("custom-text-field");
        assertThat(textArea.getLocator()).hasClass("custom-text-field");
        assertThat(textArea.getHelperLocator()).hasText("Helper text");
        textArea.assertHelperHasText("Helper text");
        assertEquals("Helper text", textArea.getHelperText());
    }

    @Test
    public void testHasHelperComponent() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea with helper component");
        textArea.assertVisible();
        textArea.assertCssClass("custom-text-field");
        TextAreaElement helperComponent = new TextAreaElement(textArea.getHelperLocator());
        helperComponent.assertCssClass("custom-helper-component");
        helperComponent.assertVisible();
        assertThat(helperComponent.getHelperLocator()).hasText("Internal helper");
        helperComponent.assertHelperHasText("Internal helper");
    }

    @Test
    public void testValue() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea");
        textArea.assertValue("");
        textArea.setValue("new value");
        textArea.assertValue("new value");
    }

    @Test
    public void testInvalid() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea");
        textArea.assertValue("");
        textArea.setValue("new value");
        textArea.assertValid();
        textArea.assertValue("new value");
        textArea.setValue("");
        // field is required
        textArea.assertInvalid();
        assertThat(textArea.getErrorMessageLocator()).hasText("Field is required");
    }

    @Test
    public void testPrefixAndSuffix() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea");
        // Locators for prefix/suffix should exist and be visible
        assertThat(textArea.getPrefixLocator()).isVisible();
        assertThat(textArea.getSuffixLocator()).isVisible();
        assertThat(textArea.getPrefixLocator()).hasText("Prefix");
        assertThat(textArea.getSuffixLocator()).hasText("Suffix");
        textArea.assertPrefixHasText("Prefix");
        textArea.assertSuffixHasText("Suffix");
        // Also verify through convenience methods
        assertEquals("Prefix", textArea.getPrefixText());
        assertEquals("Suffix", textArea.getSuffixText());
    }


    @Test
    public void testNoPrefixAndSuffix() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea with helper component");
        textArea.assertPrefixHasText(null);
        textArea.assertSuffixHasText(null);
    }

    @Test
    public void testAllowedCharPattern() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Validated TextArea");
        textArea.assertVisible();
        // Attributes presence
        assertEquals("[0-8]", textArea.getAllowedCharPattern());
        textArea.assertAllowedCharPattern("[0-8]");
    }

    @Test
    public void testPattern() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Validated TextArea");
        assertThat(textArea.getInputLocator()).hasAttribute("pattern", "\\d{7}");
        textArea.assertPattern("\\d{7}");
        assertEquals("\\d{7}", textArea.getPattern());
    }

    @Test
    public void testMinLength() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Validated TextArea");
        assertThat(textArea.getInputLocator()).hasAttribute("minlength", "6");
        textArea.assertMinLength(6);
        assertEquals(6, textArea.getMinLength());
    }

    @Test
    public void testMaxLength() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Validated TextArea");
        assertThat(textArea.getInputLocator()).hasAttribute("maxlength", "7");
        textArea.assertMaxLength(7);
        assertEquals(7, textArea.getMaxLength());
    }

    @Test
    public void testValidatedFieldBehavior() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Validated TextArea");
        // Invalid value should mark field invalid
        textArea.setValue("1");
        textArea.assertInvalid();
        textArea.assertErrorMessage("Minimum length is 6 characters");

        // Test the pattern
        textArea.setValue("123845");
        textArea.assertInvalid();
        assertThat(textArea.getErrorMessageLocator()).hasText("Invalid code format");
        // 9 is not valid
        textArea.setValue("991238456");

        textArea.setValue("1238456");
        textArea.assertValid();
    }

    @Test
    public void testPlaceholder() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea with placeholder and clear button");
        textArea.assertVisible();
        textArea.assertPlaceholder("Enter text here");
        assertEquals("Enter text here", textArea.getPlaceholder());
    }

    @Test
    public void testClearButton() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea with placeholder and clear button");
        textArea.assertVisible();
        textArea.assertClearButtonNotVisible();
        textArea.setValue("some value");
        textArea.assertValue("some value");
        textArea.assertClearButtonVisible();
        textArea.clickClearButton();
        textArea.assertValue("");
    }

    @Test
    public void testClear() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea with placeholder and clear button");
        textArea.assertVisible();
        textArea.assertClearButtonNotVisible();
        textArea.setValue("some value");
        textArea.assertValue("some value");
        textArea.assertClearButtonVisible();
        textArea.clear();
        textArea.assertValue("");
    }

    @Test
    public void testTheme() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea with theme");
        textArea.assertVisible();
        textArea.assertTheme("small");
    }

    @Test
    public void testFocused() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "TextArea");
        TextAreaElement textAreaWithHelperComponent = TextAreaElement.getByLabel(page, "TextArea with helper component");

        assertEquals("", textArea.getLocator().getAttribute("focused"));
        assertNull(textAreaWithHelperComponent.getLocator().getAttribute("focused"));
        textArea.assertIsFocused();
        textAreaWithHelperComponent.assertIsNotFocused();
        textAreaWithHelperComponent.focus();
        textAreaWithHelperComponent.assertIsFocused();
    }

    @Test
    public void testAriaLabel() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Invisible label");
        textArea.assertVisible();
        textArea.assertAriaLabel("Invisible label");
    }

    @Test
    public void testAriaLabelledBy() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Labelled by");
        textArea.assertVisible();
        textArea.assertAriaLabel(null);
    }

    @Test
    public void testEnabledDisabled() {
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Enabled/Disabled Field");
        textArea.assertDisabled();

        page.locator("#enable-disable-button").click();

        textArea.assertEnabled();
    }

    @Test
    public void testLabel() {
        TextAreaElement textAreaWithLabel = TextAreaElement.getByLabel(page, "TextArea");
        textAreaWithLabel.assertVisible();
        textAreaWithLabel.assertLabel("TextArea");
        TextAreaElement textArea = TextAreaElement.getByLabel(page, "Invisible label");
        textArea.assertVisible();
        textArea.assertLabel(null);
    }

    @Test
    public void testTooltip() {
        TextAreaElement textAreaWithLabel = TextAreaElement.getByLabel(page, "TextArea");
        textAreaWithLabel.assertVisible();
        textAreaWithLabel.assertTooltipHasText("Tooltip for textArea");
    }
}
