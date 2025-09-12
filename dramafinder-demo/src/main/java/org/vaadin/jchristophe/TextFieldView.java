package org.vaadin.jchristophe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("TextField Demo")
@Route(value = "", layout = MainLayout.class)
public class TextFieldView extends Main {

    public TextFieldView() {
        createBasicExample();
        createTextFieldWithHelperExample();
        createTextFieldWithPlaceholderAndClearButton();

    }

    private void createTextFieldWithPlaceholderAndClearButton() {
        TextField textfield = new TextField("TextField with placeholder and clear button");
        textfield.setPlaceholder("Enter text here");
        textfield.setClearButtonVisible(true);
        addExample("Placeholder and Clear Button Example", textfield);
    }

    private void createBasicExample() {
        TextField textfield = new TextField("Textfield");
        textfield.addClassName("custom-text-field");
        textfield.setRequired(true);

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

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }

}
