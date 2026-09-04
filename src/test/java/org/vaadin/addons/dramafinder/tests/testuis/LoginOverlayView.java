package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("LoginOverlay Demo")
@Route(value = "login-overlay", layout = MainLayout.class)
public class LoginOverlayView extends Main {

    public static final String VALID_USERNAME = "admin";
    public static final String VALID_PASSWORD = "secret";

    public LoginOverlayView() {
        createBasicExample();
        createCustomI18nExample();
    }

    private void createBasicExample() {
        Span loginOutput = new Span();
        loginOutput.setId("login-output");

        LoginOverlay loginOverlay = new LoginOverlay();
        loginOverlay.setTitle("DramaFinder");
        loginOverlay.setDescription("Log in to find your drama");
        loginOverlay.addLoginListener(event -> {
            if (VALID_USERNAME.equals(event.getUsername())
                    && VALID_PASSWORD.equals(event.getPassword())) {
                loginOverlay.close();
                loginOutput.setText("Welcome " + event.getUsername());
            } else {
                loginOverlay.setError(true);
            }
        });
        loginOverlay.addForgotPasswordListener(event -> {
            loginOverlay.close();
            loginOutput.setText("Forgot password");
        });

        Button openButton = new Button("Open login overlay",
                event -> loginOverlay.setOpened(true));
        openButton.setId("open-login-overlay");

        addExample("Basic Example", openButton, loginOutput);
    }

    private void createCustomI18nExample() {
        LoginI18n i18n = LoginI18n.createDefault();
        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle("DramaFinder FR");
        header.setDescription("Connectez-vous pour trouver votre drama");
        i18n.setHeader(header);
        i18n.getForm().setTitle("Connexion");
        i18n.getForm().setUsername("Identifiant");
        i18n.getForm().setPassword("Mot de passe");
        i18n.getForm().setSubmit("Se connecter");
        i18n.getErrorMessage().setTitle("Identifiants incorrects");
        i18n.getErrorMessage().setMessage("Vérifiez vos identifiants et réessayez.");
        i18n.setAdditionalInformation("Contactez le support si besoin.");

        LoginOverlay loginOverlay = new LoginOverlay(i18n);
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.addLoginListener(event -> loginOverlay.setError(true));

        Button openButton = new Button("Open i18n login overlay",
                event -> loginOverlay.setOpened(true));
        openButton.setId("open-i18n-login-overlay");

        addExample("Custom I18n Example", openButton);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        add(components);
    }
}
