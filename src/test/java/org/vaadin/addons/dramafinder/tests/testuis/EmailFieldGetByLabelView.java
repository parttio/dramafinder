package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("EmailField GetByLabel Demo")
@Route(value = "emailfield-getbylabel", layout = MainLayout.class)
public class EmailFieldGetByLabelView extends Main {

    public EmailFieldGetByLabelView() {
        EmailField withLabel = new EmailField("My Label");

        EmailField withPlaceholder = new EmailField();
        withPlaceholder.setPlaceholder("My Placeholder");

        EmailField withAriaLabel = new EmailField();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        EmailField outsideContainer = new EmailField("Outside Label");

        add(container, outsideContainer);
    }
}
