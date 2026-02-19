package org.vaadin.addons.dramafinder.tests.it;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridDetailsViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-details";
    }

    @Test
    public void testDetailsInitiallyClosed() {
        GridElement grid = GridElement.getById(page, "details-grid");
        assertFalse(grid.findRow(0).get().isDetailsOpen());
    }

    @Test
    public void testOpenDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        var row = grid.findRow(0);
        assertTrue(row.isPresent());
        row.get().openDetails();

        assertTrue(row.get().isDetailsOpen());
    }

    @Test
    public void testCloseDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        var row = grid.findRow(0);
        assertTrue(row.isPresent());
        row.get().openDetails();

        assertTrue(row.get().isDetailsOpen());

        row.get().closeDetails();
        assertFalse(row.get().isDetailsOpen());
    }

    @Test
    public void testOpenDifferentRowDetails() {
        GridElement grid = GridElement.getById(page, "details-grid");

        var row = grid.findRow(0);
        assertTrue(row.isPresent());
        row.get().openDetails();
        assertTrue(row.get().isDetailsOpen());

        // In default SINGLE selection, clicking row 1 moves selection and details

        var row2 = grid.findRow(1);
        assertTrue(row2.isPresent());   
        row2.get().openDetails();
        assertTrue(row2.get().isDetailsOpen());
        // Row 0 details are closed because selection moved
        assertFalse(row.get().isDetailsOpen());
    }
}
