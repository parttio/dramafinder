package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Component Renderer Demo")
@Route(value = "grid-component-renderer", layout = MainLayout.class)
public class GridComponentRendererView extends Main {

    private final Span clickOutput = new Span();

    public GridComponentRendererView() {
        createComponentGrid();
        createComponentHeaderGrid();
        clickOutput.setId("click-output");
        add(clickOutput);
    }

    private void createComponentHeaderGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("component-header-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");

        TextField filterField = new TextField();
        filterField.setPlaceholder("Filter email...");
        filterField.addClassName("email-filter");
        grid.addColumn(GridPerson::email).setHeader(filterField);

        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("Component Header Grid"), grid);
    }

    private void createComponentGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("component-grid");
        grid.setHeight("400px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(new ComponentRenderer<>(person -> {
            Button button = new Button("Click " + person.firstName());
            button.addClickListener(e -> clickOutput.setText("Clicked: " + person.firstName()));
            return button;
        })).setHeader("Action");
        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("Component Grid"), grid);
    }
}
