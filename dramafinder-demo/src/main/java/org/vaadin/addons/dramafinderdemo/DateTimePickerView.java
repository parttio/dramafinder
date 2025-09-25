package org.vaadin.addons.dramafinderdemo;

import java.time.LocalDateTime;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("DateTimePicker Demo")
@Route(value = "date-time-picker", layout = MainLayout.class)
public class DateTimePickerView extends Main {

    public DateTimePickerView() {
        createBasicExample();
        createMinMaxExample();
        createAriaLabelExample();
        createEnabledDisabledExample();
    }

    private void createBasicExample() {
        DateTimePicker dateTimePicker = new DateTimePicker("Appointment date");
        dateTimePicker.setHelperText("Enter the appointment date and time");
        dateTimePicker.setTooltipText("Tooltip for datetimepicker");
        dateTimePicker.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
        dateTimePicker.focus();
        addExample("Basic DateTimePicker", dateTimePicker);
    }


    private void createMinMaxExample() {
        DateTimePicker dateTimePicker = new DateTimePicker("Vacation date");
        dateTimePicker.setHelperText("You can only select a date in the next 30 days");
        LocalDateTime initialDate = LocalDateTime.of(2025, 9, 18, 14, 30);
        dateTimePicker.setValue(initialDate.plusDays(7));
        dateTimePicker.setMin(initialDate);
        dateTimePicker.setMax(initialDate.plusDays(30));
        dateTimePicker.setI18n(new DateTimePicker.DateTimePickerI18n()
                .setMaxErrorMessage("Maximum exceeded.")
                .setMinErrorMessage("Minimum exceeded.")
                .setBadInputErrorMessage("Bad input.")
        );
        addExample("Min and Max", dateTimePicker);
    }

    private void createAriaLabelExample() {
        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setAriaLabel("Invisible label");
        addExample("Aria label Example", dateTimePicker);
    }

    private void createEnabledDisabledExample() {
        DateTimePicker dateTimePicker = new DateTimePicker("Enabled/Disabled Field");
        dateTimePicker.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> dateTimePicker.setEnabled(!dateTimePicker.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(dateTimePicker, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
