package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Component Renderer Demo")
@Route(value = "grid-component-renderer", layout = MainLayout.class)
public class GridComponentRendererView extends Main {

    private final Span clickOutput = new Span();

    public GridComponentRendererView() {
        createComponentGrid();
        createComponentHeaderGrid();
        createLongComponentGrid();
        createAppElementGrid("app-element-grid", person -> new Span(person.firstName()));
        createAppElementGrid("app-element-link-grid", person -> coverCell(new Anchor("#", person.firstName())));
        createShadowRootGrid();
        clickOutput.setId("click-output");
        add(clickOutput);
    }

    // A grid in an app element's shadow root, as a Lit template's
    private void createShadowRootGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("shadow-root-grid");
        grid.setHeight("300px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("Shadow Root Grid"), new PersonCard(grid));
    }

    // Each cell covered, padding included, by an app's own element holding its content in its
    // shadow root, as a Lit template does
    private void createAppElementGrid(String id, SerializableFunction<GridPerson, Component> content) {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId(id);
        grid.setHeight("300px");
        grid.addColumn(new ComponentRenderer<>(person -> coverCell(new PersonCard(content.apply(person)))))
                .setHeader("Person");
        grid.setItems(GridBasicView.generatePersons(10));
        add(new H2("App Element Grid"), grid);
    }

    private static Component coverCell(Component component) {
        component.getElement().getStyle().set("display", "block").set("margin", "-50px").set("padding", "50px");
        return component;
    }

    @Tag("person-card")
    private static class PersonCard extends Component {
        PersonCard(Component content) {
            getElement().attachShadow().appendChild(content.getElement());
        }
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

    // Enough rows that reading a column jumps through several pages, reusing row elements
    private void createLongComponentGrid() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("long-component-grid");
        grid.setHeight("300px");
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        grid.addColumn(new ComponentRenderer<>(person -> new Span("Badge " + person.lastName())))
                .setHeader("Badge");
        grid.setItems(GridBasicView.generatePersons(200));
        add(new H2("Long Component Grid"), grid);
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
