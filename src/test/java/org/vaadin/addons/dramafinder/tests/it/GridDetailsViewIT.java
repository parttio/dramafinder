package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridDetailsViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-details";
    }

    @Test
    public void testDetailsInitiallyClosed() {
        GridElement grid = GridElement.getById(page, "details-grid");
        assertFalse(grid.isDetailsOpen(0));
        grid.assertDetailsClosed(0);
    }

    @Test
    public void testOpenDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        grid.openDetails(0);

        grid.assertDetailsOpen(0);
        assertTrue(grid.isDetailsOpen(0));
    }

    @Test
    public void testCloseDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        grid.openDetails(0);
        grid.assertDetailsOpen(0);

        // Click again to close
        grid.openDetails(0);
        grid.assertDetailsClosed(0);
    }

    @Test
    public void testOpenDifferentRowDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        grid.openDetails(0);
        grid.assertDetailsOpen(0);

        // In default SINGLE selection, clicking row 1 moves selection and details
        grid.openDetails(1);
        grid.assertDetailsOpen(1);
        // Row 0 details are closed because selection moved
        grid.assertDetailsClosed(0);
    }
}
