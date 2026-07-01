package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Footer Demo")
@Route(value = "grid-footer", layout = MainLayout.class)
public class GridFooterView extends Main {

    public GridFooterView() {
        createFooterGrid();
    }

    private void createFooterGrid() {
        List<GridPerson> persons = GridBasicView.generatePersons(50);

        Grid<GridPerson> grid = new Grid<>();
        grid.setId("footer-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name")
                .setFooter("Total: " + persons.size());
        grid.addColumn(GridPerson::lastName).setHeader("Last Name")
                .setFooter("Last names");
        grid.addColumn(GridPerson::email).setHeader("Email")
                .setFooter("Emails");
        grid.setItems(persons);
        add(new H2("Footer Grid"), grid);
    }
}
