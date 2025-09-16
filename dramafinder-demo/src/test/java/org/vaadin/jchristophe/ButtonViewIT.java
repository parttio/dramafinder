package org.vaadin.jchristophe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.dramafinder.HasTestView;
import org.vaadin.dramafinder.element.ButtonElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ButtonViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "button";
    }

    @Test
    public void testEnabledButton() {
        ButtonElement button = ButtonElement.getByLabel(page, "Enabled Button");
        button.assertEnabled();
    }

    @Test
    public void testHasClass() {
        ButtonElement button = ButtonElement.getByLabel(page, "Enabled Button");
        button.assertCssClass("custom-button");
        assertThat(button.getLocator()).hasClass("custom-button");
    }

    @Test
    public void testHasTheme() {
        ButtonElement button = ButtonElement.getByLabel(page, "Enabled Button");
        button.assertTheme("success");
    }

    @Test
    public void testDisabledButton() {
        ButtonElement button = ButtonElement.getByLabel(page, "Disabled Button");
        button.assertDisabled();
    }

    @Test
    public void testToggleButton() {
        ButtonElement button = ButtonElement.getByLabel(page, "Toggle Button");
        button.assertEnabled();
        button.click();
        button.assertDisabled();
    }

    @Test
    public void testClickableButton() {
        ButtonElement button = ButtonElement.getByLabel(page, "Click me");
        button.click();
        assertThat(page.getByText("Button clicked!")).isVisible();
    }
}
