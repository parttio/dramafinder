package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("TimePicker GetByLabel Demo")
@Route(value = "timepicker-getbylabel", layout = MainLayout.class)
public class TimePickerGetByLabelView extends Main {

    public TimePickerGetByLabelView() {
        TimePicker withLabel = new TimePicker("My Label");

        TimePicker withPlaceholder = new TimePicker();
        withPlaceholder.setPlaceholder("My Placeholder");

        TimePicker withAriaLabel = new TimePicker();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        TimePicker outsideContainer = new TimePicker("Outside Label");

        add(container, outsideContainer);
    }
}
