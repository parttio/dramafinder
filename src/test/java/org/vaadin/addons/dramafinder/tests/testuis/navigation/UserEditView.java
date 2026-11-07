package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

/** Template route {@code nav/user/:userId/edit}. */
@Route(value = "user/:userId/edit", layout = NavigationLayout.class)
public class UserEditView extends Main implements BeforeEnterObserver {

    private final Span user = new Span();

    public UserEditView() {
        user.setId("user");
        add(user);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        user.setText("Editing " + event.getRouteParameters().get("userId").orElse("?"));
    }
}
