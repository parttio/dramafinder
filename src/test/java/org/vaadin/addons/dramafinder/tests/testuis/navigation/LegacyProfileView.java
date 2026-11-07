package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

/** Forwards to {@link ProfileView}, the way a guard or a login redirect would. */
@Route(value = "legacy-profile", layout = NavigationLayout.class)
public class LegacyProfileView extends Main implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.forwardTo(ProfileView.class);
    }
}
