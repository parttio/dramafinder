package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("MultiSelectComboBox GetByLabel Demo")
@Route(value = "multiselectcombobox-getbylabel", layout = MainLayout.class)
public class MultiSelectComboBoxGetByLabelView extends Main {

    public MultiSelectComboBoxGetByLabelView() {
        MultiSelectComboBox<String> withLabel = new MultiSelectComboBox<>("My Label");

        MultiSelectComboBox<String> withPlaceholder = new MultiSelectComboBox<>();
        withPlaceholder.setPlaceholder("My Placeholder");

        MultiSelectComboBox<String> withAriaLabel = new MultiSelectComboBox<>();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        MultiSelectComboBox<String> outsideContainer = new MultiSelectComboBox<>("Outside Label");

        add(container, outsideContainer);
    }
}
