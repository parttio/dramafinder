package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The page starts signed in as the Spring Security test user of the test
 * method (issue #216), and {@code loginAs} / {@code newPageAs} sign in others.
 */
public class SpringSecurityLoginIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "security/current-user";
    }

    private static Locator text(Page page, String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    @Test
    public void startsAnonymousWithoutAnAnnotation() {
        assertThat(text(page, "Not signed in")).isVisible();
    }

    @Test
    @WithMockUser(username = "alice", roles = "ADMIN")
    public void startsSignedInAsTheMockUser() {
        assertThat(text(page, "Signed in as alice")).isVisible();
        assertThat(text(page, "Authorities: ROLE_ADMIN")).isVisible();
    }

    @Test
    @WithMockUser
    public void defaultMockUserIsUserWithRoleUser() {
        assertThat(text(page, "Signed in as user")).isVisible();
        assertThat(text(page, "Authorities: ROLE_USER")).isVisible();
    }

    @Test
    @WithMockUser(username = "dave", authorities = "SCOPE_read")
    public void authoritiesAreKeptAsGiven() {
        assertThat(text(page, "Signed in as dave")).isVisible();
        assertThat(text(page, "Authorities: SCOPE_read")).isVisible();
    }

    @Test
    @WithUserDetails("bob")
    public void startsSignedInAsTheUserDetailsUser() {
        assertThat(text(page, "Signed in as bob")).isVisible();
        assertThat(text(page, "Authorities: ROLE_AUDITOR,ROLE_USER")).isVisible();
    }

    @Test
    @WithAnonymousUser
    public void anonymousUserStaysSignedOut() {
        assertThat(text(page, "Not signed in")).isVisible();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void theSecurityFilterChainSeesTheUser() {
        Response response = page.navigate(getUrl() + "security/admin");
        assertEquals(200, response.status());
        assertThat(text(page, "Admin area")).isVisible();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void aUserWithoutTheRoleIsRefused() {
        Response response = page.navigate(getUrl() + "security/admin");
        assertEquals(403, response.status());
    }

    @Test
    public void loginAsSignsInThePageAndReloadsIt() {
        assertThat(text(page, "Not signed in")).isVisible();
        loginAs("carol", "ADMIN", "USER");
        assertThat(text(page, "Signed in as carol")).isVisible();
        assertThat(text(page, "Authorities: ROLE_ADMIN,ROLE_USER")).isVisible();
    }

    @Test
    @WithMockUser("alice")
    public void loginAsReplacesTheSignedInUser() {
        assertThat(text(page, "Signed in as alice")).isVisible();
        loginAs("erin");
        assertThat(text(page, "Signed in as erin")).isVisible();
        assertThat(text(page, "Authorities: ROLE_USER")).isVisible();
    }

    @Test
    @WithMockUser("alice")
    public void newPageAsOpensTheViewInItsOwnSession() {
        Page other = newPageAs("frank", "ADMIN");
        assertThat(text(other, "Signed in as frank")).isVisible();
        assertThat(text(other, "Authorities: ROLE_ADMIN")).isVisible();
        assertThat(text(page, "Signed in as alice")).isVisible();
        page.reload();
        assertThat(text(page, "Signed in as alice")).isVisible();
    }

    @Test
    public void loginAsSignsInAContextTheTestOpened() {
        try (BrowserContext context = getBrowser().newContext()) {
            loginAs(context, "grace", "AUDITOR");
            Page other = context.newPage();
            other.navigate(getUrl() + getView());
            assertThat(text(other, "Signed in as grace")).isVisible();
            assertThat(text(other, "Authorities: ROLE_AUDITOR")).isVisible();
        }
        assertThat(text(page, "Not signed in")).isVisible();
    }
}
