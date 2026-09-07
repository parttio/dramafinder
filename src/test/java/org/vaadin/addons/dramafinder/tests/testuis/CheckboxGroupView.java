package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.Set;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Checkbox Group Demo")
@Route(value = "checkbox-group", layout = MainLayout.class)
public class CheckboxGroupView extends Main {

    public CheckboxGroupView() {
        createBasicExample();
        createPreselectedValueExample();
        createDisabledExample();
        createDisabledItemExample();
        createHelperTextExample();
        createThemeExample();
        createTooltipExample();
        createRequiredExample();
    }

    private void createBasicExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Basic CheckboxGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        addExample("Basic CheckboxGroup", group);
    }

    private void createPreselectedValueExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Pre-selected Value");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setValue(Set.of("Option 1", "Option 3"));
        addExample("Pre-selected Value", group);
    }

    private void createDisabledExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Disabled CheckboxGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setEnabled(false);
        addExample("Disabled CheckboxGroup", group);
    }

    private void createDisabledItemExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Disabled Item");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setItemEnabledProvider(item -> !"Option 2".equals(item));
        addExample("Disabled Item", group);
    }

    private void createHelperTextExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Helper Text");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setHelperText("This is a helper text");
        addExample("Helper Text", group);
    }

    private void createThemeExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Vertical CheckboxGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.getElement().setAttribute("theme", "vertical");
        group.addClassName("styled-group");
        addExample("Vertical CheckboxGroup", group);
    }

    private void createTooltipExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Tooltip CheckboxGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setTooltipText("This is a tooltip");
        addExample("Tooltip CheckboxGroup", group);
    }

    private void createRequiredExample() {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.setLabel("Required CheckboxGroup");
        group.setItems("Option 1", "Option 2", "Option 3");
        group.setRequiredIndicatorVisible(true);
        group.setErrorMessage("Selection is required");
        Button validate = new Button("Validate CheckboxGroup",
                event -> group.setInvalid(group.isEmpty()));
        addExample("Required CheckboxGroup", group);
        add(validate);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
