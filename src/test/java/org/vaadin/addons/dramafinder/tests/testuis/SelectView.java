package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Select Demo")
@Route(value = "select", layout = MainLayout.class)
public class SelectView extends Main {

    public SelectView() {
        createBasicExample();
        createSelectWithHelperExample();
        createSelectWithPlaceholderAndClearButton();
        createSelectWithThemeExample();
        createSelectWithAriaLabel();
        createSelectWithAriaLabelledBy();
        createEnabledDisabledExample();
    }

    private void createSelectWithThemeExample() {
        Select<String> select = new Select<>();
        select.setRequiredIndicatorVisible(true);
        select.setLabel("Select with theme");
        select.addThemeVariants(SelectVariant.LUMO_SMALL);
        select.setI18n(new Select.SelectI18n().setRequiredErrorMessage("Required"));
        Binder<DummyBean> binder = new Binder<>(DummyBean.class);
        binder.setBean(new DummyBean());
        binder.forField(select).asRequired("Required field").bind("name");
        Button validate = new Button("Validate", e -> binder.validate());
        addExample("Theme Example", new HorizontalLayout(select, validate));
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

    private void createSelectWithPlaceholderAndClearButton() {
        Select<String> select = new Select<>();
        select.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        select.setLabel("Select with placeholder and clear button");
        select.setPlaceholder("Enter text here");
        addExample("Placeholder and Clear Button Example", select);
    }

    private void createBasicExample() {
        Select<String> select = new Select<>();
        select.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        select.setLabel("Sort by");
        select.setTooltipText("Tooltip for select");
        select.focus();
        select.addClassName("custom-text-field");
        // Add prefix/suffix so tests can assert them
        select.setPrefixComponent(new Span("Prefix"));

        select.setHelperText("Helper text");
        // Add prefix and suffix content to demonstrate slot usage(
        select.setPrefixComponent(new Span("Prefix"));
        addExample("Basic Example", select);
    }

    private void createSelectWithHelperExample() {
        Select<String> select = new Select<>();
        select.setLabel("Select with helper component");
        select.addClassName("custom-text-field");
        Select<String> helperComponent = new Select<>();
        helperComponent.setLabel("Helper field");
        helperComponent.addClassName("custom-helper-component");
        helperComponent.setHelperText("Internal helper");
        select.setHelperComponent(helperComponent);
        addExample("Helper Component Example", select);
    }

    private void createSelectWithAriaLabel() {
        Select<String> select = new Select<>();
        select.setAriaLabel("Invisible label");
        addExample("Aria label Example", select);
    }

    private void createSelectWithAriaLabelledBy() {
        Select<String> select = new Select<>();
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        select.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, select));
    }

    private void createEnabledDisabledExample() {
        Select<String> select = new Select<>();
        select.setLabel("Enabled/Disabled Field");
        select.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> select.setEnabled(!select.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(select, button));
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }

}
