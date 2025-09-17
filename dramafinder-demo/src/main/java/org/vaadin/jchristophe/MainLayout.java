package org.vaadin.jchristophe;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
        final DrawerToggle drawerToggle = new DrawerToggle();
        drawerToggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassName(LumoUtility.FontSize.LARGE);

        final SideNavItem textFieldItem = new SideNavItem("TextField", TextFieldView.class);
        final SideNavItem buttonItem = new SideNavItem("Button", ButtonView.class);
        final SideNavItem emailItem = new SideNavItem("EmailField", EmailFieldView.class);
        final SideNavItem integerItem = new SideNavItem("IntegerField", IntegerFieldView.class);
        final SideNav menuLayout = new SideNav();
        menuLayout.addItem(textFieldItem);
        menuLayout.addItem(buttonItem);
        menuLayout.addItem(emailItem);
        menuLayout.addItem(integerItem);
        addToDrawer(menuLayout);
        addToNavbar(true, drawerToggle, new Header(viewTitle));
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}