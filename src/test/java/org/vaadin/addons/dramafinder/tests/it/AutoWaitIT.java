package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Each action below reaches a server listener that sleeps before updating
 * {@code #result}. The result is read with a plain, non-retrying getter right
 * after the action, so these tests pass only when the action itself waited for
 * the round-trip (issue #202).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AutoWaitIT extends SpringPlaywrightIT {

    private static final int NAVIGATION_DELAY_MS = 500;

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

    @Test
    public void clickWaitsForAServerSideNavigationToUpdateTheUrl() {
        slowDownClientSideNavigation();
        ButtonElement.getByText(page, "Navigate button").click();
        assertTrue(page.url().endsWith("/nav/greeting"), page.url());
    }

    @Test
    public void explicitWaitWaitsForAServerSideNavigationToUpdateTheUrl() {
        slowDownClientSideNavigation();
        ButtonElement button = ButtonElement.getByText(page, "Navigate button");
        AutoWait.withoutWaiting(button::click);
        AutoWait.waitForVaadinIdle(page);
        assertTrue(page.url().endsWith("/nav/greeting"), page.url());
    }

    /**
     * Flow hands a server-side navigation to React Router behind
     * {@code setTimeout(0)} calls and changes the address bar only once React
     * Router has run it (issue #304). Holding every zero-delay timer except the
     * Flow client's own for {@link #NAVIGATION_DELAY_MS} after the
     * {@code vaadin-navigate} event keeps the client going idle as usual but
     * turns the few-millisecond gap, in which the URL is still the old one,
     * into one no wait can win by chance.
     */
    private void slowDownClientSideNavigation() {
        page.addInitScript("""
                (() => {
                  const setTimeout = window.setTimeout;
                  let holdUntil = 0;
                  window.addEventListener('vaadin-navigate', () => holdUntil = Date.now() + %d);
                  window.setTimeout = (handler, timeout, ...args) => {
                    const held = holdUntil - Date.now();
                    const flowClient = new Error().stack.includes('/FlowClient-');
                    return setTimeout(handler,
                        !timeout && held > 0 && !flowClient ? held : timeout, ...args);
                  };
                })();
                """.formatted(NAVIGATION_DELAY_MS));
        page.reload();
        AutoWait.waitForVaadinIdle(page);
    }
}
