package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Target of the links in {@link GridNavigationView}. Holds a grid of its own, so
 * a generic {@code vaadin-grid} locator still resolves after the navigation.
 */
@PageTitle("Grid Navigation Target")
@Route(value = GridNavigationTargetView.ROUTE, layout = MainLayout.class)
public class GridNavigationTargetView extends Main {

    public static final String ROUTE = "grid-navigation-target";

    public GridNavigationTargetView() {
        H2 title = new H2("Navigation target");
        title.setId("navigation-target");
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("target-grid");
        grid.setHeight("300px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.setItems(GridBasicView.generatePersons(3));
        add(title, grid);
    }
}
