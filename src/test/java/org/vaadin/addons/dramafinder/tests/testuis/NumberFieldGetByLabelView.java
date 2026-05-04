package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("NumberField GetByLabel Demo")
@Route(value = "numberfield-getbylabel", layout = MainLayout.class)
public class NumberFieldGetByLabelView extends Main {

    public NumberFieldGetByLabelView() {
        NumberField withLabel = new NumberField("My Label");

        NumberField withPlaceholder = new NumberField();
        withPlaceholder.setPlaceholder("My Placeholder");

        NumberField withAriaLabel = new NumberField();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        NumberField outsideContainer = new NumberField("Outside Label");

        add(container, outsideContainer);
    }
}
