package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("BigDecimalField GetByLabel Demo")
@Route(value = "bigdecimalfield-getbylabel", layout = MainLayout.class)
public class BigDecimalFieldGetByLabelView extends Main {

    public BigDecimalFieldGetByLabelView() {
        BigDecimalField withLabel = new BigDecimalField("My Label");

        BigDecimalField withPlaceholder = new BigDecimalField();
        withPlaceholder.setPlaceholder("My Placeholder");

        BigDecimalField withAriaLabel = new BigDecimalField();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        BigDecimalField outsideContainer = new BigDecimalField("Outside Label");

        add(container, outsideContainer);
    }
}
