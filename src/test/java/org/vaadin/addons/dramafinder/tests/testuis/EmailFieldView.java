package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("EmailField Demo")
@Route(value = "email", layout = MainLayout.class)
public class EmailFieldView extends Main {

    public EmailFieldView() {
        createBasicExample();
        createValidationExample();
    }

    private void createBasicExample() {
        EmailField emailField = new EmailField("Email");
        emailField.setTooltipText("Tooltip for email field");
        emailField.setPlaceholder("your@email.com");
        emailField.setClearButtonVisible(true);
        emailField.setPrefixComponent(new Span("@"));
        addExample("Basic Example", emailField);
    }

    private void createValidationExample() {
        EmailField emailField = new EmailField("Validated Email");
        emailField.setHelperText("This field is required.");
        emailField.setRequired(true);
        emailField.setErrorMessage("Please enter a valid email address");
        addExample("Validation Example", emailField);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
