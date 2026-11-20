package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridComponentRendererViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-component-renderer";
    }

    @Test
    public void testGetColumnTextsReadsComponentsOfReusedRows() {
        GridElement grid = GridElement.getById(page, "long-component-grid");
        // Read twice: the second pass starts scrolled to the end, so every page reuses row elements
        for (int pass = 0; pass < 2; pass++) {
            List<String> texts = grid.getColumnTexts(1);
            assertEquals(200, texts.size());
            for (int i = 0; i < texts.size(); i++) {
                assertEquals("Badge Last" + (i + 1), texts.get(i), "row " + i);
            }
        }
    }

    @Test
    public void testCellContentLocator() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, 2);
        assertTrue(cell.isPresent());
        assertThat(cell.get().getCellContentLocator()).isVisible();
    }

    @Test
    public void testFindButtonInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, "Action");
        assertTrue(cell.isPresent());
        Locator button = cell.get().getCellContentLocator().locator("vaadin-button");
        assertThat(button).isVisible();
        assertThat(button).hasText("Click First1");
    }

    @Test
    public void testClickButtonInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, "Action");
        assertTrue(cell.isPresent());
        Locator button = cell.get().getCellContentLocator().locator("vaadin-button");
        button.click();
        assertThat(page.locator("#click-output")).hasText("Clicked: First1");
    }

    @Test
    public void testButtonElementInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(1, "Action");
        assertTrue(cell.isPresent());
        ButtonElement button = new ButtonElement(
                cell.get().getCellContentLocator().locator("vaadin-button"));
        button.assertVisible();
        assertEquals("Click First2", button.getText());
    }

    // ── App Elements ──────────────────────────────────────────────────

    @Test
    public void testClickRowCoveredByAnAppElement() {
        GridElement grid = GridElement.getById(page, "app-element-grid");
        grid.findRow(0).orElseThrow().click();
        assertTrue(grid.findRow(0).orElseThrow().isSelected());
    }

    @Test
    public void testClickRowOfAGridInAShadowRoot() {
        GridElement grid = GridElement.getById(page, "shadow-root-grid");
        grid.findRow(0).orElseThrow().click();
        assertTrue(grid.findRow(0).orElseThrow().isSelected());
    }

    @Test
    public void testClickRowFailsWhenAnAppElementHoldsALinkOverTheCell() {
        GridElement grid = GridElement.getById(page, "app-element-link-grid");
        assertThrows(IllegalStateException.class, () -> grid.findRow(0).orElseThrow().click());
        assertFalse(grid.findRow(0).orElseThrow().isSelected());
    }

    // ── Component Header ──────────────────────────────────────────────

    @Test
    public void testHeaderCellLocatorWithComponent() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.findHeaderCell(2).get().getCellContentLocator();
        Locator filter = header.locator("vaadin-text-field");
        assertThat(filter).isVisible();
        assertThat(filter).hasAttribute("placeholder", "Filter email...");
    }

    @Test
    public void testHeaderCellLocatorByText() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.findHeaderCellByText("First Name").get().getCellContentLocator();
        assertThat(header).hasText("First Name");
    }

    @Test
    public void testInteractWithComponentHeader() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.findHeaderCell(2).get().getCellContentLocator();
        Locator filter = header.locator("vaadin-text-field");
        filter.locator("input").fill("test");
        assertThat(filter.locator("input")).hasValue("test");
    }
}
