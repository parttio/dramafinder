package org.vaadin.addons.dramafinder.tests.testuis;

import java.util.stream.IntStream;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Grid Slow Data Demo")
@Route(value = "grid-slow", layout = MainLayout.class)
public class GridSlowView extends Main {

    /** How long each page fetch takes: longer than the grid helpers' one-second in-browser wait. */
    static final long FETCH_DELAY_MS = 1500;

    public GridSlowView() {
        Grid<GridPerson> grid = new Grid<>();
        grid.setId("slow-grid");
        grid.setHeight("300px");
        grid.setPageSize(50);
        grid.addColumn(GridPerson::firstName).setHeader("First Name");
        CallbackDataProvider<GridPerson, Void> dataProvider = DataProvider.fromCallbacks(
                query -> {
                    // Only pages after the first are slow, so the view itself opens quickly
                    if (query.getOffset() > 0) {
                        sleep();
                    }
                    return IntStream.rangeClosed(query.getOffset() + 1,
                                    Math.min(query.getOffset() + query.getLimit(), 200))
                            .mapToObj(i -> new GridPerson("First" + i, "Last" + i, "person" + i + "@example.com"));
                },
                query -> 200
        );
        grid.setDataProvider(dataProvider);
        add(new H2("Slow Grid"), grid);
    }

    private static void sleep() {
        try {
            Thread.sleep(FETCH_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
