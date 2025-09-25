package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("IntegerField Demo")
@Route(value = "integer", layout = MainLayout.class)
public class IntegerFieldView extends Main {

    public IntegerFieldView() {
        createBasicExample();
        createMinMaxStepExample();
        createControlsExample();
    }

    private void createBasicExample() {
        IntegerField integerField = new IntegerField("Quantity");
        integerField.setHelperText("Enter the number of items");
        addExample("Basic IntegerField", integerField);
    }

    private void createMinMaxStepExample() {
        IntegerField integerField = new IntegerField("Score");
        integerField.setHelperText("Value between 0 and 100, in steps of 10");
        integerField.setValue(10);
        integerField.setMin(0);
        integerField.setMax(100);
        integerField.setStep(10);
        addExample("Min, Max, and Step", integerField);
    }

    private void createControlsExample() {
        IntegerField integerField = new IntegerField("With Controls");
        integerField.setStepButtonsVisible(true);
        addExample("With Step Controls", integerField);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
