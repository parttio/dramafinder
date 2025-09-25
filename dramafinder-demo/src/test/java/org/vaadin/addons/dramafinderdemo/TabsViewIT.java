package org.vaadin.addons.dramafinderdemo;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TabSheetElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TabsViewIT extends SpringPlaywrightIT implements HasTestView {

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
}
