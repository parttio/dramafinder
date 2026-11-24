package org.vaadin.addons.dramafinder.tests.it;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
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
    public void explicitWaitWaitsEvenWhenAutoWaitIsOff() {
        ButtonElement button = ButtonElement.getByText(page, "Slow button");
        AutoWait.withoutWaiting(() -> {
            button.click();
            button.waitForVaadinIdle();
        });
        assertEquals("clicked", result());
    }

    @Test
    public void waitingWhenNothingIsPendingTakesOneBrowserCall() {
        List<String> calls = new ArrayList<>();
        AutoWait.waitForVaadinIdle(recordingCalls(page, calls));
        assertEquals(1, calls.size(), calls.toString());
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

    @Test
    public void clickWaitsForANavigationThatStartsJustAfterTheClientGoesIdle() {
        startServerSideNavigationsWhenTheClientGoesIdle();
        ButtonElement.getByText(page, "Navigate button").click();
        assertTrue(page.url().endsWith("/nav/greeting"), page.url());
    }

    @Test
    public void clickWaitsForAPageLoadTheServerStarted() {
        ButtonElement.getByText(page, "Reload to greeting").click();
        assertEquals(1, page.getByText("Greeting", new Page.GetByTextOptions().setExact(true)).count());
    }

    /**
     * A regression guard rather than a red-first test: Playwright's
     * {@code click()} already waits for a document load the click starts, so
     * this passed before issue #320 too. It pins that a page without Flow is
     * idle as soon as it has loaded.
     */
    @Test
    public void explicitWaitWaitsForALinkToLoadAPageWithoutFlow() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Plain link")).click();
        AutoWait.waitForVaadinIdle(page);
        assertEquals(1, page.getByText("Plain page content").count());
    }

    /**
     * Flow 25.3 starts handing a server-side navigation to React Router, and
     * sets {@code window.Vaadin.Flow.navigation}, while its client is still
     * busy with the response, so the client never looks idle before the
     * navigation is pending. This holds the {@code vaadin-navigate} event back
     * until a check first sees every client idle, then dispatches it in the
     * next task: the one-task gap in which the client is idle and no
     * navigation is pending yet, where a single check would stop waiting.
     */
    private void startServerSideNavigationsWhenTheClientGoesIdle() {
        page.addInitScript("""
                (() => {
                  const dispatchEvent = window.dispatchEvent.bind(window);
                  window.dispatchEvent = event => {
                    if (event.type !== 'vaadin-navigate' || event.late) {
                      return dispatchEvent(event);
                    }
                    const late = new CustomEvent(event.type, { detail: event.detail });
                    late.late = true;
                    const clients = Object.values(window.Vaadin.Flow.clients);
                    const isActive = clients.map(client => client.isActive.bind(client));
                    let held = true;
                    clients.forEach((client, i) => client.isActive = () => {
                      const active = isActive[i]();
                      if (held && !isActive.some(check => check())) {
                        held = false;
                        setTimeout(() => dispatchEvent(late));
                      }
                      return active;
                    });
                    return true;
                  };
                })();
                """);
        page.reload();
        AutoWait.waitForVaadinIdle(page);
    }

    /** The real page, recording the name of every method called on it. */
    private static Page recordingCalls(Page page, List<String> calls) {
        return (Page) Proxy.newProxyInstance(Page.class.getClassLoader(), new Class<?>[] {Page.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    try {
                        return method.invoke(page, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
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
