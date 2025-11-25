package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.tests.it.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ButtonViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "button";
    }

    @Test
    public void testEnabledButton() {
        ButtonElement button = ButtonElement.getByText(page, "Enabled Button");
        button.assertEnabled();
    }

    @Test
    public void testHasClass() {
        ButtonElement button = ButtonElement.getByText(page, "Enabled Button");
        button.assertCssClass("custom-button");
        assertThat(button.getLocator()).hasClass("custom-button");
    }

    @Test
    public void testHasTheme() {
        ButtonElement button = ButtonElement.getByText(page, "Enabled Button");
        button.assertTheme("success");
    }

    @Test
    public void testDisabledButton() {
        ButtonElement button = ButtonElement.getByText(page, "Disabled Button");
        button.assertDisabled();
    }

    @Test
    public void testToggleButton() {
        ButtonElement button = ButtonElement.getByText(page, "Toggle Button");
        button.assertEnabled();
        button.click();
        button.assertDisabled();
    }

    @Test
    public void testClickableButton() {
        ButtonElement button = ButtonElement.getByText(page, "Click me");
        button.click();
        assertThat(page.getByText("Button clicked!")).isVisible();
    }

    @Test
    public void testAriaLabel() {
        ButtonElement iconButton = ButtonElement.getByText(page, "Icon Button");
        iconButton.assertAriaLabel("Icon Button");
    }

    @Test
    public void testPrefixAndSuffix() {
        ButtonElement iconButton = ButtonElement.getByText(page, "Icon Button");
        // Locators for prefix/suffix should exist and be visible
        assertThat(iconButton.getPrefixLocator()).isVisible();
        assertThat(iconButton.getSuffixLocator()).isVisible();
        assertThat(iconButton.getPrefixLocator()).hasText("Prefix");
        assertThat(iconButton.getSuffixLocator()).hasText("Suffix");
        iconButton.assertPrefixHasText("Prefix");
        iconButton.assertSuffixHasText("Suffix");
        // Also verify through convenience methods
        assertEquals("Prefix", iconButton.getPrefixText());
        assertEquals("Suffix", iconButton.getSuffixText());
    }

    @Test
    public void testFocused() {
        ButtonElement focusedButton = ButtonElement.getByText(page, "Enabled Button");
        ButtonElement nextButton = ButtonElement.getByText(page, "Toggle Button");

        assertEquals("", focusedButton.getLocator().getAttribute("focused"));
        assertNull(nextButton.getLocator().getAttribute("focused"));
        focusedButton.assertIsFocused();
        nextButton.assertIsNotFocused();
        nextButton.focus();
        nextButton.assertIsFocused();
    }

    @Test
    public void testTooltip() {
        ButtonElement tooltipButton = ButtonElement.getByText(page, "Tooltip Button");
        tooltipButton.assertTooltipHasText("Tooltip Text");
    }
}
