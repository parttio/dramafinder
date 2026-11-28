package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * A multi-select combo box with a button right below it, where the overlay
 * opens: the button can only be clicked once the overlay is closed (issue #344).
 */
@PageTitle("MultiSelectComboBox Overlay Demo")
@Route(value = "multi-select-combo-box-overlay", layout = MainLayout.class)
public class MultiSelectComboBoxOverlayView extends Main {

    public MultiSelectComboBoxOverlayView() {
        MultiSelectComboBox<String> fruits = new MultiSelectComboBox<>("Fruits");
        fruits.setItems("Apple", "Banana", "Cherry", "Date", "Elderberry");
        Span value = new Span("empty");
        value.setId("fruits-value");
        fruits.addValueChangeListener(e -> value.setText(e.getValue().isEmpty()
                ? "empty"
                : e.getValue().stream().sorted().collect(Collectors.joining(", "))));

        Span clicked = new Span("not clicked");
        clicked.setId("below-clicked");
        Button below = new Button("Below", e -> clicked.setText("clicked"));

        add(fruits, below, clicked, value);
        getStyle().set("display", "flex").set("flex-direction", "column").set("width", "300px");
    }
}
