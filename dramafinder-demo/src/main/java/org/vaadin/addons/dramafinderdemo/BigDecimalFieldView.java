package org.vaadin.addons.dramafinderdemo;

import java.math.BigDecimal;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("BigDecimalField Demo")
@Route(value = "bigdecimal", layout = MainLayout.class)
public class BigDecimalFieldView extends Main {

    public BigDecimalFieldView() {
        createBasicExample();
    }

    private void createBasicExample() {
        BigDecimalField bigDecimalField = new BigDecimalField("Amount");
        bigDecimalField.setValue(new BigDecimal("123.456789"));
        bigDecimalField.setHelperText("Enter a precise decimal value");
        addExample("Basic BigDecimalField", bigDecimalField);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
