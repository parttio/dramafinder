package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid LitRenderer Demo")
@Route(value = "grid-lit-renderer", layout = MainLayout.class)
public class GridLitRendererView extends Main {

    private final Span clickOutput = new Span();

    public GridLitRendererView() {
        createLitRendererGrid();
        clickOutput.setId("lit-click-output");
        add(clickOutput);
    }

    private void createLitRendererGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("lit-renderer-grid");
        grid.setHeight("400px");

        grid.addColumn(LitRenderer.<GridPerson>of(
                        "<span class='badge'>${item.firstName}</span>")
                .withProperty("firstName", GridPerson::firstName))
                .setHeader("First Name");

        grid.addColumn(GridPerson::lastName).setHeader("Last Name");

        grid.addColumn(LitRenderer.<GridPerson>of(
                        "<vaadin-button @click='${handleClick}'>Action ${item.firstName}</vaadin-button>")
                .withProperty("firstName", GridPerson::firstName)
                .withFunction("handleClick", person ->
                        clickOutput.setText("LitClicked: " + person.firstName())))
                .setHeader("Action");

        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("LitRenderer Grid"), grid);
    }
}
