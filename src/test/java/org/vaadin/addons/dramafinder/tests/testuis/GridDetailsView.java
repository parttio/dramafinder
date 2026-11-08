package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Details Demo")
@Route(value = "grid-details", layout = MainLayout.class)
public class GridDetailsView extends Main {

    private final Span buttonClicks = new Span("Button clicks: 0");
    private final Span itemClicked = new Span();
    private final Span itemDoubleClicked = new Span();
    private int buttonClickCount;

    public GridDetailsView() {
        createDetailsGrid();
        createSingleColumnGrid();
        createButtonGrid();
        createMultiSelectGrid();
        createNoDetailsOnClickGrid();
        createClickGrid();
        buttonClicks.setId("button-clicks");
        itemClicked.setId("item-clicked");
        itemDoubleClicked.setId("item-double-clicked");
        add(buttonClicks, itemClicked, itemDoubleClicked);
    }

    private void createDetailsGrid() {
        Grid<GridPerson> grid = createGrid("details-grid");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(GridPerson::lastName).setHeader("Last Name");
        grid.addColumn(GridPerson::email).setHeader("Email");
        add(new H2("Details Grid"), grid);
    }

    private void createSingleColumnGrid() {
        Grid<GridPerson> grid = createGrid("single-column-details-grid");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        add(new H2("Single Column Details Grid"), grid);
    }

    // The second column holds a button: a click on it must not be mistaken for a row click
    private void createButtonGrid() {
        Grid<GridPerson> grid = createGrid("button-details-grid");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(new ComponentRenderer<>(person -> fillCell(createCountingButton())))
                .setHeader("Action");
        add(new H2("Button Details Grid"), grid);
    }

    private void createMultiSelectGrid() {
        Grid<GridPerson> grid = createGrid("multi-select-details-grid");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        add(new H2("Multi Select Details Grid"), grid);
    }

    private void createNoDetailsOnClickGrid() {
        Grid<GridPerson> grid = createGrid("no-details-on-click-grid");
        grid.setDetailsVisibleOnClick(false);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        add(new H2("No Details On Click Grid"), grid);
    }

    // Multi selection, a button in the first data column, and item click listeners
    private void createClickGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("click-grid");
        grid.setHeight("300px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(new ComponentRenderer<>(person -> createCountingButton())).setHeader("Action");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addItemClickListener(e -> itemClicked.setText("Clicked: " + e.getItem().firstName()));
        grid.addItemDoubleClickListener(
                e -> itemDoubleClicked.setText("Double-clicked: " + e.getItem().firstName()));
        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("Click Grid"), grid);
    }

    private Grid<GridPerson> createGrid(String id) {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId(id);
        grid.setHeight("300px");
        grid.setItemDetailsRenderer(new ComponentRenderer<>(person -> {
            Span span = new Span("Details: " + person.email());
            span.addClassName("detail-content");
            return span;
        }));
        grid.setDetailsVisibleOnClick(true);
        grid.setItems(GridBasicView.generatePersons(10));
        return grid;
    }

    private Button createCountingButton() {
        return new Button("Edit", e -> buttonClicks.setText("Button clicks: " + ++buttonClickCount));
    }

    // Make the button cover the cell content, so a click in the middle of the cell lands on it
    private static Button fillCell(Button button) {
        button.setWidthFull();
        return button;
    }
}
