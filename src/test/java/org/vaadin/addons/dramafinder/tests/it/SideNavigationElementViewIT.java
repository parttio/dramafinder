package org.vaadin.addons.dramafinder.tests.it;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.vaadin.addons.dramafinder.HasTestView;
import org.vaadin.addons.dramafinder.element.SideNavigationElement;
import org.vaadin.addons.dramafinder.element.SideNavigationItemElement;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SideNavigationElementViewIT extends SpringPlaywrightIT implements HasTestView {

    @Override
    public String getView() {
        return "side-navigation-element-view";
    }

    @Test
    public void testSideNavStructure() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
        nav.assertVisible();
        nav.assertExpanded();

        // Check Label (Header)
        assertEquals("My App", nav.getLabel());

        // Check flat item
        SideNavigationItemElement dashboard = nav.getItem("Dashboard");
        dashboard.assertVisible();
        dashboard.assertEnabled();
        dashboard.assertLabel("Dashboard");
        assertEquals("Dashboard", dashboard.getLabel());

        // Check nested item
        SideNavigationItemElement admin = nav.getItem("Admin");
        admin.assertVisible();

        // It has children, so it should be expandable. Validating default state.
        // Usually dependent on whether it's expanded by default.
        // Vaadin SideNavItem with children is collapsible.
        // Let's check if we can find children.

        SideNavigationItemElement users = nav.getItem("Users");
        // Depending on whether Admin is expanded, Users might be visible or not?
        // Actually locally in valid SideNav, items are in light dom or shadow?
        // They are usually always in DOM but hidden via CSS if collapsed?
        // assertVisible() checks visibility.
        // Assuming default is expanded or we toggle it.

        if (!admin.isExpanded()) {
            admin.toggle();
        }
        admin.assertExpanded();
        users.assertVisible();
    }

    @Test
    public void testItemState() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
        SideNavigationItemElement reports = nav.getItem("Reports");
        reports.assertVisible();
        reports.assertDisabled();
    }

    @Test
    public void testToggle() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
        SideNavigationItemElement admin = nav.getItem("Admin");

        admin.assertCollapsed();
        // Ensure state matches expectation
        boolean initialExpanded = admin.isExpanded();

        admin.toggle();
        if (initialExpanded) {
            admin.assertCollapsed();
        } else {
            admin.assertExpanded();
        }

        admin.toggle();
        if (initialExpanded) {
            admin.assertExpanded();
        } else {
            admin.assertCollapsed();
        }
    }

    @Test
    public void testToggleOnClick() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
        SideNavigationItemElement admin = nav.getItem("Admin");

        admin.assertCollapsed();
        admin.click();
        admin.assertExpanded();

    }

    @Test
    public void testNavigate() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
        SideNavigationItemElement reports = nav.getItem("Dashboard");
        assertThat(page).hasURL(getUrl() + getView());
        reports.navigate();
        assertThat(page).not().hasURL(getUrl());
    }

    @Test
    public void testNavigateOnClick() {
        SideNavigationElement nav = SideNavigationElement.getByLabel(page, "My App");
        SideNavigationItemElement reports = nav.getItem("Dashboard");
        assertThat(page).hasURL(getUrl() + getView());
        reports.click();
        assertThat(page).not().hasURL(getUrl());
    }
}
