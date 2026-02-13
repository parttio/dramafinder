package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.IntStream;

@PageTitle("VirtualList Demo")
@Route(value = "virtual-list", layout = MainLayout.class)
public class VirtualListView extends Main {

    public VirtualListView() {
        createBasicExample();
        createEmptyExample();
        createStyledExample();
        createLazyExample();
        createComponentExample();
    }

    private void createBasicExample() {
        VirtualList<String> virtualList = new VirtualList<>();
        virtualList.setId("basic-virtual-list");
        virtualList.setHeight("200px");
        List<String> items = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> "Item " + i)
                .toList();
        virtualList.setItems(items);
        virtualList.setRenderer(new ComponentRenderer<>(item -> {
            Span span = new Span(item);
            span.addClassName("virtual-list-item");
            return span;
        }));
        addExample("Basic example", virtualList);
    }

    private void createEmptyExample() {
        VirtualList<String> virtualList = new VirtualList<>();
        virtualList.setId("empty-virtual-list");
        virtualList.setHeight("100px");
        addExample("Empty example", virtualList);
    }

    private void createStyledExample() {
        VirtualList<String> virtualList = new VirtualList<>();
        virtualList.setId("styled-virtual-list");
        virtualList.addClassName("custom-virtual-list");
        virtualList.setHeight("150px");
        List<String> items = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> "Styled " + i)
                .toList();
        virtualList.setItems(items);
        virtualList.setRenderer(new ComponentRenderer<>(item -> {
            Div div = new Div(new Span(item));
            div.addClassName("styled-item");
            return div;
        }));
        addExample("Styled example", virtualList);
    }

    private void createLazyExample() {
        VirtualList<String> virtualList = new VirtualList<>();
        virtualList.setId("lazy-virtual-list");
        virtualList.setHeight("200px");
        CallbackDataProvider<String, Void> dataProvider = DataProvider.fromCallbacks(
                query -> IntStream.rangeClosed(
                                query.getOffset() + 1,
                                query.getOffset() + query.getLimit())
                        .mapToObj(i -> "Lazy " + i),
                query -> 500
        );
        virtualList.setDataProvider(dataProvider);
        virtualList.setRenderer(new ComponentRenderer<>(item -> new Span(item)));
        addExample("Lazy example", virtualList);
    }

    private void createComponentExample() {
        VirtualList<String> virtualList = new VirtualList<>();
        virtualList.setId("component-virtual-list");
        virtualList.setHeight("200px");
        List<String> items = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> "Row " + i)
                .toList();
        virtualList.setItems(items);
        virtualList.setRenderer(new ComponentRenderer<>(item -> {
            Span label = new Span(item);
            Button button = new Button("Action " + item);
            return new HorizontalLayout(label, button);
        }));
        addExample("Component example", virtualList);
    }

    private void addExample(String title, Component component) {
        add(new H2(title), component);
    }
}
