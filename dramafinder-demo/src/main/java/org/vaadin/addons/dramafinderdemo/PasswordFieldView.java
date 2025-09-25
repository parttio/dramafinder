package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("PasswordField Demo")
@Route(value = "password-field", layout = MainLayout.class)
public class PasswordFieldView extends Main {

    public PasswordFieldView() {
        createBasicExample();
        createPasswordFieldWithHelperExample();
        createPasswordFieldWithPlaceholderAndClearButton();
        createValidationPropertiesExample();
        createPasswordFieldWithThemeExample();
        createPasswordFieldWithAriaLabel();
        createPasswordFieldWithAriaLabelledBy();
        createEnabledDisabledExample();
    }

    private void createPasswordFieldWithThemeExample() {
        PasswordField passwordField = new PasswordField("PasswordField with theme");
        passwordField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        addExample("Theme Example", passwordField);
    }

    private void createPasswordFieldWithPlaceholderAndClearButton() {
        PasswordField passwordField = new PasswordField("PasswordField with placeholder and clear button");
        passwordField.setPlaceholder("Enter text here");
        passwordField.setClearButtonVisible(true);
        addExample("Placeholder and Clear Button Example", passwordField);
    }

    private void createBasicExample() {
        PasswordField passwordField = new PasswordField("PasswordField");
        passwordField.setTooltipText("Tooltip for passwordField");
        passwordField.focus();
        passwordField.addClassName("custom-text-field");
        passwordField.setRequired(true);
        // Add prefix/suffix so tests can assert them
        passwordField.setPrefixComponent(new Span("Prefix"));
        passwordField.setSuffixComponent(new Span("Suffix"));

        passwordField.setI18n(new PasswordField.PasswordFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setMinLengthErrorMessage("Minimum length is 5 characters")
                .setMaxLengthErrorMessage("Maximum length is 18 characters")
                .setPatternErrorMessage("Invalid phone number format"));
        passwordField.setHelperText("Helper text");
        // Add prefix and suffix content to demonstrate slot usage
        passwordField.setPrefixComponent(new Span("Prefix"));
        passwordField.setSuffixComponent(new Span("Suffix"));
        addExample("Basic Example", passwordField);
    }

    private void createPasswordFieldWithHelperExample() {
        PasswordField passwordField = new PasswordField("PasswordField with helper component");
        passwordField.addClassName("custom-text-field");
        passwordField.setRequired(true);
        PasswordField helperComponent = new PasswordField("Helper field");
        helperComponent.addClassName("custom-helper-component");
        helperComponent.setHelperText("Internal helper");
        passwordField.setHelperComponent(helperComponent);
        addExample("Helper Component Example", passwordField);
    }

    private void createValidationPropertiesExample() {
        PasswordField validated = new PasswordField("Validated PasswordField");
        validated.setHelperText("This field accepts only 7 numbers 0 to 8. 9 is forbidden");
        validated.addClassName("validated-text-field");
        validated.setRequired(true);
        // Allowed characters are digits only
        validated.setAllowedCharPattern("[0-8]");
        // Expect exactly 7 digits
        validated.setPattern("\\d{7}");
        validated.setMinLength(6);
        validated.setMaxLength(7);

        validated.setI18n(new PasswordField.PasswordFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setMinLengthErrorMessage("Minimum length is 6 characters")
                .setMaxLengthErrorMessage("Maximum length is 7 characters")
                .setPatternErrorMessage("Invalid code format"));

        addExample("Validation Properties Example", validated);
    }

    private void createPasswordFieldWithAriaLabel() {
        PasswordField passwordField = new PasswordField();
        passwordField.setAriaLabel("Invisible label");
        addExample("Aria label Example", passwordField);
    }

    private void createPasswordFieldWithAriaLabelledBy() {
        PasswordField passwordField = new PasswordField();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        passwordField.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, passwordField));
    }

    private void createEnabledDisabledExample() {
        PasswordField passwordField = new PasswordField("Enabled/Disabled Field");
        passwordField.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> passwordField.setEnabled(!passwordField.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(passwordField, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }

}
