package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("IntegerField GetByLabel Demo")
@Route(value = "integerfield-getbylabel", layout = MainLayout.class)
public class IntegerFieldGetByLabelView extends Main {

    public IntegerFieldGetByLabelView() {
        IntegerField withLabel = new IntegerField("My Label");

        IntegerField withPlaceholder = new IntegerField();
        withPlaceholder.setPlaceholder("My Placeholder");

        IntegerField withAriaLabel = new IntegerField();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        IntegerField outsideContainer = new IntegerField("Outside Label");

        add(container, outsideContainer);
    }
}
