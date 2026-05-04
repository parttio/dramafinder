package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("TextField GetByLabel Demo")
@Route(value = "textfield-getbylabel", layout = MainLayout.class)
public class TextFieldGetByLabelView extends Main {

    public TextFieldGetByLabelView() {
        TextField withLabel = new TextField("My Label");

        TextField withPlaceholder = new TextField();
        withPlaceholder.setPlaceholder("My Placeholder");

        TextField withAriaLabel = new TextField();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        TextField outsideContainer = new TextField("Outside Label");

        add(container, outsideContainer);
    }
}
