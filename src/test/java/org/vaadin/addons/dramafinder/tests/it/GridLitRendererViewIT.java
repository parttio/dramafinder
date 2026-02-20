package org.vaadin.addons.dramafinder.tests.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.GridElement;

import com.microsoft.playwright.Locator;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GridLitRendererViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "grid-lit-renderer";
    }

    @Test
    public void testLitRendererCellText() {
        GridElement grid = GridElement.getById(page, "lit-renderer-grid");
        var cellContent = grid.findCell(0, 0).get().getCellContent();
        assertEquals("First1", cellContent.innerText());
    }

    @Test
    public void testLitRendererBadgeElement() {
        GridElement grid = GridElement.getById(page, "lit-renderer-grid");
        var cellContent = grid.findCell(0, "First Name").get().getCellContent();
        Locator badge = cellContent.locator(".badge");
        assertThat(badge).isVisible();
        assertThat(badge).hasText("First1");
    }

    @Test
    public void testLitRendererButtonClick() {
        GridElement grid = GridElement.getById(page, "lit-renderer-grid");
        var cellContent = grid.findCell(0, "Action").get().getCellContent();
        Locator button = cellContent.locator("vaadin-button");
        assertThat(button).hasText("Action First1");
        button.click();
        assertThat(page.locator("#lit-click-output")).hasText("LitClicked: First1");
    }

    @Test
    public void testLitRendererSecondRow() {
        GridElement grid = GridElement.getById(page, "lit-renderer-grid");
        var cellContent = grid.findCell(1, "First Name").get().getCellContent();
        Locator badge = cellContent.locator(".badge");
        assertThat(badge).hasText("First2");
    }
}
