package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Duration;
import java.time.LocalTime;

@PageTitle("TimePicker Demo")
@Route(value = "time-picker", layout = MainLayout.class)
public class TimePickerView extends Main {

    public TimePickerView() {
        createBasicExample();
        createMinMaxStepExample();
        createAriaLabelExample();
        createAriaLabelledByExample();
        createEnabledDisabledExample();
    }

    private void createBasicExample() {
        TimePicker timePicker = new TimePicker("Appointment time");
        timePicker.setHelperText("Enter the appointment time");
        timePicker.setTooltipText("Tooltip for component");
        timePicker.focus();
        timePicker.addThemeVariants(TimePickerVariant.LUMO_SMALL);
        addExample("Basic TimePicker", timePicker);
    }

    private void createMinMaxStepExample() {
        TimePicker timePicker = new TimePicker("Meeting time");
        timePicker.setHelperText("Select a time between 09:00 and 17:00, in 30-minute steps");
        timePicker.setValue(LocalTime.of(10, 30));
        timePicker.setMin(LocalTime.of(9, 0));
        timePicker.setMax(LocalTime.of(17, 0));
        timePicker.setStep(Duration.ofMinutes(30));
        timePicker.setI18n(new TimePicker.TimePickerI18n()
                .setMaxErrorMessage("Maximum time exceeded")
                .setMinErrorMessage("Minimum time exceeded"));
        addExample("Min, Max and Step", timePicker);
    }

    private void createAriaLabelExample() {
        TimePicker timePicker = new TimePicker();
        timePicker.setAriaLabel("Invisible label");
        addExample("Aria label Example", timePicker);
    }

    private void createAriaLabelledByExample() {
        TimePicker timePicker = new TimePicker();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        timePicker.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, timePicker));
    }

    private void createEnabledDisabledExample() {
        TimePicker timePicker = new TimePicker("Enabled/Disabled Field");
        timePicker.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> timePicker.setEnabled(!timePicker.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(timePicker, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
