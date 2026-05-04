package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("TextArea GetByLabel Demo")
@Route(value = "textarea-getbylabel", layout = MainLayout.class)
public class TextAreaGetByLabelView extends Main {

    public TextAreaGetByLabelView() {
        TextArea withLabel = new TextArea("My Label");

        TextArea withPlaceholder = new TextArea();
        withPlaceholder.setPlaceholder("My Placeholder");

        TextArea withAriaLabel = new TextArea();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        TextArea outsideContainer = new TextArea("Outside Label");

        add(container, outsideContainer);
    }
}
