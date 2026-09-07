package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.slider.DecimalSlider;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Slider Demo")
@Route(value = "slider", layout = MainLayout.class)
public class SliderView extends Main {

    public SliderView() {
        createBasicExample();
        createMinMaxStepExample();
        createStatesExample();
        createStyledExample();
    }

    private void createBasicExample() {
        IntegerSlider slider = new IntegerSlider("Volume", 0, 100);
        slider.setHelperText("Pick a value between 0 and 100");
        slider.setValue(50);
        slider.setMinMaxVisible(true);

        Span selectedValue = new Span("Selected: 50");
        selectedValue.setId("volume-value");
        slider.addValueChangeListener(event -> selectedValue.setText("Selected: " + event.getValue()));

        addExample("Basic Slider", slider, selectedValue);
    }

    private void createMinMaxStepExample() {
        DecimalSlider slider = new DecimalSlider("Measurement", 0.5, 10);
        slider.setHelperText("Value between 0.5 and 10, in steps of 0.5");
        slider.setStep(0.5);
        slider.setValue(1.5);
        addExample("Min, Max, and Step", slider);
    }

    private void createStatesExample() {
        IntegerSlider disabled = new IntegerSlider("Disabled slider", 0, 100);
        disabled.setValue(20);
        disabled.setEnabled(false);

        IntegerSlider readOnly = new IntegerSlider("Read-only slider", 0, 100);
        readOnly.setValue(30);
        readOnly.setReadOnly(true);

        addExample("Disabled and Read-only", disabled, readOnly);
    }

    private void createStyledExample() {
        IntegerSlider slider = new IntegerSlider("Styled slider", 0, 10);
        slider.setValue(4);
        slider.setTooltipText("Drag to change the rating");
        slider.getElement().getThemeList().add("small");
        slider.addClassName("rating-slider");
        addExample("Theme, Style, and Tooltip", slider);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        add(components);
    }
}
