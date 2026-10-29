package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.function.Function;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

/**
 * Grids whose first cell holds a link that navigates away, so clicking the cell
 * removes the grid from the DOM.
 */
@PageTitle("Grid Navigation Demo")
@Route(value = "grid-navigation", layout = MainLayout.class)
public class GridNavigationView extends Main {

    public GridNavigationView() {
        add(new H2("Router Link Grid"), createGrid("router-link-grid", person -> {
            RouterLink link = new RouterLink(person.firstName(), GridNavigationTargetView.class);
            return fillCell(link);
        }));
        add(new H2("Reload Link Grid"), createGrid("reload-link-grid", person -> {
            Anchor link = new Anchor(GridNavigationTargetView.ROUTE, person.firstName());
            // Full page load instead of client-side navigation
            link.getElement().setAttribute("router-ignore", true);
            return fillCell(link);
        }));
    }

    private Grid<GridPerson> createGrid(String id, Function<GridPerson, Component> linkFactory) {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId(id);
        grid.setHeight("300px");
        grid.addColumn(new ComponentRenderer<>(linkFactory::apply)).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.setItems(GridBasicView.generatePersons(10));
        return grid;
    }

    // Make the link cover the whole cell, so a click on the cell lands on it
    private static Component fillCell(Component link) {
        link.getElement().getStyle().set("display", "block").set("width", "100%");
        return link;
    }
}
