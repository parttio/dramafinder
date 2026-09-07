package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Tabs Demo")
@Route(value = "tabs", layout = MainLayout.class)
public class TabsView extends Main {

    public TabsView() {
        add(createHorizontalTabs());
        add(createVerticalTabs());
    }

    private Component createHorizontalTabs() {
        Map<Tab, Component> pages = new LinkedHashMap<>();
        pages.put(new Tab("Details"), new Div(new Text("This is the details page")));
        pages.put(new Tab("Payment"), new Div(new Text("This is the payment page")));
        pages.put(new Tab("Shipping"), new Div(new Text("This is the shipping page")));

        Tabs tabs = new Tabs(pages.keySet().toArray(new Tab[0]));
        tabs.setId("order-tabs");
        tabs.addClassName("order-tabs");
        tabs.getElement().setAttribute("theme", "minimal");

        Div content = new Div();
        content.setId("order-content");
        pages.values().forEach(content::add);
        showSelectedPage(pages, tabs.getSelectedTab());

        tabs.addSelectedChangeListener(event -> showSelectedPage(pages, event.getSelectedTab()));

        return new Div(tabs, content);
    }

    private Component createVerticalTabs() {
        Tabs tabs = new Tabs(new Tab("North"), new Tab("South"));
        tabs.setId("direction-tabs");
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private void showSelectedPage(Map<Tab, Component> pages, Tab selectedTab) {
        pages.forEach((tab, page) -> page.setVisible(tab.equals(selectedTab)));
    }
}
