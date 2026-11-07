package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.vaadin.flow.router.RouteParameters;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.ArticleView;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.GreetingView;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.LegacyProfileView;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.ProfileView;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.TopLevelView;
import org.vaadin.addons.dramafinder.tests.testuis.navigation.UserEditView;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Navigation by route class and location assertions (issue #204). */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RouteNavigationIT extends SpringPlaywrightIT {

    private Locator text(String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    @Test
    public void navigatesToAPrefixedRoute() {
        navigate(ProfileView.class);
        assertPath("nav/profile");
        assertAt(ProfileView.class);
        assertThat(text("Profile")).isVisible();
    }

    @Test
    public void navigatesByNamingConvention() {
        navigate(GreetingView.class);
        assertPath("nav/greeting");
        assertThat(text("Greeting")).isVisible();
    }

    @Test
    public void absoluteRouteSkipsThePrefix() {
        navigate(TopLevelView.class);
        assertPath("top-level");
        assertThat(text("Top level")).isVisible();
    }

    @Test
    public void navigatesWithAUrlParameter() {
        navigate(ArticleView.class, "42");
        assertPath("nav/article/42");
        assertAt(ArticleView.class);
        assertThat(page.locator("#article")).hasText("Article 42");
    }

    @Test
    public void navigatesWithRouteParameters() {
        navigate(UserEditView.class, new RouteParameters("userId", "42"));
        assertPath("nav/user/42/edit");
        assertAt(UserEditView.class);
        assertThat(page.locator("#user")).hasText("Editing 42");
    }

    @Test
    public void assertAtFollowsAForward() {
        navigate(LegacyProfileView.class);
        assertAt(ProfileView.class);
    }

    @Test
    public void assertAtFailsOnAnotherView() {
        navigate(GreetingView.class);
        PlaywrightAssertions.setDefaultAssertionTimeout(1000);
        try {
            AssertionError e = assertThrows(AssertionError.class, () -> assertAt(ProfileView.class));
            assertTrue(e.getMessage().contains("nav/greeting"), e.getMessage());
        } finally {
            PlaywrightAssertions.setDefaultAssertionTimeout(5000);
        }
    }

    @Test
    public void assertsQueryParameters() {
        navigate(ProfileView.class);
        assertQueryParameter("tab", null);
        ButtonElement.getByText(page, "Open tab 2").click();
        assertAt(ProfileView.class);
        assertQueryParameter("tab", "2");
        assertQueryParameter("note", "a b");
    }
}
