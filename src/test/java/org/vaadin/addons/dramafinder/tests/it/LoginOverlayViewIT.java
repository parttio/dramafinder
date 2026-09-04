package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.LoginOverlayElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginOverlayViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "login-overlay";
    }

    private LoginOverlayElement openLoginOverlay() {
        ButtonElement.getByText(page, "Open login overlay").click();
        return LoginOverlayElement.get(page);
    }

    private LoginOverlayElement openI18nLoginOverlay() {
        ButtonElement.getByText(page, "Open i18n login overlay").click();
        return LoginOverlayElement.getByTitle(page, "DramaFinder FR");
    }

    private Locator getLoginOutput() {
        return page.locator("#login-output");
    }

    @Test
    public void testLoginOverlay() {
        LoginOverlayElement loginOverlay = new LoginOverlayElement(page);
        loginOverlay.assertHidden();

        ButtonElement.getByText(page, "Open login overlay").click();

        loginOverlay.assertOpen();
        loginOverlay.assertVisible();
        assertTrue(loginOverlay.isOpen());
        loginOverlay.assertHeaderTitle("DramaFinder");
        loginOverlay.assertDescription("Log in to find your drama");
        loginOverlay.assertTitle("Log in");
        loginOverlay.assertNoError();

        loginOverlay.getUsernameField().assertLabel("Username");
        loginOverlay.getPasswordField().assertLabel("Password");
        assertThat(loginOverlay.getSubmitButton().getLocator()).hasText("Log in");
    }

    @Test
    public void testLogin() {
        LoginOverlayElement loginOverlay = openLoginOverlay();
        loginOverlay.login("admin", "secret");

        loginOverlay.assertClosed();
        assertThat(getLoginOutput()).hasText("Welcome admin");
    }

    @Test
    public void testLoginWithWrongPasswordShowsError() {
        LoginOverlayElement loginOverlay = openLoginOverlay();
        loginOverlay.login("admin", "wrong");

        loginOverlay.assertOpen();
        loginOverlay.assertErrorVisible();
        loginOverlay.assertErrorTitle("Incorrect username or password");
        loginOverlay.assertErrorMessage(
                "Check that you have entered the correct username and password and try again.");
    }

    @Test
    public void testForgotPassword() {
        LoginOverlayElement loginOverlay = openLoginOverlay();
        loginOverlay.getForgotPasswordButton().click();

        loginOverlay.assertClosed();
        assertThat(getLoginOutput()).hasText("Forgot password");
    }

    @Test
    public void testCustomI18n() {
        LoginOverlayElement loginOverlay = openI18nLoginOverlay();

        loginOverlay.assertOpen();
        loginOverlay.assertHeaderTitle("DramaFinder FR");
        loginOverlay.assertDescription("Connectez-vous pour trouver votre drama");
        loginOverlay.assertTitle("Connexion");
        loginOverlay.getUsernameField().assertLabel("Identifiant");
        loginOverlay.getPasswordField().assertLabel("Mot de passe");
        assertThat(loginOverlay.getSubmitButton().getLocator()).hasText("Se connecter");
        assertThat(loginOverlay.getForgotPasswordButton().getLocator()).isHidden();
        loginOverlay.assertAdditionalInformation("Contactez le support si besoin.");

        loginOverlay.login("admin", "secret");

        loginOverlay.assertErrorVisible();
        loginOverlay.assertErrorTitle("Identifiants incorrects");
        loginOverlay.assertErrorMessage("Vérifiez vos identifiants et réessayez.");
    }
}
