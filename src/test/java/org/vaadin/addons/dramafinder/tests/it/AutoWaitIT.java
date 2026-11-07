package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Each action below reaches a server listener that sleeps before updating
 * {@code #result}. The result is read with a plain, non-retrying getter right
 * after the action, so these tests pass only when the action itself waited for
 * the round-trip (issue #202).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AutoWaitIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "auto-wait";
    }

    private String result() {
        return page.locator("#result").textContent();
    }

    @Test
    public void clickWaitsForTheServer() {
        ButtonElement.getByText(page, "Slow button").click();
        assertEquals("clicked", result());
    }

    @Test
    public void setValueWaitsForTheServer() {
        TextFieldElement.getByLabel(page, "Slow field").setValue("Ada");
        assertEquals("field: Ada", result());
    }

    @Test
    public void checkWaitsForTheServer() {
        CheckboxElement.getByLabel(page, "Slow checkbox").check();
        assertEquals("checkbox: true", result());
    }

    @Test
    public void selectItemWaitsForTheServer() {
        ComboBoxElement.getByLabel(page, "Slow combo").selectItem("Banana");
        assertEquals("combo: Banana", result());
    }

    @Test
    public void withoutWaitingSeesTheInFlightState() {
        ButtonElement button = ButtonElement.getByText(page, "Slow button");
        AutoWait.withoutWaiting(button::click);
        assertEquals("initial", result());
        assertThat(page.locator("#result")).hasText("clicked");
    }

    @Test
    public void systemPropertyTurnsWaitingOff() {
        ButtonElement button = ButtonElement.getByText(page, "Slow button");
        System.setProperty(AutoWait.PROPERTY, "false");
        try {
            button.click();
            assertEquals("initial", result());
        } finally {
            System.clearProperty(AutoWait.PROPERTY);
        }
        assertThat(page.locator("#result")).hasText("clicked");
    }

    @Test
    public void explicitWaitWaitsEvenWhenAutoWaitIsOff() {
        ButtonElement button = ButtonElement.getByText(page, "Slow button");
        AutoWait.withoutWaiting(() -> {
            button.click();
            button.waitForVaadinIdle();
        });
        assertEquals("clicked", result());
    }
}
