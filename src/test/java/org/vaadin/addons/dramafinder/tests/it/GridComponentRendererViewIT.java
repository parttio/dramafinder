package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.GridElement;
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
        Locator cellContent = grid.getCellContentLocator(0, 2);
        assertThat(cellContent).isVisible();
    }

    @Test
    public void testFindButtonInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        Locator cellContent = grid.getCellContentLocator(0, "Action");
        Locator button = cellContent.locator("vaadin-button");
        assertThat(button).isVisible();
        assertThat(button).hasText("Click First1");
    }

    @Test
    public void testClickButtonInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        Locator cellContent = grid.getCellContentLocator(0, "Action");
        Locator button = cellContent.locator("vaadin-button");
        button.click();
        assertThat(page.locator("#click-output")).hasText("Clicked: First1");
    }

    @Test
    public void testButtonElementInCell() {
        GridElement grid = GridElement.getById(page, "component-grid");
        Locator cellContent = grid.getCellContentLocator(1, "Action");
        ButtonElement button = new ButtonElement(
                cellContent.locator("vaadin-button"));
        button.assertVisible();
        assertThat(button.getLocator()).hasText("Click First2");
    }

    // ── Component Header ──────────────────────────────────────────────

    @Test
    public void testHeaderCellLocatorWithComponent() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.getHeaderCellLocator(2);
        Locator filter = header.locator("vaadin-text-field");
        assertThat(filter).isVisible();
        assertThat(filter).hasAttribute("placeholder", "Filter email...");
    }

    @Test
    public void testHeaderCellLocatorByText() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.getHeaderCellLocator("First Name");
        assertThat(header).hasText("First Name");
    }

    @Test
    public void testInteractWithComponentHeader() {
        GridElement grid = GridElement.getById(page, "component-header-grid");
        Locator header = grid.getHeaderCellLocator(2);
        Locator filter = header.locator("vaadin-text-field");
        filter.locator("input").fill("test");
        assertThat(filter.locator("input")).hasValue("test");
    }
}
