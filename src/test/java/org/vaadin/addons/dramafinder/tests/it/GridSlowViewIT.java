package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A grid whose pages take longer to load than the one second the grid helpers wait in the
 * browser, so they have to fall back to waiting with Playwright's timeout.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridSlowViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-slow";
    }

    @Test
    public void testFindRowWaitsForASlowPage() {
        GridElement grid = GridElement.getById(page, "slow-grid");
        assertEquals("First151", grid.findRow(150).orElseThrow().getCell(0).getText());
    }

    @Test
    public void testScrollToRowWaitsForASlowPage() {
        GridElement grid = GridElement.getById(page, "slow-grid");
        grid.scrollToRow(120);
        grid.assertRowInView(120);
        assertEquals("First121", grid.findRow(120).orElseThrow().getCell(0).getText());
    }

    @Test
    public void testGetColumnTextsResumesAfterASlowPage() {
        GridElement grid = GridElement.getById(page, "slow-grid");
        List<String> texts = grid.getColumnTexts(0);
        assertEquals(200, texts.size());
        for (int i = 0; i < texts.size(); i++) {
            assertEquals("First" + (i + 1), texts.get(i), "row " + i);
        }
    }
}
