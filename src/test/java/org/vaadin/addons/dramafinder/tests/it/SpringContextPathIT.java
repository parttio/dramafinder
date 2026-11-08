package org.vaadin.addons.dramafinder.tests.it;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code getUrl()} includes the servlet context path (issue #205), and so does
 * the test sign-in (issue #216).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.context-path=/ctx")
public class SpringContextPathIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "button";
    }

    @Test
    public void baseUrlIncludesTheContextPath() {
        assertTrue(getUrl().matches("http://localhost:\\d+/ctx/"), getUrl());
        assertThat(page).hasURL(Pattern.compile("^http://localhost:\\d+/ctx/button$"));
        ButtonElement.getByText(page, "Enabled Button").assertVisible();
    }

    @Test
    @WithMockUser("alice")
    public void signsInUnderTheContextPath() {
        page.navigate(getUrl() + "security/current-user");
        assertThat(page.getByText("Signed in as alice")).isVisible();
    }
}
