package org.vaadin.addons.dramafinder.tests.it;

import java.time.Duration;
import java.util.List;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.TabElement;
import org.vaadin.addons.dramafinder.element.TabsElement;
import org.vaadin.addons.dramafinder.spring.SpringPlaywrightIT;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // ── Tab labels ─────────────────────────────────────────────────────

    @Test
    public void testTabLabels() {
        TabsElement tabs = TabsElement.getById(page, "order-tabs");
        assertEquals(List.of("Details", "Payment", "Shipping"), tabs.getTabLabels());
        tabs.assertTabLabels("Details", "Payment", "Shipping");
        assertThrows(AssertionError.class, () -> tabs.assertTabLabels("Details", "Shipping", "Payment"));
    }

    @Test
    public void testTabLabelsAreOwnTrimmedText() {
        TabsElement tabs = TabsElement.getById(page, "account-tabs");
        assertEquals(List.of("Profile", "Billing", "Security", "Messages"), tabs.getTabLabels());
        tabs.assertTabLabels("Profile", "Billing", "Security", "Messages");
        assertEquals("Messages", tabs.getTab(3).getLabel());
    }

        // ── Disabled tabs ──────────────────────────────────────────────────

    @Test
    public void testDisabledTab() {
        TabsElement tabs = TabsElement.getById(page, "account-tabs");
        TabElement billing = tabs.getTab("Billing");
        assertFalse(billing.isEnabled());
        billing.assertDisabled();
        tabs.getTab("Profile").assertEnabled();
        assertTrue(tabs.getTab("Profile").isEnabled());
    }

    @Test
    public void testSelectDisabledTabFailsAtOnce() {
        TabsElement tabs = TabsElement.getById(page, "account-tabs");
        // Well below Playwright's 15 s action timeout, which a click on the disabled tab ran into
        var e = assertTimeout(Duration.ofSeconds(10),
                () -> assertThrows(IllegalStateException.class, () -> tabs.selectTab("Billing")));
        assertTrue(e.getMessage().contains("Billing") && e.getMessage().contains("disabled"), e.getMessage());
        tabs.assertSelectedTab("Profile");
        assertThrows(IllegalStateException.class, () -> tabs.selectTab(1));
    }

    @Test
    public void testSelectTabOnceEnabled() {
        TabsElement tabs = TabsElement.getById(page, "account-tabs");
        ButtonElement.getByText(page, "Enable billing").click();
        tabs.getTab("Billing").assertEnabled();
        tabs.selectTab("Billing");
        tabs.assertSelectedTab("Billing");
    }

    @Test
    public void testSelectTabByEveryListedLabel() {
        TabsElement tabs = TabsElement.getById(page, "labelled-tabs");
        // own text wins over the aria-label; an icon-only tab reads as its aria-label
        tabs.assertTabLabels("Inbox", "Archive", "Settings");
        for (String label : tabs.getTabLabels()) {
            tabs.selectTab(label);
            tabs.assertSelectedTab(label);
            assertEquals(label, tabs.getSelectedTab().getLabel());
        }
        tabs.selectTab("Inbox");
        tabs.assertSelectedTab(0);
        tabs.selectTab("Settings");
        tabs.assertSelectedTab(2);
        // the aria-label of a tab that has a text does not find it
        assertThat(tabs.getTab("Inbox, 3 unread").getLocator()).hasCount(0);
    }
}
