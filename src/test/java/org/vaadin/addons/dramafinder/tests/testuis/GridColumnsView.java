package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Columns Demo")
@Route(value = "grid-columns", layout = MainLayout.class)
public class GridColumnsView extends Main {

    public GridColumnsView() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("columns-grid");
        grid.setAllRowsVisible(true);
        grid.setColumnReorderingAllowed(true);
        grid.addColumn(GridPerson::firstName).setHeader("First Name").setId("first-name");
        var lastName = grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        lastName.setId("last-name");
        lastName.setVisible(false);
        // No header text: only findable by id
        grid.addColumn(GridPerson::email).setId("email");
        // A key stays on the server; no id in the browser
        grid.addColumn(person -> "key " + person.firstName()).setHeader("Key Only").setKey("key-only");
        grid.setItems(GridBasicView.generatePersons(5));
        add(new H2("Columns Grid"), grid);
    }
}
