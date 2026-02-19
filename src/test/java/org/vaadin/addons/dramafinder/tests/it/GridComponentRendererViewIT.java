package org.vaadin.addons.dramafinder.tests.it;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.GridElement;

import com.microsoft.playwright.Locator;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridComponentRendererViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-component-renderer";
    }

    @Test
    public void testCellContentLocator() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, 2);
        assertTrue(cell.isPresent());
        assertThat(cell.get().getCellContent()).isVisible();
    }

    @Test
    public void testFindButtonInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, "Action");
        assertTrue(cell.isPresent());
        Locator button = cell.get().getCellContent().locator("vaadin-button");
        assertThat(button).isVisible();
        assertThat(button).hasText("Click First1");
    }

    @Test
    public void testClickButtonInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, "Action");
        assertTrue(cell.isPresent());
        Locator button = cell.get().getCellContent().locator("vaadin-button");
        button.click();
        assertThat(page.locator("#click-output")).hasText("Clicked: First1");
    }

    @Test
    public void testButtonElementInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        var cell = grid.findCell(0, "Action");
        assertTrue(cell.isPresent());
        ButtonElement button = new ButtonElement(
                cell.get().getCellContent().locator("vaadin-button"));
        button.assertVisible();
        assertThat(button.getLocator()).hasText("Click First2");
    }

    // ── Component Header ──────────────────────────────────────────────

    @Test
    public void testHeaderCellLocatorWithComponent() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.findHeaderCell(2).get().getCellContent();
        Locator filter = header.locator("vaadin-text-field");
        assertThat(filter).isVisible();
        assertThat(filter).hasAttribute("placeholder", "Filter email...");
    }

    @Test
    public void testHeaderCellLocatorByText() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.findHeaderCellByText("First Name").get().getCellContent();
        assertThat(header).hasText("First Name");
    }

    @Test
    public void testInteractWithComponentHeader() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.findHeaderCell(2).get().getCellContent();
        Locator filter = header.locator("vaadin-text-field");
        filter.locator("input").fill("test");
        assertThat(filter.locator("input")).hasValue("test");
    }
}
