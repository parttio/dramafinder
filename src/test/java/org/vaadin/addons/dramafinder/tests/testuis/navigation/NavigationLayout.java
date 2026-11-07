package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLayout;
import org.vaadin.addons.dramafinder.tests.testuis.MainLayout;

/** Prefixes its routes with {@code nav/}; see {@code RouteNavigationIT}. */
@RoutePrefix("nav")
@ParentLayout(MainLayout.class)
public class NavigationLayout extends Div implements RouterLayout {
}
