package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

/** No route value: Flow's naming convention makes it {@code nav/greeting}. */
@Route(layout = NavigationLayout.class)
public class GreetingView extends Main {

    public GreetingView() {
        add(new Span("Greeting"));
    }
}
