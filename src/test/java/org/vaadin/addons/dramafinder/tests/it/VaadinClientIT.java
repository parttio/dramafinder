package org.vaadin.addons.dramafinder.tests.it;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.VaadinClient;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The UI id of the page, as the server knows it (issue #280). */
public class VaadinClientIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "generic-element";
    }

    @Test
    public void uiIdIsTheServerSideUiId() {
        assertThat(page.locator("#ui-id")).hasText(java.util.regex.Pattern.compile("^\\d+$"));
        int serverId = Integer.parseInt(page.locator("#ui-id").textContent());
        assertEquals(Optional.of(serverId), VaadinClient.uiId(page));
    }

    @Test
    public void uiIdIsEmptyWithoutAFlowClient() {
        page.navigate("about:blank");
        assertTrue(VaadinClient.uiId(page).isEmpty());
    }
}
