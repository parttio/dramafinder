package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.IntStream;

@PageTitle("ComboBox Demo")
@Route(value = "combo-box", layout = MainLayout.class)
public class ComboBoxView extends Main {

    public ComboBoxView() {
        createBasicExample();
        createComboBoxWithHelperExample();
        createComboBoxWithPlaceholderAndClearButton();
        createComboBoxWithThemeExample();
        createComboBoxWithAriaLabel();
        createComboBoxWithAriaLabelledBy();
        createEnabledDisabledExample();
        createReadOnlyExample();
        createFilterableExample();
        createCustomValueExample();
        createAllowedCharPatternExample();
        createLazyLoadingExample();
    }

    private void createBasicExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        comboBox.setLabel("Sort by");
        comboBox.setTooltipText("Tooltip for combo box");
        comboBox.focus();
        comboBox.addClassName("custom-combo-box");
        comboBox.setPrefixComponent(new Span("Prefix"));
        comboBox.setHelperText("Helper text");
        addExample("Basic Example", comboBox);
    }

    private void createComboBoxWithHelperExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLabel("ComboBox with helper component");
        comboBox.addClassName("custom-combo-box");
        ComboBox<String> helperComponent = new ComboBox<>();
        helperComponent.setLabel("Helper field");
        helperComponent.addClassName("custom-helper-component");
        helperComponent.setHelperText("Internal helper");
        comboBox.setHelperComponent(helperComponent);
        addExample("Helper Component Example", comboBox);
    }

    private void createComboBoxWithPlaceholderAndClearButton() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        comboBox.setLabel("ComboBox with placeholder and clear button");
        comboBox.setPlaceholder("Enter text here");
        comboBox.setClearButtonVisible(true);
        addExample("Placeholder and Clear Button Example", comboBox);
    }

    private void createComboBoxWithThemeExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setRequiredIndicatorVisible(true);
        comboBox.setLabel("ComboBox with theme");
        comboBox.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        Binder<DummyBean> binder = new Binder<>(DummyBean.class);
        binder.setBean(new DummyBean());
        binder.forField(comboBox).asRequired("Required field").bind("name");
        Button validate = new Button("Validate", e -> binder.validate());
        addExample("Theme Example", new HorizontalLayout(comboBox, validate));
    }

    public static class DummyBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private void createComboBoxWithAriaLabel() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setAriaLabel("Invisible label");
        addExample("Aria label Example", comboBox);
    }

    private void createComboBoxWithAriaLabelledBy() {
        ComboBox<String> comboBox = new ComboBox<>();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "combo-label-id";
        label.setId(id);
        comboBox.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, comboBox));
    }

    private void createEnabledDisabledExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLabel("Enabled/Disabled Field");
        comboBox.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> comboBox.setEnabled(!comboBox.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(comboBox, button));
    }

    private void createReadOnlyExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("Apple", "Banana", "Cherry");
        comboBox.setLabel("Read-only ComboBox");
        comboBox.setValue("Banana");
        comboBox.setReadOnly(true);
        addExample("Read-only Example", comboBox);
    }

    private void createFilterableExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("Apple", "Apricot", "Banana", "Blueberry", "Cherry",
                "Date", "Elderberry", "Fig", "Grape");
        comboBox.setLabel("Filterable ComboBox");
        addExample("Filterable Example", comboBox);
    }

    private void createCustomValueExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("Option 1", "Option 2", "Option 3");
        comboBox.setLabel("Custom value ComboBox");
        comboBox.setAllowCustomValue(true);
        Span customValueDisplay = new Span();
        customValueDisplay.setId("custom-value-display");
        comboBox.addCustomValueSetListener(e -> {
            customValueDisplay.setText(e.getDetail());
            comboBox.setValue(e.getDetail());
        });
        addExample("Custom Value Example", comboBox, customValueDisplay);
    }

    private void createAllowedCharPatternExample() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems("ABC", "DEF", "GHI");
        comboBox.setLabel("Allowed char pattern ComboBox");
        comboBox.setAllowedCharPattern("[A-Z]");
        addExample("Allowed Char Pattern Example", comboBox);
    }

    private void createLazyLoadingExample() {
        List<String> allItems = IntStream.rangeClosed(1, 500)
                .mapToObj(i -> "Item " + i)
                .toList();

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setLabel("Lazy ComboBox");
        comboBox.setItems(
                query -> {
                    String filter = query.getFilter().orElse("");
                    return allItems.stream()
                            .filter(item -> item.toLowerCase()
                                    .contains(filter.toLowerCase()))
                            .skip(query.getOffset())
                            .limit(query.getLimit());
                },
                query -> {
                    String filter = query.getFilter().orElse("");
                    return (int) allItems.stream()
                            .filter(item -> item.toLowerCase()
                                    .contains(filter.toLowerCase()))
                            .count();
                }
        );
        comboBox.setClearButtonVisible(true);

        Span selectedValueDisplay = new Span();
        selectedValueDisplay.setId("lazy-selected-value");
        comboBox.addValueChangeListener(e ->
                selectedValueDisplay.setText(
                        e.getValue() != null ? e.getValue() : ""));

        addExample("Lazy Loading Example", comboBox, selectedValueDisplay);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        for (Component component : components) {
            add(component);
        }
    }
}
