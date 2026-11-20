package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Sorting Demo")
@Route(value = "grid-sorting", layout = MainLayout.class)
public class GridSortingView extends Main {

    public GridSortingView() {
        createSortableGrid();
        createFilterGrid();
        createGroupedGrid();
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

    // Three header rows: a joined "Name" row on top, the default row with the sorters,
    // and an appended row holding a filter field per column
    private void createFilterGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("filter-grid");
        grid.setHeight("400px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        var firstName = grid.addColumn(GridPerson::firstName).setHeader("First Name").setSortable(true);
        var lastName = grid.addColumn(GridPerson::lastName).setHeader("Last Name").setSortable(true);
        lastName.setId("last-name-column");
        ListDataProvider<GridPerson> dataProvider = new ListDataProvider<>(GridBasicView.generatePersons(20));
        grid.setItems(dataProvider);

        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(firstName).setComponent(createFilter("first-filter", "First name filter",
                text -> dataProvider.setFilter(p -> p.firstName().contains(text))));
        filterRow.getCell(lastName).setComponent(createFilter("last-filter", "Last name filter",
                text -> dataProvider.setFilter(p -> p.lastName().contains(text))));
        grid.prependHeaderRow().join(List.of(firstName, lastName).toArray(Grid.Column[]::new)).setText("Name");
        add(new H2("Filter Grid"), grid);
    }

    // Five sortable columns under a header row that joins the two middle ones into "Name":
    // the group row has four cells, so its cell positions are not column indexes
    private void createGroupedGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("grouped-grid");
        grid.setHeight("300px");
        grid.addColumn(p -> p.firstName().substring(5)).setHeader("Id").setSortable(true);
        var firstName = grid.addColumn(GridPerson::firstName).setHeader("First Name").setSortable(true);
        var lastName = grid.addColumn(GridPerson::lastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(GridPerson::email).setHeader("Email").setSortable(true);
        grid.addColumn(p -> "City" + p.lastName().substring(4)).setHeader("City").setSortable(true);
        grid.setItems(GridBasicView.generatePersons(20));
        grid.prependHeaderRow().join(firstName, lastName).setText("Name");
        add(new H2("Grouped Grid"), grid);
    }

    private static TextField createFilter(String id, String ariaLabel, java.util.function.Consumer<String> filter) {
        TextField field = new TextField();
        field.setId(id);
        field.setAriaLabel(ariaLabel);
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.addValueChangeListener(e -> filter.accept(e.getValue()));
        return field;
    }
}
