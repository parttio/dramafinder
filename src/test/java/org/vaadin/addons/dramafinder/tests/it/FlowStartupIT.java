package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * After a page load, Flow's constructor registers a {@code TypeScript} client
 * that is idle until {@code flowInit} runs and registers the UI's own client
 * (issue #305). These pages fake that window of {@code Vaadin.Flow.clients}
 * and read the page once, without retrying, after
 * {@link AutoWait#waitForVaadinIdle}.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class FlowStartupIT extends SpringPlaywrightIT {

    // Flow has started but not registered its UI's client yet.
    private static final String FLOW_STARTING = "<script>"
            + "window.Vaadin = { Flow: { clients: { TypeScript: { isActive: () => false } } } };"
            + "</script>";

    @Override
    public String getView() {
        return "auto-wait";
    }

    private Object clientIds() {
        return page.evaluate("() => Object.keys(window.Vaadin.Flow.clients).join(',')");
    }

    @Test
    public void waitsUntilFlowHasRegisteredItsUiClient() {
        page.setContent(FLOW_STARTING + "<script>"
                + "setTimeout(() => window.Vaadin.Flow.clients.ROOT = { isActive: () => false }, 500);"
                + "</script>");

        AutoWait.waitForVaadinIdle(page);

        assertEquals("TypeScript,ROOT", clientIds());
    }

    @Test
    public void givesUpWaitingForAUiClientThatNeverComes() {
        // Flow runs but shows no Flow UI, as on a Hilla view. Without the cap
        // the wait would last until the page's 15 s default timeout.
        page.setContent(FLOW_STARTING);

        AutoWait.waitForVaadinIdle(page);

        assertEquals("TypeScript", clientIds());
    }
}
