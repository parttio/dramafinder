package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.vaadin.addons.dramafinder.spring.SpringTestLogin;
import org.vaadin.addons.dramafinder.spring.TestLoginConfiguration;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Signing in from a test that manages its own Playwright browser context and
 * does not extend {@code SpringPlaywrightIT} (issue #236): the endpoint comes
 * from importing {@link TestLoginConfiguration}, the sign-in from
 * {@link SpringTestLogin}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestLoginConfiguration.class)
public class SpringTestLoginIT {

    private static Playwright playwright;
    private static Browser browser;

    @Autowired
    private ApplicationContext applicationContext;

    @LocalServerPort
    private int port;

    private BrowserContext context;

    @BeforeAll
    static void launch() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(!"false".equals(System.getProperty("headless"))));
    }

    @AfterAll
    static void close() {
        playwright.close();
    }

    @BeforeEach
    void openContext() {
        context = browser.newContext();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    /** Without the trailing slash, which the sign-in methods must accept. */
    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private Page openCurrentUser() {
        Page page = context.newPage();
        page.navigate(baseUrl() + "/security/current-user");
        return page;
    }

    private static int fetchStatus(Page page, String url) {
        return ((Number) page.evaluate("url => fetch(url).then(r => r.status)", url)).intValue();
    }

    private static Locator text(Page page, String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    @Test
    public void signsInByNameAndRoles() {
        SpringTestLogin.signIn(applicationContext, context, baseUrl(), "alice", "ADMIN");
        Page page = openCurrentUser();
        assertThat(text(page, "Signed in as alice")).isVisible();
        assertThat(text(page, "Authorities: ROLE_ADMIN")).isVisible();
    }

    @Test
    public void signsInAnAuthentication() {
        SpringTestLogin.signIn(applicationContext, context, baseUrl() + "/",
                UsernamePasswordAuthenticationToken.authenticated("dave", null,
                        List.of(new SimpleGrantedAuthority("SCOPE_read"))));
        Page page = openCurrentUser();
        assertThat(text(page, "Signed in as dave")).isVisible();
        assertThat(text(page, "Authorities: SCOPE_read")).isVisible();
    }

    @Test
    @WithMockUser(username = "erin", roles = "AUDITOR")
    public void signsInTheTestUser() {
        assertTrue(SpringTestLogin.signInTestUser(applicationContext, context, baseUrl()));
        Page page = openCurrentUser();
        assertThat(text(page, "Signed in as erin")).isVisible();
        assertThat(text(page, "Authorities: ROLE_AUDITOR")).isVisible();
    }

    @Test
    public void leavesTheContextSignedOutWithoutATestUser() {
        assertFalse(SpringTestLogin.signInTestUser(applicationContext, context, baseUrl()));
        assertThat(text(openCurrentUser(), "Not signed in")).isVisible();
    }

    @Test
    public void aSignInUrlSignsInTheBrowserThatOpensItOnce() {
        String url = SpringTestLogin.signInUrl(applicationContext, baseUrl(), "frank");
        Page page = openCurrentUser();
        assertThat(text(page, "Not signed in")).isVisible();
        // the browser itself requests it, as a browser DramaFinder does not
        // drive would; a navigation to a 204 is aborted by Chromium
        assertEquals(204, fetchStatus(page, url));
        page.reload();
        assertThat(text(page, "Signed in as frank")).isVisible();
        assertThat(text(page, "Authorities: ROLE_USER")).isVisible();

        assertEquals(404, fetchStatus(page, url));
    }
}
