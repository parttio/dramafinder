package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Sorting Demo")
@Route(value = "grid-sorting", layout = MainLayout.class)
public class GridSortingView extends Main {

    public GridSortingView() {
        createSortableGrid();
    }

    private void createSortableGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("sortable-grid");
        grid.setHeight("400px");
        grid.setMultiSort(true);
        grid.addColumn(GridPerson::firstName).setHeader("First Name").setSortable(true);
        grid.addColumn(GridPerson::lastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(GridPerson::email).setHeader("Email").setSortable(true);
        grid.setItems(GridBasicView.generatePersons(50));
        add(new H2("Sortable Grid"), grid);
    }
}
