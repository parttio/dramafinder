package org.vaadin.jchristophe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("TextArea Demo")
@Route(value = "text-area", layout = MainLayout.class)
public class TextAreaView extends Main {

    public TextAreaView() {
        createBasicExample();
        createTextAreaWithHelperExample();
        createTextAreaWithPlaceholderAndClearButton();
        createValidationPropertiesExample();
        createTextAreaWithThemeExample();
        createTextAreaWithAriaLabel();
        createTextAreaWithAriaLabelledBy();
        createEnabledDisabledExample();
    }

    private void createTextAreaWithThemeExample() {
        TextArea textArea = new TextArea("TextArea with theme");
        textArea.addThemeVariants(TextAreaVariant.LUMO_SMALL);
        addExample("Theme Example", textArea);
    }

    private void createTextAreaWithPlaceholderAndClearButton() {
        TextArea textArea = new TextArea("TextArea with placeholder and clear button");
        textArea.setPlaceholder("Enter text here");
        textArea.setClearButtonVisible(true);
        addExample("Placeholder and Clear Button Example", textArea);
    }

    private void createBasicExample() {
        TextArea textArea = new TextArea("TextArea");
        textArea.setTooltipText("Tooltip for textArea");
        textArea.focus();
        textArea.addClassName("custom-text-field");
        textArea.setRequired(true);
        // Add prefix/suffix so tests can assert them
        textArea.setPrefixComponent(new Span("Prefix"));
        textArea.setSuffixComponent(new Span("Suffix"));

        textArea.setI18n(new TextArea.TextAreaI18n()
                .setRequiredErrorMessage("Field is required")
                .setMinLengthErrorMessage("Minimum length is 5 characters")
                .setMaxLengthErrorMessage("Maximum length is 18 characters")
                .setPatternErrorMessage("Invalid phone number format"));
        textArea.setHelperText("Helper text");
        // Add prefix and suffix content to demonstrate slot usage
        textArea.setPrefixComponent(new Span("Prefix"));
        textArea.setSuffixComponent(new Span("Suffix"));
        addExample("Basic Example", textArea);
    }

    private void createTextAreaWithHelperExample() {
        TextArea textArea = new TextArea("TextArea with helper component");
        textArea.addClassName("custom-text-field");
        textArea.setRequired(true);
        TextArea helperComponent = new TextArea("Helper field");
        helperComponent.addClassName("custom-helper-component");
        helperComponent.setHelperText("Internal helper");
        textArea.setHelperComponent(helperComponent);
        addExample("Helper Component Example", textArea);
    }

    private void createValidationPropertiesExample() {
        TextArea validated = new TextArea("Validated TextArea");
        validated.setHelperText("This field accepts only 7 numbers 0 to 8. 9 is forbidden");
        validated.addClassName("validated-text-field");
        validated.setRequired(true);
        // Allowed characters are digits only
        validated.setAllowedCharPattern("[0-8]");
        // Expect exactly 7 digits
        validated.setPattern("\\d{7}");
        validated.setMinLength(6);
        validated.setMaxLength(7);

        validated.setI18n(new TextArea.TextAreaI18n()
                .setRequiredErrorMessage("Field is required")
                .setMinLengthErrorMessage("Minimum length is 6 characters")
                .setMaxLengthErrorMessage("Maximum length is 7 characters")
                .setPatternErrorMessage("Invalid code format"));

        addExample("Validation Properties Example", validated);
    }

    private void createTextAreaWithAriaLabel() {
        TextArea textArea = new TextArea();
        textArea.setAriaLabel("Invisible label");
        addExample("Aria label Example", textArea);
    }

    private void createTextAreaWithAriaLabelledBy() {
        TextArea textArea = new TextArea();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        textArea.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, textArea));
    }

    private void createEnabledDisabledExample() {
        TextArea textArea = new TextArea("Enabled/Disabled Field");
        textArea.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> textArea.setEnabled(!textArea.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(textArea, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
