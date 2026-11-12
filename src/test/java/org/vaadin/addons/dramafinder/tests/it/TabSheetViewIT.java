package org.vaadin.addons.dramafinder.tests.it;

import java.util.List;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TabSheetElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TabSheetViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "tabsheet";
    }

    @Test
    public void testTabs() {
        TabSheetElement tabs = TabSheetElement.get(page);
        assertThat(tabs.getLocator()).isVisible();
        tabs.assertTabsCount(3);

        TabElement tab1 = tabs.getTab("Tab 1");
        tab1.assertSelected();

        TabElement tab2 = tabs.getTab("Tab 2");
        tab2.assertNotSelected();

        Locator page1 = page.locator("div").getByText("This is page 1");
        assertThat(page1).isVisible();
        assertThat(tabs.getContentLocator()).hasText("This is page 1");

        Locator page2 = page.locator("div").getByText("This is page 2");
        assertThat(page2).isHidden();

        tabs.selectTab("Tab 2");
        tab1.assertNotSelected();
        tab2.assertSelected();
        assertThat(tabs.getContentLocator()).hasText("This is page 2");

        assertThat(page1).isHidden();
        assertThat(page2).isVisible();
    }

    @Test
    public void testTabLabels() {
        TabSheetElement tabs = TabSheetElement.get(page);
        assertEquals(List.of("Tab 1", "Tab 2", "Tab 3"), tabs.getTabLabels());
        tabs.assertTabLabels("Tab 1", "Tab 2", "Tab 3");
    }

    @Test
    public void testSelectDisabledTabFails() {
        TabSheetElement tabs = TabSheetElement.getById(page, "disabled-tabsheet");
        tabs.getTab("Locked").assertDisabled();
        var e = assertThrows(IllegalStateException.class, () -> tabs.selectTab("Locked"));
        assertTrue(e.getMessage().contains("Locked"), e.getMessage());
        tabs.assertSelectedTab("Open");
        assertThat(tabs.getContentLocator()).hasText("Open page");
    }

    @Test
    public void testNestedTabSheetContent() {
        TabSheetElement outer = TabSheetElement.getById(page, "outer-tabsheet");
        outer.assertTabsCount(2);
        outer.assertTabLabels("Outer 1", "Outer 2");
        assertThat(outer.getContentLocator()).hasCount(1);
        assertThat(outer.getContentLocator()).containsText("Outer page 1");

        TabSheetElement inner = TabSheetElement.getById(page, "inner-tabsheet");
        assertThat(inner.getContentLocator()).hasText("Inner page 1");
        inner.selectTab("Inner 2");
        assertThat(inner.getContentLocator()).hasText("Inner page 2");
        outer.assertSelectedTab("Outer 1");

        outer.selectTab("Outer 2");
        assertThat(outer.getContentLocator()).hasText("Outer page 2");
    }
}
