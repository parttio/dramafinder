package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("SplitLayout Demo")
@Route(value = "splitlayout", layout = MainLayout.class)
public class SplitLayoutView extends Main {

    public SplitLayoutView() {
        // Default horizontal split
        SplitLayout horizontal = new SplitLayout(new Div(new Div("Primary 1")), new Div(new Div("Secondary 1")));
        add(horizontal);

        // Vertical split
        SplitLayout vertical = new SplitLayout(new Div(new Div("Primary V")), new Div(new Div("Secondary V")));
        vertical.setOrientation(Orientation.VERTICAL);
        add(vertical);

        // Pre-positioned splitter
        SplitLayout positioned = new SplitLayout(new Div(new Div("Primary P")), new Div(new Div("Secondary P")));
        positioned.setSplitterPosition(70);
        add(positioned);
    }
}

