package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** A class-level {@code @WithMockUser} signs in every test; a method-level one wins (issue #216). */
@WithMockUser(username = "admin", roles = "ADMIN")
public class SpringSecurityClassLevelLoginIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "security/current-user";
    }

    @Test
    public void classLevelUserIsSignedIn() {
        assertThat(page.getByText("Signed in as admin", new Page.GetByTextOptions().setExact(true)))
                .isVisible();
    }

    @Test
    @WithMockUser("override")
    public void methodLevelUserWins() {
        assertThat(page.getByText("Signed in as override", new Page.GetByTextOptions().setExact(true)))
                .isVisible();
    }
}
