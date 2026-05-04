package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("DateTimePicker GetByLabel Demo")
@Route(value = "datetimepicker-getbylabel", layout = MainLayout.class)
public class DateTimePickerGetByLabelView extends Main {

    public DateTimePickerGetByLabelView() {
        DateTimePicker withLabel = new DateTimePicker("My Label");

        DateTimePicker withPlaceholder = new DateTimePicker();
        withPlaceholder.setDatePlaceholder("My Placeholder");

        DateTimePicker withAriaLabel = new DateTimePicker();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        DateTimePicker outsideContainer = new DateTimePicker("Outside Label");

        add(container, outsideContainer);
    }
}
