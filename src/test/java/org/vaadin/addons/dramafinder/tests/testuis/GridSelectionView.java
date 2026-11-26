package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridI18n;
import com.vaadin.flow.component.grid.GridSingleSelectionModel;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Selection Demo")
@Route(value = "grid-selection", layout = MainLayout.class)
public class GridSelectionView extends Main {

    public GridSelectionView() {
        createSingleSelectGrid();
        createMultiSelectGrid();
        createNoSelectionGrid();
        createNoDeselectGrid();
        createI18nGrid();
        createLazyMultiSelectGrid();
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

    private void createNoSelectionGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("no-select-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.setItems(GridBasicView.generatePersons(5));
        add(new H2("No Selection Grid"), grid);
    }

    private void createNoDeselectGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("no-deselect-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        ((GridSingleSelectionModel<GridPerson>) grid.getSelectionModel()).setDeselectAllowed(false);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.setItems(GridBasicView.generatePersons(5));
        add(new H2("No Deselect Grid"), grid);
    }

    /** A multi-select grid with translated accessible names for its checkboxes and sorters. */
    private void createI18nGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("i18n-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(GridPerson::firstName).setHeader("First Name").setSortable(true);
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.setItems(GridBasicView.generatePersons(5));
        grid.setI18n(new GridI18n().setSelectAll("Alle auswählen").setSelectRow("Zeile {rowHeader} auswählen")
                .setSorter("Sortieren nach {column}"));
        add(new H2("Translated Grid"), grid);
    }

    /** A multi-select grid over a lazy data provider: it hides the select-all checkbox. */
    private void createLazyMultiSelectGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("lazy-multi-select-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        var persons = GridBasicView.generatePersons(5);
        grid.setItems(DataProvider.fromCallbacks(
                query -> persons.stream().skip(query.getOffset()).limit(query.getLimit()),
                query -> persons.size()));
        add(new H2("Lazy Multi Select Grid"), grid);
    }
}
