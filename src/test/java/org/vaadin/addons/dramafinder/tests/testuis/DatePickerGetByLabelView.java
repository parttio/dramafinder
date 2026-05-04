package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("DatePicker GetByLabel Demo")
@Route(value = "datepicker-getbylabel", layout = MainLayout.class)
public class DatePickerGetByLabelView extends Main {

    public DatePickerGetByLabelView() {
        DatePicker withLabel = new DatePicker("My Label");

        DatePicker withPlaceholder = new DatePicker();
        withPlaceholder.setPlaceholder("My Placeholder");

        DatePicker withAriaLabel = new DatePicker();
        withAriaLabel.setAriaLabel("My AriaLabel");

        Div container = new Div(withLabel, withPlaceholder, withAriaLabel);
        container.setId("container");

        DatePicker outsideContainer = new DatePicker("Outside Label");

        add(container, outsideContainer);
    }
}
