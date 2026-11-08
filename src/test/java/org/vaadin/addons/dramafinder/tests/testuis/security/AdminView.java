package org.vaadin.addons.dramafinder.tests.testuis.security;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

/** Reachable with the {@code ADMIN} role only (see {@code TestSecurityConfiguration}). */
@Route("security/admin")
public class AdminView extends Main {

    public AdminView() {
        add(new Span("Admin area"));
    }
}
