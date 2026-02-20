package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Details Demo")
@Route(value = "grid-details", layout = MainLayout.class)
public class GridDetailsView extends Main {

    public GridDetailsView() {
        createDetailsGrid();
    }

    private void createDetailsGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("details-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        grid.setItemDetailsRenderer(new com.vaadin.flow.data.renderer.ComponentRenderer<>(
                person -> {
                    Span span = new Span("Details: " + person.email());
                    span.addClassName("detail-content");
                    return span;
                }));
        grid.setDetailsVisibleOnClick(true);
        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("Details Grid"), grid);
    }
}
