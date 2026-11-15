package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Light DOM Demo")
@Route(value = "light-dom", layout = MainLayout.class)
public class LightDomView extends Main {

    public LightDomView() {
        Div nested = new Div(new Button("Nested"));
        nested.setId("light-dom-nested");

        Div container = new Div(new Button("Direct"), nested, new TextField("Name"));
        container.setId("light-dom-container");

        add(container);
    }
}
