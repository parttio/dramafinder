package org.vaadin.addons.dramafinder.tests.testuis.navigation;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

/** {@code nav/profile}; a button reopens it with {@code ?tab=2&note=a b}. */
@Route(value = "profile", layout = NavigationLayout.class)
public class ProfileView extends Main {

    public ProfileView() {
        Button tab = new Button("Open tab 2", e -> getUI().ifPresent(ui -> ui.navigate(ProfileView.class,
                QueryParameters.simple(java.util.Map.of("tab", "2", "note", "a b")))));
        add(new Span("Profile"), tab);
    }
}
