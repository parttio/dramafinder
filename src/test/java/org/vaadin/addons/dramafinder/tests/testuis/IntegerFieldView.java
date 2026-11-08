package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
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
        // The value the server received, to check the typed value reaches it
        Span serverValue = new Span("Server value: " + integerField.getValue());
        serverValue.setId("quantity-server-value");
        integerField.addValueChangeListener(e -> serverValue.setText("Server value: " + e.getValue()));
        addExample("Basic IntegerField", integerField);
        add(serverValue);
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
