package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("PasswordField GetByLabel Demo")
@Route(value = "passwordfield-getbylabel", layout = MainLayout.class)
public class PasswordFieldGetByLabelView extends Main {

    public PasswordFieldGetByLabelView() {
        PasswordField withLabel = new PasswordField("My Label");

        PasswordField withPlaceholder = new PasswordField();
        withPlaceholder.setPlaceholder("My Placeholder");

        PasswordField withAriaLabel = new PasswordField();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        PasswordField outsideContainer = new PasswordField("Outside Label");

        add(container, outsideContainer);
    }
}
