package org.vaadin.addons.dramafinderdemo;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Tabs Demo")
@Route(value = "tabsheet", layout = MainLayout.class)
public class TabSheetView extends Main {

    public TabSheetView() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Tab 1", new Div(new Text("This is page 1")));
        tabSheet.add("Tab 2", new Div(new Text("This is page 2")));
        tabSheet.add("Tab 3", new Div(new Text("This is page 3")));
        add(tabSheet);
    }
}
