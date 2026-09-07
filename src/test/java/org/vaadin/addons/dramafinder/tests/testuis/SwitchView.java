package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Switch;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Switch Demo")
@Route(value = "switch", layout = MainLayout.class)
public class SwitchView extends Main {

    public SwitchView() {
        createBasicExample();
        createCheckedExample();
        createDisabledExample();
        createAriaLabelExample();
        createHelperExample();
        createRequiredExample();
    }

    private void createBasicExample() {
        Switch switchComponent = new Switch("Default Switch");
        switchComponent.focus();
        addExample("Basic Switch", switchComponent);
    }

    private void createCheckedExample() {
        Switch switchComponent = new Switch("Checked by default");
        switchComponent.setValue(true);
        addExample("Checked Switch", switchComponent);
    }

    private void createDisabledExample() {
        Switch switchComponent = new Switch("Disabled Switch");
        switchComponent.setEnabled(false);
        addExample("Disabled Switch", switchComponent);
    }

    private void createAriaLabelExample() {
        Switch switchComponent = new Switch();
        switchComponent.setAriaLabel("Aria label");
        addExample("Aria label Switch", switchComponent);
    }

    private void createHelperExample() {
        Switch switchComponent = new Switch("Switch with helper");
        switchComponent.setHelperText("Helper text");
        addExample("Helper Switch", switchComponent);
    }

    private void createRequiredExample() {
        Switch switchComponent = new Switch("Required Switch");
        switchComponent.setRequiredIndicatorVisible(true);
        switchComponent.setI18n(new Switch.SwitchI18n().setRequiredErrorMessage("Required Message"));
        addExample("Required Switch", switchComponent);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
