package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@code -Ddramafinder.autoWait=false} turns waiting off for the whole JVM.
 * <p>
 * Isolated from the other test classes, which run in parallel: while the
 * property is set, their actions would not wait either, and a test that reads
 * the state right after an action would fail (issue #315).
 */
@Isolated
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AutoWaitSystemPropertyIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "auto-wait";
    }

    @Test
    public void systemPropertyTurnsWaitingOff() {
        ButtonElement button = ButtonElement.getByText(page, "Slow button");
        System.setProperty(AutoWait.PROPERTY, "false");
        try {
            button.click();
            assertEquals("initial", page.locator("#result").textContent());
        } finally {
            System.clearProperty(AutoWait.PROPERTY);
        }
        assertThat(page.locator("#result")).hasText("clicked");
    }
}
