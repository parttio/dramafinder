package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Context Menu Demo")
@Route(value = "contextmenu", layout = MainLayout.class)
public class ContextMenuView extends Main {

    public ContextMenuView() {
        addFileContextMenu();
        addStatusContextMenu();
    }

    private void addFileContextMenu() {
        Div target = new Div(new Text("Right click to open the file menu"));
        target.setId("file-target");
        target.getStyle().set("padding", "var(--lumo-space-m)").set("border", "1px dashed var(--lumo-contrast-50pct)");

        Span selection = new Span();
        selection.setId("file-selection");

        ContextMenu contextMenu = new ContextMenu(target);
        contextMenu.addClassName("file-context-menu");
        contextMenu.addItem("Open", e -> selection.setText("Open"));
        contextMenu.addItem("Rename", e -> selection.setText("Rename"));
        contextMenu.addItem("Delete", e -> selection.setText("Delete"));
        MenuItem share = contextMenu.addItem("Share");
        share.getSubMenu().addItem("Copy link", e -> selection.setText("Copy link"));
        share.getSubMenu().addItem("Send via email", e -> selection.setText("Send via email"));

        add(new H2("File context menu"), target, selection);
    }

    private void addStatusContextMenu() {
        Div target = new Div(new Text("Right click to manage status"));
        target.setId("status-target");
        target.getStyle().set("padding", "var(--lumo-space-m)").set("border", "1px dashed var(--lumo-contrast-50pct)");

        Span status = new Span();
        status.setId("status-selection");

        ContextMenu statusMenu = new ContextMenu(target);
        statusMenu.addItem("Refresh", e -> status.setText("Refresh"));
        statusMenu.addItem("Archive", e -> status.setText("Archive"));
        MenuItem disabledAction = statusMenu.addItem("Disabled action", e -> status.setText("Disabled"));
        disabledAction.setEnabled(false);

        add(new H2("Status context menu"), target, status);
    }
}
