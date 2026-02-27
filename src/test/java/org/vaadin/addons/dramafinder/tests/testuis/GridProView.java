package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.List;
import java.util.stream.IntStream;

import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Pro Demo")
@Route(value = "grid-pro", layout = MainLayout.class)
public class GridProView extends Main {

    public GridProView() {
        createBasicGridPro();
        createEditOnClickGridPro();
        createSingleCellEditGridPro();
        createEnterNextRowGridPro();
    }

    private void createBasicGridPro() {
        GridPro<GridProPerson> grid = new GridPro<>();
        grid.setId("basic-grid-pro");
        grid.setHeight("400px");
        grid.addEditColumn(GridProPerson::getFirstName)
                .text((person, value) -> person.setFirstName(value))
                .setHeader("First Name");
        grid.addColumn(GridProPerson::getLastName).setHeader("Last Name");
        grid.addEditColumn(GridProPerson::isActive)
                .checkbox((person, value) -> person.setActive(value))
                .setHeader("Active");
        grid.addEditColumn(GridProPerson::getDepartment)
                .select((person, value) -> person.setDepartment(value), "Engineering", "Marketing", "Sales")
                .setHeader("Department");
        grid.setItems(generatePersons(10));
        add(new H2("Basic Grid Pro"), grid);
    }

    private void createEditOnClickGridPro() {
        GridPro<GridProPerson> grid = new GridPro<>();
        grid.setId("edit-on-click-grid-pro");
        grid.setHeight("300px");
        grid.setEditOnClick(true);
        grid.addEditColumn(GridProPerson::getFirstName)
                .text((person, value) -> person.setFirstName(value))
                .setHeader("First Name");
        grid.addEditColumn(GridProPerson::isActive)
                .checkbox((person, value) -> person.setActive(value))
                .setHeader("Active");
        grid.setItems(generatePersons(5));
        add(new H2("Edit on Click Grid Pro"), grid);
    }

    private void createSingleCellEditGridPro() {
        GridPro<GridProPerson> grid = new GridPro<>();
        grid.setId("single-cell-edit-grid-pro");
        grid.setHeight("300px");
        grid.setSingleCellEdit(true);
        grid.addEditColumn(GridProPerson::getFirstName)
                .text((person, value) -> person.setFirstName(value))
                .setHeader("First Name");
        grid.addEditColumn(GridProPerson::getLastName)
                .text((person, value) -> person.setLastName(value))
                .setHeader("Last Name");
        grid.setItems(generatePersons(5));
        add(new H2("Single Cell Edit Grid Pro"), grid);
    }

    private void createEnterNextRowGridPro() {
        GridPro<GridProPerson> grid = new GridPro<>();
        grid.setId("enter-next-row-grid-pro");
        grid.setHeight("300px");
        grid.setEnterNextRow(true);
        grid.addEditColumn(GridProPerson::getFirstName)
                .text((person, value) -> person.setFirstName(value))
                .setHeader("First Name");
        grid.setItems(generatePersons(5));
        add(new H2("Enter Next Row Grid Pro"), grid);
    }

    private static List<GridProPerson> generatePersons(int count) {
        String[] departments = {"Engineering", "Marketing", "Sales"};
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new GridProPerson(
                        "First" + i,
                        "Last" + i,
                        i % 2 == 0,
                        departments[i % departments.length]))
                .toList();
    }
}
