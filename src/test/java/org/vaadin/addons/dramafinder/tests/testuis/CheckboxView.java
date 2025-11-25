package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Checkbox Demo")
@Route(value = "checkbox", layout = MainLayout.class)
public class CheckboxView extends Main {

    public CheckboxView() {
        createBasicExample();
        createCheckedExample();
        createIndeterminateExample();
        createDisabledExample();
        createAriaLabelExample();
        createRequiredExample();
    }

    private void createBasicExample() {
        Checkbox checkbox = new Checkbox("Default Checkbox");
        checkbox.focus();
        addExample("Basic Checkbox", checkbox);
    }

    private void createCheckedExample() {
        Checkbox checkbox = new Checkbox("Checked by default");
        checkbox.setValue(true);
        addExample("Checked Checkbox", checkbox);
    }

    private void createIndeterminateExample() {
        Checkbox checkbox = new Checkbox("Indeterminate Checkbox");
        checkbox.setIndeterminate(true);
        addExample("Indeterminate Checkbox", checkbox);
    }

    private void createDisabledExample() {
        Checkbox checkbox = new Checkbox("Disabled Checkbox");
        checkbox.setEnabled(false);
        addExample("Disabled Checkbox", checkbox);
    }

    private void createAriaLabelExample() {
        Checkbox checkbox = new Checkbox();
        checkbox.setAriaLabel("Aria label");
        addExample("Aria label Checkbox", checkbox);
    }

    private void createRequiredExample() {
        Checkbox checkbox = new Checkbox("Required Checkbox");
        checkbox.setRequiredIndicatorVisible(true);
        checkbox.setI18n(new Checkbox.CheckboxI18n().setRequiredErrorMessage("Required Message"));
        addExample("Required Checkbox", checkbox);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
