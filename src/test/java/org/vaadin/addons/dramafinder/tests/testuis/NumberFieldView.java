package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("NumberField Demo")
@Route(value = "number", layout = MainLayout.class)
public class NumberFieldView extends Main {

    public NumberFieldView() {
        createBasicExample();
        createMinMaxStepExample();
        createControlsExample();
    }

    private void createBasicExample() {
        NumberField numberField = new NumberField("Weight");
        numberField.setHelperText("Enter the weight in kg");
        addExample("Basic NumberField", numberField);
    }

    private void createMinMaxStepExample() {
        NumberField numberField = new NumberField("Measurement");
        numberField.setHelperText("Value between 0.5 and 10.5, in steps of 0.5");
        numberField.setValue(1.5);
        numberField.setMin(0.5);
        numberField.setMax(10);
        numberField.setStep(0.5);
        addExample("Min, Max, and Step", numberField);
    }

    private void createControlsExample() {
        NumberField numberField = new NumberField("With Controls");
        numberField.setStepButtonsVisible(true);
        numberField.setStep(0.1);
        addExample("With Step Controls", numberField);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
