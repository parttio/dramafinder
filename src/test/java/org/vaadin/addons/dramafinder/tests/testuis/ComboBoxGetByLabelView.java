package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("ComboBox GetByLabel Demo")
@Route(value = "combobox-getbylabel", layout = MainLayout.class)
public class ComboBoxGetByLabelView extends Main {

    public ComboBoxGetByLabelView() {
        ComboBox<String> withLabel = new ComboBox<>("My Label");

        ComboBox<String> withPlaceholder = new ComboBox<>();
        withPlaceholder.setPlaceholder("My Placeholder");

        ComboBox<String> withAriaLabel = new ComboBox<>();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        ComboBox<String> outsideContainer = new ComboBox<>("Outside Label");

        add(container, outsideContainer);
    }
}
