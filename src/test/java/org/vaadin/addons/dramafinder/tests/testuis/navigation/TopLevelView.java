package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

/** {@code absolute = true}: the layout's {@code nav/} prefix is skipped. */
@Route(value = "top-level", layout = NavigationLayout.class, absolute = true)
public class TopLevelView extends Main {

    public TopLevelView() {
        add(new Span("Top level"));
    }
}
