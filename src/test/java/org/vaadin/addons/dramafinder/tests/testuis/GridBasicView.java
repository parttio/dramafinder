package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.List;
import java.util.stream.IntStream;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Basic Demo")
@Route(value = "grid-basic", layout = MainLayout.class)
public class GridBasicView extends Main {

    public GridBasicView() {
        createBasicGrid();
        createEmptyGrid();
        createAllRowsVisibleGrid();
        createLazyGrid();
        createUndefinedSizeLazyGrid();
    }

    private void createBasicGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("basic-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        grid.setItems(generatePersons(100));
        grid.addClassName("styled-grid");
        add(new H2("Basic Grid"), grid);
    }

    private void createEmptyGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("empty-grid");
        grid.setHeight("200px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        add(new H2("Empty Grid"), grid);
    }

    private void createAllRowsVisibleGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("all-rows-visible-grid");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        grid.setItems(generatePersons(5));
        grid.setAllRowsVisible(true);
        add(new H2("All Rows Visible Grid"), grid);
    }

    private void createLazyGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("lazy-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        CallbackDataProvider<GridPerson, Void> dataProvider = DataProvider.fromCallbacks(
                query -> IntStream.rangeClosed(
                                query.getOffset() + 1,
                                query.getOffset() + query.getLimit())
                        .mapToObj(i -> new GridPerson("First" + i, "Last" + i, "person" + i + "@example.com")),
                query -> 10000
        );
        grid.setDataProvider(dataProvider);
        add(new H2("Lazy Grid"), grid);
    }

    private void createUndefinedSizeLazyGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("undefined-size-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        GridLazyDataView<GridPerson> dataView = grid.setItems(query ->
                IntStream.rangeClosed(
                                query.getOffset() + 1,
                                query.getOffset() + query.getLimit())
                        .mapToObj(i -> new GridPerson("First" + i, "Last" + i, "person" + i + "@example.com")));
        dataView.setItemCountUnknown();
        add(new H2("Undefined Size Lazy Grid"), grid);
    }

    static List<GridPerson> generatePersons(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new GridPerson("First" + i, "Last" + i, "person" + i + "@example.com"))
                .toList();
    }
}
