package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Tree Grid Demo")
@Route(value = "tree-grid", layout = MainLayout.class)
public class TreeGridView extends Main {

    public TreeGridView() {
        createTwoLevelTreeGrid();
        createThreeLevelTreeGrid();
        createMultiSelectTwoLevelTreeGrid();
    }

    /**
     * A two-level tree:
     * <pre>
     * Root 0
     *   Child 0-0
     *   Child 0-1
     * Root 1
     *   Child 1-0
     *   Child 1-1
     *   Child 1-2
     * Root 2
     *   Child 2-0
     * </pre>
     * Initially only the 3 root items are visible.
     */
    private void createTwoLevelTreeGrid() {
        TreeData<String> data = new TreeData<>();
        data.addRootItems("Root 0", "Root 1", "Root 2");
        data.addItems("Root 0", "Child 0-0", "Child 0-1");
        data.addItems("Root 1", "Child 1-0", "Child 1-1", "Child 1-2");
        data.addItems("Root 2", "Child 2-0");

        TreeGrid<String> grid = new TreeGrid<>(new TreeDataProvider<>(data));
        grid.setId("two-level-tree-grid");
        grid.setHeight("400px");
        grid.addHierarchyColumn(s -> s).setHeader("Name");
        grid.addColumn(s -> s + " desc").setHeader("Description");

        add(new H2("Two-Level Tree Grid"), grid);
    }

    /**
     * A two-level tree with multi-select mode enabled (checkbox column at index 0,
     * hierarchy column at index 1). Same data as the plain two-level tree.
     * <pre>
     * Root 0
     *   Child 0-0
     *   Child 0-1
     * Root 1
     *   Child 1-0
     *   Child 1-1
     *   Child 1-2
     * Root 2
     *   Child 2-0
     * </pre>
     */
    private void createMultiSelectTwoLevelTreeGrid() {
        TreeData<String> data = new TreeData<>();
        data.addRootItems("Root 0", "Root 1", "Root 2");
        data.addItems("Root 0", "Child 0-0", "Child 0-1");
        data.addItems("Root 1", "Child 1-0", "Child 1-1", "Child 1-2");
        data.addItems("Root 2", "Child 2-0");

        TreeGrid<String> grid = new TreeGrid<>(new TreeDataProvider<>(data));
        grid.setId("multi-select-tree-grid");
        grid.setHeight("400px");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addHierarchyColumn(s -> s).setHeader("Name");
        grid.addColumn(s -> s + " desc").setHeader("Description");

        add(new H2("Multi-Select Two-Level Tree Grid"), grid);
    }

    /**
     * A three-level tree:
     * <pre>
     * Division A
     *   Dept A1
     *     Employee A1a
     *     Employee A1b
     *   Dept A2
     *     Employee A2a
     * Division B
     *   Dept B1
     *     Employee B1a
     * </pre>
     * Initially only the 2 division items are visible.
     */
    private void createThreeLevelTreeGrid() {
        TreeData<String> data = new TreeData<>();
        data.addRootItems("Division A", "Division B");
        data.addItems("Division A", "Dept A1", "Dept A2");
        data.addItems("Division B", "Dept B1");
        data.addItems("Dept A1", "Employee A1a", "Employee A1b");
        data.addItems("Dept A2", "Employee A2a");
        data.addItems("Dept B1", "Employee B1a");

        TreeGrid<String> grid = new TreeGrid<>(new TreeDataProvider<>(data));
        grid.setId("three-level-tree-grid");
        grid.setHeight("400px");
        grid.addHierarchyColumn(s -> s).setHeader("Name");
        grid.addColumn(s -> {
            if (s.startsWith("Division")) return "Division";
            if (s.startsWith("Dept")) return "Department";
            return "Employee";
        }).setHeader("Type");

        add(new H2("Three-Level Tree Grid"), grid);
    }
}
