package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.ClientSideErrors;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClientSideErrorsIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "client-side-errors";
    }

    @Test
    public void noErrorsPasses() {
        ClientSideErrors errors = ClientSideErrors.track(page);
        ButtonElement.getByText(page, "Log info").click();
        errors.assertNone();
        assertEquals(List.of(), errors.getErrors());
    }

    @Test
    public void recordsConsoleErrors() {
        ClientSideErrors errors = ClientSideErrors.track(page);
        ButtonElement.getByText(page, "Log error").click();
        page.waitForCondition(() -> !errors.getErrors().isEmpty());
        assertEquals(List.of("logged failure"), errors.getErrors());
        AssertionError failure = assertThrows(AssertionError.class, errors::assertNone);
        assertTrue(failure.getMessage().contains("logged failure"), failure.getMessage());
    }

    @Test
    public void recordsUncaughtExceptions() {
        ClientSideErrors errors = ClientSideErrors.track(page);
        ButtonElement.getByText(page, "Throw error").click();
        page.waitForCondition(() -> !errors.getErrors().isEmpty());
        assertTrue(errors.getErrors().get(0).contains("uncaught failure"), errors.getErrors().toString());
    }

    @Test
    public void clearAndStop() {
        ClientSideErrors errors = ClientSideErrors.track(page);
        ButtonElement.getByText(page, "Log error").click();
        page.waitForCondition(() -> !errors.getErrors().isEmpty());
        errors.clear();
        errors.assertNone();

        errors.stop();
        ButtonElement.getByText(page, "Log error").click();
        errors.assertNone();
    }
}
