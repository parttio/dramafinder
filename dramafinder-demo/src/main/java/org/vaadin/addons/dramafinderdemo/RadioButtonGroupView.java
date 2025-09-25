package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Radio Button Demo")
@Route(value = "radio-button-group", layout = MainLayout.class)
public class RadioButtonGroupView extends Main {

    public RadioButtonGroupView() {
        createBasicExample();
        createPreselectedValueExample();
        createDisabledExample();
        createHelperTextExample();
    }

    private void createBasicExample() {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setLabel("Basic RadioButtonGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        addExample("Basic RadioButtonGroup", group);
    }

    private void createPreselectedValueExample() {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setLabel("Pre-selected Value");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setValue("Option 2");
        addExample("Pre-selected Value", group);
    }

    private void createDisabledExample() {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setLabel("Disabled RadioButtonGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setEnabled(false);
        addExample("Disabled RadioButtonGroup", group);
    }

    private void createHelperTextExample() {
        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setLabel("Helper Text");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setHelperText("This is a helper text");
        addExample("Helper Text", group);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
