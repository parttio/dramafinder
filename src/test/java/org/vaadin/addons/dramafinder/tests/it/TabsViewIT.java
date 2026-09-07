package org.vaadin.addons.dramafinder.tests.it;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TabsElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TabsViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "tabs";
    }

    /** The page of the horizontal tabs that is currently shown. */
    private Locator visiblePage() {
        return page.locator("#order-content > div:not([hidden])");
    }

    @Test
    public void testTabsLookup() {
        TabsElement tabs = TabsElement.get(page);
        assertThat(tabs.getLocator()).isVisible();

        tabs.assertTabCount(3);
        assertEquals(3, tabs.getTabCount());
        assertThat(tabs.getTabs()).hasText(new String[] { "Details", "Payment", "Shipping" });

        assertEquals("Details", tabs.getTab(0).getLabel());
        assertEquals("Shipping", tabs.getTab("Shipping").getLabel());
    }

    @Test
    public void testSelectTabByLabel() {
        TabsElement tabs = TabsElement.getById(page, "order-tabs");

        tabs.assertSelectedTab("Details");
        assertEquals(0, tabs.getSelectedIndex());
        assertThat(visiblePage()).hasText("This is the details page");

        tabs.selectTab("Payment");

        tabs.assertSelectedTab("Payment");
        tabs.getTab("Details").assertNotSelected();
        assertEquals("Payment", tabs.getSelectedTab().getLabel());
        assertThat(visiblePage()).hasText("This is the payment page");
    }

    @Test
    public void testSelectTabByIndex() {
        TabsElement tabs = TabsElement.getById(page, "order-tabs");

        tabs.selectTab(2);

        tabs.assertSelectedTab(2);
        tabs.assertSelectedTab("Shipping");
        assertEquals(2, tabs.getSelectedIndex());
        assertThat(visiblePage()).hasText("This is the shipping page");
    }

    @Test
    public void testSelectedTabElement() {
        TabsElement tabs = TabsElement.getById(page, "order-tabs");

        TabElement selected = tabs.getSelectedTab();
        selected.assertSelected();
        assertEquals("Details", selected.getLabel());
    }

    @Test
    public void testOrientation() {
        TabsElement horizontal = TabsElement.getById(page, "order-tabs");
        assertEquals(TabsElement.ORIENTATION_HORIZONTAL, horizontal.getOrientation());
        horizontal.assertOrientation(TabsElement.ORIENTATION_HORIZONTAL);

        TabsElement vertical = TabsElement.getById(page, "direction-tabs");
        assertEquals(TabsElement.ORIENTATION_VERTICAL, vertical.getOrientation());
        vertical.assertOrientation(TabsElement.ORIENTATION_VERTICAL);
        vertical.assertTabCount(2);
    }

    @Test
    public void testThemeAndStyle() {
        TabsElement tabs = TabsElement.getById(page, "order-tabs");

        assertEquals("minimal", tabs.getTheme());
        tabs.assertTheme("minimal");
        tabs.assertCssClass("order-tabs");
    }
}
