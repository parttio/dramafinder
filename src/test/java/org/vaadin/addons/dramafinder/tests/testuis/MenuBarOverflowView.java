package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * A menu bar as wide as the page: at a phone-sized viewport its trailing items
 * (Share, Print) collapse into the overflow ("More options") menu.
 */
@PageTitle("MenuBar Overflow Demo")
@Route(value = "menubar-overflow", layout = MainLayout.class)
public class MenuBarOverflowView extends Main {

    private final Text selected = new Text("");
    private final ComponentEventListener<ClickEvent<MenuItem>> listener =
            e -> selected.setText(e.getSource().getText());

    public MenuBarOverflowView() {
        MenuBar menuBar = new MenuBar();
        menuBar.setId("overflow-menu-bar");
        menuBar.setWidthFull();
        for (String label : new String[] {"File", "Edit", "View", "Insert", "Format"}) {
            menuBar.addItem(label, listener);
        }
        MenuItem share = menuBar.addItem("Share");
        share.getSubMenu().addItem("By email", listener);
        share.getSubMenu().addItem("Get Link", listener);
        menuBar.addItem("Print", listener);

        Div message = new Div(new Text("Clicked item: "), selected);
        message.setId("text");
        add(menuBar, message);
    }
}
