package org.vaadin.addons.dramafinder.tests.it;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Extends the library's {@link SpringPlaywrightIT} with no annotation of its
 * own (issue #205): the inherited {@code @SpringBootTest(webEnvironment =
 * RANDOM_PORT)} starts the app and {@code getUrl()} comes from its port.
 */
public class SpringBaseClassIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "button";
    }

    @Test
    public void startsTheAppAndOpensTheView() {
        assertTrue(getUrl().matches("http://localhost:\\d+/"), getUrl());
        assertThat(page).hasURL(Pattern.compile("^http://localhost:\\d+/button$"));
        ButtonElement.getByText(page, "Enabled Button").assertVisible();
    }
}
