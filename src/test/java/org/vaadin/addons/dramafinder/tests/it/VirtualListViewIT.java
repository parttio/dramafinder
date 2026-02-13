package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.VirtualListElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VirtualListViewIT extends SpringPlaywrightIT {

    @Override
    public String getView() {
        return "virtual-list";
    }

    @Test
    public void testTitle() {
        assertThat(page).hasTitle("VirtualList Demo");
    }

    @Test
    public void testRowCount() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.assertVisible();
        virtualList.assertRowCount(100);
    }

    @Test
    public void testEmptyList() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#empty-virtual-list"));
        virtualList.assertVisible();
        virtualList.assertEmpty();
        assertEquals(0, virtualList.getRowCount());
    }

    @Test
    public void testFirstVisibleRow() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.assertFirstVisibleRow(0);
    }

    @Test
    public void testRowInView() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.assertRowInView(0);
        virtualList.assertRowNotInView(99);
        assertTrue(virtualList.isRowInView(0));
        assertFalse(virtualList.isRowInView(99));
    }

    @Test
    public void testScrollToRow() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.assertRowNotInView(50);

        virtualList.scrollToRow(50);

        virtualList.assertRowInView(50);
        virtualList.assertRowNotInView(0);
    }

    @Test
    public void testScrollToStart() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.scrollToRow(50);
        virtualList.assertRowNotInView(0);

        virtualList.scrollToStart();

        virtualList.assertFirstVisibleRow(0);
        virtualList.assertRowInView(0);
    }

    @Test
    public void testScrollToEnd() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.assertRowNotInView(99);

        virtualList.scrollToEnd();

        virtualList.assertRowInView(99);
        virtualList.assertRowNotInView(0);
    }

    @Test
    public void testVisibleRowCount() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        int visibleCount = virtualList.getVisibleRowCount();
        assertTrue(visibleCount > 0, "Should have at least one visible row");
        assertTrue(visibleCount < 100,
                "Should not show all 100 rows in a 200px container");
    }

    @Test
    public void testGetItemByText() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.assertItemRendered("Item 1");
        assertThat(virtualList.getItemByText("Item 1")).isVisible();
    }

    @Test
    public void testGetItemByIndex() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        assertThat(virtualList.getItemByIndex(0)).isVisible();
        assertThat(virtualList.getItemByIndex(0)).hasText("Item 1");
    }

    @Test
    public void testGetRenderedItems() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        int renderedCount = virtualList.getRenderedItems().count();
        assertTrue(renderedCount > 0, "Should have rendered items");
    }

    @Test
    public void testScrollAndReadItem() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#basic-virtual-list"));
        virtualList.scrollToRow(50);
        virtualList.assertItemRendered("Item 51");
        assertThat(virtualList.getItemByText("Item 51")).isVisible();
    }

    @Test
    public void testGetStaticFactory() {
        VirtualListElement virtualList = VirtualListElement.get(page);
        virtualList.assertVisible();
        virtualList.assertRowCount(100);
    }

    @Test
    public void testHasClass() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#styled-virtual-list"));
        virtualList.assertCssClass("custom-virtual-list");
    }

    @Test
    public void testLazyRowCount() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#lazy-virtual-list"));
        virtualList.assertVisible();
        virtualList.assertRowCount(500);
    }

    @Test
    public void testGetItemComponentByIndex() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#component-virtual-list"));
        ButtonElement button = virtualList.getItemComponent(0, ButtonElement.class);
        button.assertVisible();
        assertThat(button.getLocator()).hasText("Action Row 1");
    }

    @Test
    public void testGetItemComponentByText() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#component-virtual-list"));
        ButtonElement button = virtualList.getItemComponentByText(
                "Row 3", ButtonElement.class);
        button.assertVisible();
        assertThat(button.getLocator()).hasText("Action Row 3");
    }

    @Test
    public void testGetComponent() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#component-virtual-list"));
        ButtonElement button = virtualList.getComponent(ButtonElement.class);
        button.assertVisible();
    }

    @Test
    public void testScrollAndGetItemComponent() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#component-virtual-list"));
        virtualList.scrollToRow(20);
        virtualList.assertItemRendered("Row 21");
        ButtonElement button = virtualList.getItemComponentByText(
                "Row 21", ButtonElement.class);
        button.assertVisible();
        assertThat(button.getLocator()).hasText("Action Row 21");
    }

    @Test
    public void testStyledListRowCount() {
        VirtualListElement virtualList = new VirtualListElement(
                page.locator("#styled-virtual-list"));
        virtualList.assertRowCount(50);
    }
}
