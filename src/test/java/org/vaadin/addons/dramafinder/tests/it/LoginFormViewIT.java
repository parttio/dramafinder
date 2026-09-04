package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.LoginFormElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginFormViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "login-form";
    }

    private LoginFormElement getBasicLoginForm() {
        return LoginFormElement.get(page.locator("#basic-login-wrapper"));
    }

    private LoginFormElement getI18nLoginForm() {
        return LoginFormElement.get(page.locator("#i18n-login-wrapper"));
    }

    private Locator getLoginOutput() {
        return page.locator("#login-output");
    }

    @Test
    public void testLoginForm() {
        LoginFormElement loginForm = getBasicLoginForm();
        loginForm.assertVisible();
        loginForm.assertTitle("Log in");
        loginForm.assertNoError();
        assertFalse(loginForm.isErrorVisible());

        loginForm.getUsernameField().assertLabel("Username");
        loginForm.getPasswordField().assertLabel("Password");
        assertThat(loginForm.getSubmitButton().getLocator()).hasText("Log in");
        assertThat(loginForm.getForgotPasswordButton().getLocator())
                .hasText("Forgot password");
    }

    @Test
    public void testLogin() {
        LoginFormElement loginForm = getBasicLoginForm();
        loginForm.login("admin", "secret");

        assertThat(getLoginOutput()).hasText("Welcome admin");
        loginForm.assertNoError();
    }

    @Test
    public void testLoginWithWrongPasswordShowsError() {
        LoginFormElement loginForm = getBasicLoginForm();
        loginForm.login("admin", "wrong");

        loginForm.assertErrorVisible();
        loginForm.assertErrorTitle("Incorrect username or password");
        loginForm.assertErrorMessage(
                "Check that you have entered the correct username and password and try again.");
        assertTrue(loginForm.isErrorVisible());
        assertThat(getLoginOutput()).isEmpty();
    }

    @Test
    public void testFieldValues() {
        LoginFormElement loginForm = getBasicLoginForm();
        loginForm.getUsernameField().setValue("admin");
        loginForm.getPasswordField().setValue("secret");

        loginForm.getUsernameField().assertValue("admin");
        loginForm.getPasswordField().assertValue("secret");
    }

    @Test
    public void testForgotPassword() {
        LoginFormElement loginForm = getBasicLoginForm();
        loginForm.getForgotPasswordButton().click();

        assertThat(getLoginOutput()).hasText("Forgot password");
    }

    @Test
    public void testDisabled() {
        LoginFormElement loginForm = getBasicLoginForm();
        loginForm.assertEnabled();

        ButtonElement.getByText(page, "Disable login").click();

        loginForm.assertDisabled();
        assertTrue(loginForm.isDisabled());
    }

    @Test
    public void testCustomI18n() {
        LoginFormElement loginForm = getI18nLoginForm();
        loginForm.assertTitle("Connexion");
        loginForm.getUsernameField().assertLabel("Identifiant");
        loginForm.getPasswordField().assertLabel("Mot de passe");
        assertThat(loginForm.getSubmitButton().getLocator()).hasText("Se connecter");
        assertThat(loginForm.getForgotPasswordButton().getLocator()).isHidden();
        loginForm.assertAdditionalInformation("Contactez le support si besoin.");

        loginForm.login("admin", "secret");

        loginForm.assertErrorVisible();
        loginForm.assertErrorTitle("Identifiants incorrects");
        loginForm.assertErrorMessage("Vérifiez vos identifiants et réessayez.");
    }
}
