package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@PageTitle("DatePicker Demo")
@Route(value = "date-picker", layout = MainLayout.class)
public class DatePickerView extends Main {

    public DatePickerView() {
        createBasicExample();
        createMinMaxExample();
        createAriaLabelExample();
        createAriaLabelledByExample();
        createEnabledDisabledExample();
    }

    private void createBasicExample() {
        DatePicker datePicker = new DatePicker("Appointment date");
        datePicker.setHelperText("Enter the appointment date");
        datePicker.setTooltipText("Tooltip for datepicker");
        datePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        datePicker.focus();
        addExample("Basic DatePicker", datePicker);
    }

    private void createMinMaxExample() {
        DatePicker datePicker = new DatePicker("Vacation date");
        datePicker.setHelperText("You can only select a date in the next 30 days");
        LocalDate now = LocalDate.now();
        datePicker.setValue(now.plusDays(7));
        datePicker.setMin(now);
        datePicker.setMax(now.plusDays(30));
        datePicker.setI18n(new DatePicker.DatePickerI18n()
                .setMaxErrorMessage("Maximum number of days exceeded.")
                .setMinErrorMessage("Minimum number of days exceeded.")
        );
        addExample("Min and Max", datePicker);
    }

    private void createAriaLabelExample() {
        DatePicker datePicker = new DatePicker();
        datePicker.setAriaLabel("Invisible label");
        addExample("Aria label Example", datePicker);
    }

    private void createAriaLabelledByExample() {
        DatePicker datePicker = new DatePicker();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        datePicker.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, datePicker));
    }

    private void createEnabledDisabledExample() {
        DatePicker datePicker = new DatePicker("Enabled/Disabled Field");
        datePicker.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> datePicker.setEnabled(!datePicker.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(datePicker, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
