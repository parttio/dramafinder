package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/**
 * An optional parameter and two aliases, like Bakery's storefront (issue #318):
 * {@code nav/storefront/:orderID?}, {@code nav/storefront/:orderID/edit} and
 * {@code nav}.
 */
@Route(value = "storefront/:orderID?", layout = NavigationLayout.class)
@RouteAlias(value = "storefront/:orderID/edit", layout = NavigationLayout.class)
@RouteAlias(value = "", layout = NavigationLayout.class)
public class StorefrontView extends Main implements BeforeEnterObserver {

    private final Span order = new Span();

    public StorefrontView() {
        order.setId("order");
        add(order);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        order.setText("Order " + event.getRouteParameters().get("orderID").orElse("-"));
    }
}
