package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@PageTitle("MultiSelectComboBox Demo")
@Route(value = "multi-select-combo-box", layout = MainLayout.class)
public class MultiSelectComboBoxView extends Main {

    public MultiSelectComboBoxView() {
        createBasicExample();
        createComboBoxWithHelperExample();
        createComboBoxWithPlaceholderAndClearButton();
        createComboBoxWithThemeExample();
        createComboBoxWithAriaLabel();
        createComboBoxWithAriaLabelledBy();
        createEnabledDisabledExample();
        createReadOnlyExample();
        createFilterableExample();
        createSelectedItemsOnTopExample();
        createAutoExpandExample();
        createLazyLoadingExample();
        createCustomRendererExample();
    }

    private void createBasicExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Apple", "Banana", "Cherry", "Date", "Elderberry");
        comboBox.setLabel("Fruits");
        comboBox.setTooltipText("Tooltip for multi-select");
        comboBox.focus();
        comboBox.addClassName("custom-multi-select");
        comboBox.setHelperText("Helper text");
        addExample("Basic Example", comboBox);
    }

    private void createComboBoxWithHelperExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel("MultiSelect with helper component");
        comboBox.addClassName("custom-multi-select");
        MultiSelectComboBox<String> helperComponent = new MultiSelectComboBox<>();
        helperComponent.setLabel("Helper field");
        helperComponent.addClassName("custom-helper-component");
        helperComponent.setHelperText("Internal helper");
        comboBox.setHelperComponent(helperComponent);
        addExample("Helper Component Example", comboBox);
    }

    private void createComboBoxWithPlaceholderAndClearButton() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Apple", "Banana", "Cherry");
        comboBox.setLabel("MultiSelect with placeholder and clear button");
        comboBox.setPlaceholder("Select items");
        comboBox.setClearButtonVisible(true);
        addExample("Placeholder and Clear Button Example", comboBox);
    }

    private void createComboBoxWithThemeExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setRequiredIndicatorVisible(true);
        comboBox.setLabel("MultiSelect with theme");
        comboBox.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        Binder<DummyBean> binder = new Binder<>(DummyBean.class);
        binder.setBean(new DummyBean());
        binder.forField(comboBox).asRequired("Required field").bind("names");
        Button validate = new Button("Validate", e -> binder.validate());
        addExample("Theme Example", new HorizontalLayout(comboBox, validate));
    }

    public static class DummyBean {
        private Set<String> names;

        public Set<String> getNames() {
            return names;
        }

        public void setNames(Set<String> names) {
            this.names = names;
        }
    }

    private void createComboBoxWithAriaLabel() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setAriaLabel("Invisible label");
        addExample("Aria label Example", comboBox);
    }

    private void createComboBoxWithAriaLabelledBy() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "multi-combo-label-id";
        label.setId(id);
        comboBox.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, comboBox));
    }

    private void createEnabledDisabledExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel("Enabled/Disabled Field");
        comboBox.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> comboBox.setEnabled(!comboBox.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(comboBox, button));
    }

    private void createReadOnlyExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Apple", "Banana", "Cherry");
        comboBox.setLabel("Read-only MultiSelect");
        comboBox.setValue(Set.of("Apple", "Cherry"));
        comboBox.setReadOnly(true);
        addExample("Read-only Example", comboBox);
    }

    private void createFilterableExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Apple", "Apricot", "Banana", "Blueberry", "Cherry",
                "Date", "Elderberry", "Fig", "Grape");
        comboBox.setLabel("Filterable MultiSelect");
        addExample("Filterable Example", comboBox);
    }

    private void createSelectedItemsOnTopExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
        comboBox.setLabel("Selected on top");
        comboBox.setSelectedItemsOnTop(true);
        addExample("Selected Items On Top Example", comboBox);
    }

    private void createAutoExpandExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Short", "Medium length", "A rather long item name");
        comboBox.setLabel("Auto expand");
        comboBox.setAutoExpand(MultiSelectComboBox.AutoExpandMode.BOTH);
        addExample("Auto Expand Example", comboBox);
    }

    private void createLazyLoadingExample() {
        List<String> allItems = IntStream.rangeClosed(1, 500)
                .mapToObj(i -> "Item " + i)
                .toList();

        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel("Lazy MultiSelect");
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
                        e.getValue() != null
                                ? e.getValue().stream().sorted().collect(Collectors.joining(", "))
                                : ""));

        addExample("Lazy Loading Example", comboBox, selectedValueDisplay);
    }

    private void createCustomRendererExample() {
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setItems("Apple", "Banana", "Cherry");
        comboBox.setLabel("Custom renderer");

        Span output = new Span();
        output.setId("custom-renderer-output");

        comboBox.setRenderer(new ComponentRenderer<>(item -> {
            Span label = new Span(item);
            Button button = new Button("Info " + item);
            button.addClickListener(e ->
                    output.setText("Clicked: " + item));
            return new HorizontalLayout(label, button);
        }));

        addExample("Custom Renderer Example", comboBox, output);
    }

    private void addExample(String title, Component... components) {
        add(new H2(title));
        for (Component component : components) {
            add(component);
        }
    }
}
