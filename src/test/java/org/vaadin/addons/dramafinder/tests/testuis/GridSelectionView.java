package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Selection Demo")
@Route(value = "grid-selection", layout = MainLayout.class)
public class GridSelectionView extends Main {

    public GridSelectionView() {
        createSingleSelectGrid();
        createMultiSelectGrid();
    }

    private void createSingleSelectGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("single-select-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        grid.setItems(GridBasicView.generatePersons(20));
        add(new H2("Single Select Grid"), grid);
    }

    private void createMultiSelectGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("multi-select-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        grid.setItems(GridBasicView.generatePersons(20));
        add(new H2("Multi Select Grid"), grid);
    }
}
