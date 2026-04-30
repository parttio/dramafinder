package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Select GetByLabel Demo")
@Route(value = "select-getbylabel", layout = MainLayout.class)
public class SelectGetByLabelView extends Main {

    public SelectGetByLabelView() {
        Select<String> withLabel = new Select<>();
        withLabel.setLabel("My Label");

        Select<String> withPlaceholder = new Select<>();
        withPlaceholder.setPlaceholder("My Placeholder");

        Select<String> withAriaLabel = new Select<>();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        Select<String> outsideContainer = new Select<>();
        outsideContainer.setLabel("Outside Label");

        add(container, outsideContainer);
    }
}
