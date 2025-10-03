package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Set;

@PageTitle("ListBox Demo")
@Route(value = "list-box", layout = MainLayout.class)
public class ListBoxView extends Main {

    public ListBoxView() {
        createBasicExample();
        createAriaLabelledBy();
        createEnabledDisabledExample();
        createMultipleList();
        createMultipleListDataBean();
    }

    private void createBasicExample() {
        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        listBox.setTooltipText("Tooltip for listbox");
        listBox.addClassName("list-box");
        listBox.setAriaLabel("Invisible label");
        listBox.setValue("Most recent first");
        add(listBox);
        addExample("Basic example", listBox);
    }

    private void createAriaLabelledBy() {
        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        NativeLabel label = new NativeLabel("Labelled by");
        String id = "label-id";
        label.setId(id);
        listBox.setAriaLabelledBy(id);
        addExample("Aria labelled by Example", new HorizontalLayout(label, listBox));
    }

    private void createEnabledDisabledExample() {
        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("Most recent first", "Rating: high to low", "Always disabled");
        listBox.setItemEnabledProvider(item -> !"Always disabled".equals(item));
        listBox.setAriaLabel("Enabled/Disabled Field");
        listBox.setEnabled(false);
        Button button = new Button("Enable/Disable");
        button.setId("enable-disable-button");
        button.addClickListener(e -> listBox.setEnabled(!listBox.isEnabled()));
        addExample("Enabled/Disabled Example", new HorizontalLayout(listBox, button));
    }

    private void createMultipleList() {
        MultiSelectListBox<String> listBox = new MultiSelectListBox<>();
        listBox.setAriaLabel("Multiple list");
        listBox.setItems("Most recent first", "Rating: high to low", "Rating: low to high");
        listBox.setValue(Set.of("Most recent first"));
        add(listBox);
        addExample("Multiple list", listBox);
    }

    private void createMultipleListDataBean() {
        MultiSelectListBox<DataBean> listBox = new MultiSelectListBox<>();
        listBox.setRenderer(new ComponentRenderer<Component, DataBean>(data -> new Div(new Span(data.name), new Span(data.surname))));
        listBox.setAriaLabel("Multiple databean");
        List<DataBean> dataBeans = List.of(
                new DataBean("name", "surname"),
                new DataBean("John", "Doe"));
        listBox.setItems(dataBeans);
        add(listBox);
        addExample("Multiple list", listBox);
    }

    public record DataBean(String name, String surname) {
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
