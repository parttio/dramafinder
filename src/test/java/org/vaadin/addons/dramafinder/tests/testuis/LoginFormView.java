package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("LoginForm Demo")
@Route(value = "login-form", layout = MainLayout.class)
public class LoginFormView extends Main {

    public static final String VALID_USERNAME = "admin";
    public static final String VALID_PASSWORD = "secret";

    public LoginFormView() {
        createBasicExample();
        createCustomI18nExample();
    }

    private void createBasicExample() {
        Span loginOutput = new Span();
        loginOutput.setId("login-output");

        LoginForm loginForm = new LoginForm();
        loginForm.setId("basic-login-form");
        loginForm.addLoginListener(event -> {
            if (VALID_USERNAME.equals(event.getUsername())
                    && VALID_PASSWORD.equals(event.getPassword())) {
                loginOutput.setText("Welcome " + event.getUsername());
            } else {
                loginForm.setError(true);
            }
        });
        loginForm.addForgotPasswordListener(
                event -> loginOutput.setText("Forgot password"));

        Button disableButton = new Button("Disable login",
                event -> loginForm.setEnabled(false));
        disableButton.setId("disable-login");

        addExample("Basic Example", wrap("basic-login-wrapper", loginForm),
                disableButton, loginOutput);
    }

    private void createCustomI18nExample() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Connexion");
        i18n.getForm().setUsername("Identifiant");
        i18n.getForm().setPassword("Mot de passe");
        i18n.getForm().setSubmit("Se connecter");
        i18n.getErrorMessage().setTitle("Identifiants incorrects");
        i18n.getErrorMessage().setMessage("Vérifiez vos identifiants et réessayez.");
        i18n.setAdditionalInformation("Contactez le support si besoin.");

        LoginForm loginForm = new LoginForm(i18n);
        loginForm.setId("i18n-login-form");
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(event -> loginForm.setError(true));

        addExample("Custom I18n Example", wrap("i18n-login-wrapper", loginForm));
    }

    private Div wrap(String id, Component component) {
        Div wrapper = new Div(component);
        wrapper.setId(id);
        return wrapper;
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        add(components);
    }
}
