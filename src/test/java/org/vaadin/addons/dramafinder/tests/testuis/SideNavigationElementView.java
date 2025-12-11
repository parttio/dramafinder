package org.vaadin.addons.dramafinder.tests.testuis;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Side Navigation Demo")
@Route(value = "side-navigation-element-view", layout = MainLayout.class)
public class SideNavigationElementView extends Main {

    public SideNavigationElementView() {
        SideNav nav = new SideNav("My App");
        nav.setId("default-nav");

        SideNavItem dashboard = new SideNavItem("Dashboard");
        dashboard.setPath("");
        nav.addItem(dashboard);

        SideNavItem messages = new SideNavItem("Messages");
        messages.setPath("messages");
        nav.addItem(messages);

        SideNavItem admin = new SideNavItem("Admin");
        admin.setPrefixComponent(VaadinIcon.COG.create());
        admin.setSuffixComponent(VaadinIcon.ACCORDION_MENU.create());

        SideNavItem users = new SideNavItem("Users");
        users.setPath("admin/users");

        SideNavItem roles = new SideNavItem("Roles");
        roles.setPath("admin/roles");

        admin.addItem(users, roles);
        nav.addItem(admin);

        // Disabled item
        SideNavItem reports = new SideNavItem("Reports");
        reports.setEnabled(false);
        nav.addItem(reports);

        add(nav);

        Span activeItemSpan = new Span();
        activeItemSpan.setId("active-item");
        add(activeItemSpan);
    }
}
