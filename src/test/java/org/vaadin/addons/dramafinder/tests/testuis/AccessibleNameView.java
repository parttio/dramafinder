package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * One component per source of an accessible name, each with an id, for
 * {@code getAccessibleName()} and {@code assertAccessibleName(..)}.
 */
@PageTitle("Accessible Name Demo")
@Route(value = "accessible-name", layout = MainLayout.class)
public class AccessibleNameView extends Main {

    public AccessibleNameView() {
        TextField labelled = new TextField("Name");
        labelled.setId("label-field");

        TextField ariaLabelled = new TextField();
        ariaLabelled.setAriaLabel("Search");
        ariaLabelled.setPlaceholder("Type to search");
        ariaLabelled.setId("aria-label-field");

        Span billing = new Span("Billing address");
        billing.setId("billing-heading");
        TextField referenced = new TextField();
        referenced.setAriaLabelledBy("billing-heading");
        referenced.setId("labelledby-field");

        TextField unnamed = new TextField();
        unnamed.setPlaceholder("Street");
        unnamed.setId("unnamed-field");

        Span first = new Span("Shipping");
        first.setId("ref-first");
        Span second = new Span("  options ");
        second.setId("ref-second");
        Div multiRef = new Div("content");
        multiRef.getElement().setAttribute("aria-labelledby", "ref-first ref-second");
        multiRef.setId("multi-ref");

        Checkbox terms = new Checkbox("Accept terms");
        terms.setId("terms");

        Select<String> size = new Select<>();
        size.setItems("S", "M");
        size.setPlaceholder("Choose size");
        size.setId("placeholder-select");

        Button save = new Button("Save");
        save.setTooltipText("Saves the form");
        save.setId("save-button");
        Button close = new Button(VaadinIcon.CLOSE.create());
        close.setAriaLabel("Close");
        close.setId("close-button");
        Button iconOnly = new Button(VaadinIcon.COG.create());
        iconOnly.setId("unnamed-button");

        Tab home = new Tab("Home");
        home.setId("home-tab");
        Tabs tabs = new Tabs(home, new Tab("Settings"));

        Button rename = new Button("Rename", e -> labelled.setLabel("Full name"));
        rename.setId("rename");

        // Vaadin forwards a grid's accessibleName and accessibleNameRef to its internal <table>.
        Grid<String> users = new Grid<>();
        users.addColumn(s -> s).setHeader("User");
        users.setItems("Ada", "Linus");
        users.setAriaLabel("Users");
        users.setId("aria-label-grid");

        Span teamHeading = new Span("Team members");
        teamHeading.setId("team-heading");
        Grid<String> team = new Grid<>();
        team.addColumn(s -> s).setHeader("Member");
        team.setItems("Grace");
        team.setAriaLabelledBy("team-heading");
        team.setId("labelledby-grid");

        Grid<String> unnamedGrid = new Grid<>();
        unnamedGrid.addColumn(s -> s).setHeader("Item");
        unnamedGrid.setItems("One");
        unnamedGrid.setId("unnamed-grid");

        TreeGrid<String> folders = new TreeGrid<>();
        folders.addHierarchyColumn(s -> s).setHeader("Folder");
        folders.setItems(List.of("Documents"), folder -> List.of());
        folders.setAriaLabel("Folders");
        folders.setId("aria-label-tree-grid");

        add(new Div(labelled, ariaLabelled, billing, referenced, unnamed), new Div(first, second, multiRef),
                new Div(terms, size), new Div(save, close, iconOnly, rename), tabs,
                new Div(users, teamHeading, team, unnamedGrid, folders));
    }
}
