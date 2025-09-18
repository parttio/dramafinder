package org.vaadin.jchristophe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("TextField Demo")
@Route(value = "", layout = MainLayout.class)
public class TextFieldView extends Main {

    public TextFieldView() {
        createBasicExample();
        createTextFieldWithHelperExample();
        createTextFieldWithPlaceholderAndClearButton();
        createValidationPropertiesExample();
        createTextFieldWithThemeExample();
        createTextFieldWithAriaLabel();
        createTextFieldWithAriaLabelledBy();
        createEnabledDisabledExample();
    }

    private void createTextFieldWithThemeExample() {
        TextField textfield = new TextField("TextField with theme");
        textfield.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        addExample("Theme Example", textfield);
    }

    private void createTextFieldWithPlaceholderAndClearButton() {
        TextField textfield = new TextField("TextField with placeholder and clear button");
        textfield.setPlaceholder("Enter text here");
        textfield.setClearButtonVisible(true);
        addExample("Placeholder and Clear Button Example", textfield);
    }

    private void createBasicExample() {
        TextField textfield = new TextField("Textfield");
        textfield.setTooltipText("Tooltip for textfield");
        textfield.focus();
        textfield.addClassName("custom-text-field");
        textfield.setRequired(true);
        // Add prefix/suffix so tests can assert them
        textfield.setPrefixComponent(new Span("Prefix"));
        textfield.setSuffixComponent(new Span("Suffix"));

        textfield.setI18n(new TextField.TextFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setMinLengthErrorMessage("Minimum length is 5 characters")
                .setMaxLengthErrorMessage("Maximum length is 18 characters")
                .setPatternErrorMessage("Invalid phone number format"));
        textfield.setHelperText("Helper text");
        // Add prefix and suffix content to demonstrate slot usage
        textfield.setPrefixComponent(new Span("Prefix"));
        textfield.setSuffixComponent(new Span("Suffix"));
        addExample("Basic Example", textfield);
    }

    private void createTextFieldWithHelperExample() {
        TextField textfield = new TextField("TextField with helper component");
        textfield.addClassName("custom-text-field");
        textfield.setRequired(true);
        TextField helperComponent = new TextField("Helper field");
        helperComponent.addClassName("custom-helper-component");
        helperComponent.setHelperText("Internal helper");
        textfield.setHelperComponent(helperComponent);
        addExample("Helper Component Example", textfield);
    }

    private void createValidationPropertiesExample() {
        TextField validated = new TextField("Validated Textfield");
        validated.setHelperText("This field accepts only 7 numbers 0 to 8. 9 is forbidden");
        validated.addClassName("validated-text-field");
        validated.setRequired(true);
        // Allowed characters are digits only
        validated.setAllowedCharPattern("[0-8]");
        // Expect exactly 7 digits
        validated.setPattern("\\d{7}");
        validated.setMinLength(6);
        validated.setMaxLength(7);

        validated.setI18n(new TextField.TextFieldI18n()
                .setRequiredErrorMessage("Field is required")
                .setMinLengthErrorMessage("Minimum length is 6 characters")
                .setMaxLengthErrorMessage("Maximum length is 7 characters")
                .setPatternErrorMessage("Invalid code format"));

        addExample("Validation Properties Example", validated);
    }

    private void createTextFieldWithAriaLabel() {
        TextField textfield = new TextField();
        textfield.setAriaLabel("Invisible label");
        addExample("Aria label Example", textfield);
    }

    private void createTextFieldWithAriaLabelledBy() {
        TextField textfield = new TextField();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        textfield.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, textfield));
    }

    private void createEnabledDisabledExample() {
        TextField textfield = new TextField("Enabled/Disabled Field");
        textfield.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> textfield.setEnabled(!textfield.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(textfield, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }


}
